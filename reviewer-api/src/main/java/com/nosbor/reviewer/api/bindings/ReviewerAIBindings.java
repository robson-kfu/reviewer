package com.nosbor.reviewer.api.bindings;

import com.nosbor.reviewer.api.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@Slf4j
public class ReviewerAIBindings {

    @Bean
    Function<Message<PullRequestContextTO>, Message<AIResponseTO>> requestIaRevision() {
        return pullRequestContext -> {
            log.info("Requisitando analise da IA para o PR {}", pullRequestContext.getPayload());
            // Recuperar Diff do MR
            AIResponseTO iaResponse = new AIResponseTO();

            iaResponse.setPullRequestId(pullRequestContext.getPayload().getPullRequestId());

            log.info("Reposta da IA {}", iaResponse);
            return MessageBuilder.withPayload(iaResponse).copyHeaders(pullRequestContext.getHeaders()).build();
        };
    }
}
