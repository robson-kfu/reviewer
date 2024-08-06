package com.nosbor.reviewer.api.services.impl;

import com.nosbor.reviewer.api.helpers.ValidationHelper;
import com.nosbor.reviewer.api.models.AIResponseTO;
import com.nosbor.reviewer.api.models.AIResponseWrapper;
import com.nosbor.reviewer.api.models.PullRequestContextTO;
import com.nosbor.reviewer.api.services.IAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static com.nosbor.reviewer.api.models.AIAvailableServicesEnum.OLLAMA;

@Service
@Slf4j
public class AIServiceImpl implements IAIService {

    public static final String AI_SERVICES_OLLAMA_BASE_URL = "ai.services.ollama.baseUrl";
    private final WebClient client;
    private final String baseUrl;

    public AIServiceImpl(@Value("${" + AI_SERVICES_OLLAMA_BASE_URL + ":}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public AIResponseWrapper getAIReview(PullRequestContextTO pullRequestContextTO) {
        log.info("Iniciando analise da PR {}", pullRequestContextTO.getPullRequestId());

        String prompt = formatPrompt(pullRequestContextTO);

        AIResponseWrapper aiResponseWrapper = new AIResponseWrapper();
        aiResponseWrapper.setPullRequestId(pullRequestContextTO.getPullRequestId());

        List<AIResponseTO> aiResponseTOS = client.post()
                .uri("/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_NDJSON)
                .bodyValue("{\"prompt\": \"" + prompt + "\"}")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AIResponseTO>>() {
                })
                .block();
        aiResponseWrapper.setAiResponseTOList(aiResponseTOS);
        log.info("Comentários gerados. Tamanho {}.", aiResponseWrapper.getAiResponseTOList().size());
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

    private String formatPrompt(PullRequestContextTO pullRequestContextTO) {
        //TODO - devo externalizar para reaproveitar?
        String template = """
                **Context:**
                You are an AI designed to review code and provide insightful, constructive feedback.
               
                **Task:**
                Please analyze the provided diff delimited by single triple quotes, considering the following aspects:

                1. **Code Quality and Readability:**
                   - Are variable names descriptive and follow naming conventions?
                   - Is the code well-structured and easy to follow?
                   - Are there any redundant or overly complex sections that could be simplified?

                2. **Functionality and Correctness:**
                   - Does the code correctly implement the specified functionality?
                   - Are there any potential bugs or logical errors?
                   - Are edge cases handled appropriately?

                3. **Testing:**
                   - Are there sufficient unit tests and integration tests?
                   - Do the tests effectively cover the critical paths and edge cases?
                   - Are the tests well-written and maintainable?

                4. **Performance and Optimization:**
                   - Are there any performance bottlenecks or inefficient code sections?
                   - Can any part of the code be optimized for better performance?

                5. **Best Practices and Standards:**
                   - Does the code adhere to industry best practices and standards?
                   - Are there any security considerations that need attention?
                   - Is the use of external libraries and frameworks appropriate and efficient?
                **Response**:
                Your response must be in JSON, following the format:
                [
                    {
                        "line": number,
                        "comment": string
                    }
                ]
                where:
                line: the number of the line where the comment must be done.
                comment: Your comment about the change.
                ```
                 %s
                ```
                """;
        return template.formatted(pullRequestContextTO.getDiff());
    }

}
