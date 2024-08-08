package com.nosbor.reviewer.api.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nosbor.reviewer.api.helpers.ValidationHelper;
import com.nosbor.reviewer.api.models.AIResponseWrapper;
import com.nosbor.reviewer.api.models.OllamaResponseTO;
import com.nosbor.reviewer.api.models.PullRequestContextTO;
import com.nosbor.reviewer.api.services.IAIService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.nosbor.reviewer.api.models.AIAvailableServicesEnum.OLLAMA;

@Service
@Slf4j
public class OllamaServiceImpl implements IAIService {

    public static final String AI_SERVICES_OLLAMA_BASE_URL = "ai.services.ollama.baseUrl";
    private final WebClient client;
    private final String baseUrl;
    private final ObjectMapper objectMapper;
    public OllamaServiceImpl(@Value("${" + AI_SERVICES_OLLAMA_BASE_URL + ":}") String baseUrl,
                             ObjectMapper objectMapper) {
        this.baseUrl = baseUrl;
        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public AIResponseWrapper getAIReview(PullRequestContextTO pullRequestContextTO) {
        log.info("Iniciando analise da PR {}", pullRequestContextTO.getPullRequestId());

        Mono<String> responseString = client.post()
                .uri("/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body(pullRequestContextTO.getDiff()))
                .retrieve()
                .bodyToFlux(OllamaResponseTO.class)
                .map(OllamaResponseTO::getResponse)
                .collect(Collectors.joining());
        String response = responseString.block();
        AIResponseWrapper aiResponseWrapper = objectMapper.readValue(response, AIResponseWrapper.class);
        aiResponseWrapper.setPullRequestId(pullRequestContextTO.getPullRequestId());
        log.info("Comentários gerados. Tamanho {}.", aiResponseWrapper.getComments().size());
        return aiResponseWrapper;
    }

    @Override
    public void validate() {
        log.info("Validando as configurações do git hub");
        ValidationHelper.validate(Map.of(
                this.baseUrl, AI_SERVICES_OLLAMA_BASE_URL
        ), OLLAMA.toString());
        log.info("Configurações do git hub estão OK!");
    }

    private String body(String diff) {
        return """
                {
                  "model": "phi3",
                  "format": "json",
                  "prompt": "%s",
                  "system": "%s",
                  "stream": true,
                  "options": {
                                "temperature": 0
                             }
                }
                """
                .formatted(StringEscapeUtils.escapeJson(diff), StringEscapeUtils.escapeJson(getSystemMessage()));
    }


    private String getSystemMessage() {
        //TODO - devo externalizar para reaproveitar?
        return """
                Context:
                You are an AI designed to review code and provide insightful, constructive feedback.
                Task:
                Please analyze the provided diff delimited by single triple quotes, considering the following aspects:
                1. Code Quality and Readability:
                   - Are variable names descriptive and follow naming conventions?
                   - Is the code well-structured and easy to follow?
                   - Are there any redundant or overly complex sections that could be simplified?
                2. Functionality and Correctness:
                   - Does the code correctly implement the specified functionality?
                   - Are there any potential bugs or logical errors?
                   - Are edge cases handled appropriately?
                3. Testing:
                   - Are there sufficient unit tests and integration tests?
                   - Do the tests effectively cover the critical paths and edge cases?
                   - Are the tests well-written and maintainable?
                4. Performance and Optimization:
                   - Are there any performance bottlenecks or inefficient code sections?
                   - Can any part of the code be optimized for better performance?
                5. Best Practices and Standards:
                   - Does the code adhere to industry best practices and standards?
                   - Are there any security considerations that need attention?
                   - Is the use of external libraries and frameworks appropriate and efficient?
                6. Response:
                   - Just generates comments that demands changes in the code.
                Your response must be in JSON, following the format:
                {
                  "comments": [
                    {
                      "line": number,
                      "comment": string
                     },
                     {
                      "line": number,
                      "comment": string
                     }
                   ]
                }
                where:
                line: the number of the line where the comment must be done.
                comment: Your comment about the change.
                """;
    }

}
