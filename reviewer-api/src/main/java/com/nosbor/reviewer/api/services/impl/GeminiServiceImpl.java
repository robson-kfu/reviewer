package com.nosbor.reviewer.api.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

import static com.nosbor.reviewer.api.helpers.Constants.COMMENTS;
import static com.nosbor.reviewer.api.helpers.Constants.SYSTEM_INSTRUCTIONS;
import static com.nosbor.reviewer.api.models.AIAvailableServicesEnum.OLLAMA;

@Service
@Slf4j
@ConditionalOnProperty(
        name = "ai.service.active",
        havingValue = "gemini"
)
public class GeminiServiceImpl implements IAIService {

    private final ObjectMapper objectMapper;
    private final WebClient client;
    private final String geminiKey;

    public GeminiServiceImpl(@Value("${ai.service.gemini.baseUrl}") String baseUrl,
                             @Value("${ai.service.gemini.key}") String key,
                             ObjectMapper objectMapper) {
        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.objectMapper = objectMapper;
        this.geminiKey = key;
    }


    @SneakyThrows
    @Override
    public AIResponseWrapper getAIReview(PullRequestContextTO pullRequestContextTO) {
        log.info("Iniciando analise da PR {}", pullRequestContextTO.getPullRequestId());

        Mono<String> responseString = client.post()
                .uri(":streamGenerateContent?alt=sse&key=%s".formatted(geminiKey))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body(pullRequestContextTO.getDiff()))
                .retrieve()
                .bodyToFlux(OllamaResponseTO.class)
                .map(OllamaResponseTO::getResponse)
                .collect(Collectors.joining());
        String response = responseString.block();
        JsonNode jsonNode = objectMapper.readValue(response, JsonNode.class);
        AIResponseWrapper aiResponseWrapper = objectMapper.convertValue(pullRequestContextTO, AIResponseWrapper.class);
        aiResponseWrapper.setComments(objectMapper.convertValue(jsonNode.get(COMMENTS), new TypeReference<>() {
        }));
        log.info("Comentários gerados. Tamanho {}.",
                aiResponseWrapper.getComments().isEmpty() ? 0 : aiResponseWrapper.getComments().size());
        return aiResponseWrapper;
    }

    @Override
    public String getEmbeddings(PullRequestContextTO pullRequestContextTO) {
        return IAIService.super.getEmbeddings(pullRequestContextTO);
    }

    @Override
    public void validate() {
       // Do nothing
    }

    private static String body(String diff) {
        return """
                {
                    "system_instruction": {
                        "parts": [
                            {
                                "text": "%s"
                            }
                        ]
                    },
                    "contents": [
                        {
                            "parts": [
                                {
                                    "text": "%s"
                                }
                            ]
                        }
                    ]
                }"""
                .formatted(StringEscapeUtils.escapeJson(SYSTEM_INSTRUCTIONS), StringEscapeUtils.escapeJson(diff));
    }
}
