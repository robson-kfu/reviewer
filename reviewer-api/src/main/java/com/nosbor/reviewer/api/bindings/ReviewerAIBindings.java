package com.nosbor.reviewer.api.bindings;

import com.nosbor.reviewer.api.models.AIAvailableServicesEnum;
import com.nosbor.reviewer.api.models.AIResponseWrapper;
import com.nosbor.reviewer.api.models.PullRequestContextTO;
import com.nosbor.reviewer.api.services.IAIService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Slf4j
public class ReviewerAIBindings {

    private final ApplicationContext applicationContext;

    public ReviewerAIBindings(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    @Bean
    Function<Message<PullRequestContextTO>, Message<AIResponseWrapper>> requestIaRevision() {
        return pullRequestContext -> {
            log.info("Requisitando analise da IA para o PR {}", pullRequestContext.getPayload().getPullRequestId());
            PullRequestContextTO pullRequestContextTO = pullRequestContext.getPayload();
            IAIService aiService = getAiService(pullRequestContextTO.getAiRevisor());
            AIResponseWrapper aiReview = aiService.getAIReview(pullRequestContextTO);

            log.info("Processo de analise da IA finalizado.");
            return MessageBuilder.withPayload(aiReview).copyHeaders(pullRequestContext.getHeaders()).build();
        };
    }

    private @NotNull IAIService getAiService(AIAvailableServicesEnum aiAvailableServicesEnum) {
        return applicationContext.getBean(aiAvailableServicesEnum.getService());
    }
}
