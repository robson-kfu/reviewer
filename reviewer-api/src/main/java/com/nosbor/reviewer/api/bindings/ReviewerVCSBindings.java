package com.nosbor.reviewer.api.bindings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nosbor.reviewer.api.models.*;
import com.nosbor.reviewer.api.services.IVCSService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.Function;

@Component
@Slf4j
public class ReviewerVCSBindings {

    private final ApplicationContext applicationContext;
    private final ObjectMapper objectMapper;

    public ReviewerVCSBindings(ApplicationContext applicationContext, ObjectMapper objectMapper) {
        this.applicationContext = applicationContext;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Bean
    Function<Message<RequestRevisionTO>, Message<PullRequestContextTO>> requestContext() {
        return mergeRevision -> {
            log.info("Processando requisição de revisão de PR {}", mergeRevision);
            RequestRevisionTO requestRevision = mergeRevision.getPayload();
            IVCSService iVCSService = getIvscService(requestRevision.getVcs());
            PullRequestContextTO context = objectMapper.convertValue(requestRevision, PullRequestContextTO.class);

            context.setDiff(iVCSService.getPullRequestDiff(requestRevision));

            context.setContext(iVCSService.getPullRequestContext(requestRevision));
            log.info("Finalizando recuperação dos diffs.");
            return MessageBuilder.withPayload(context).copyHeaders(mergeRevision.getHeaders()).build();
        };
    }

    @Bean
    Function<Message<AIResponseWrapper>, Message<ProcessStatusTO>> returnComments() {
        return aiResponseWrapperMessage -> {
            log.info("Preparando envio dos comentários da IA para o VSC.");

            AIResponseWrapper aiResponseWrapper = aiResponseWrapperMessage.getPayload();
            IVCSService iVCSService = getIvscService(aiResponseWrapper.getVcs());
            iVCSService.comment(aiResponseWrapper);

            return MessageBuilder.withPayload(
                            ProcessStatusTO.builder()
                                    .pullRequestId(aiResponseWrapperMessage.getPayload().getPullRequestId())
                                    .message("Processo finalizado!")
                                    .status(StatusEnum.FINALIZADO)
                                    .createdAt(LocalDate.now())
                                    .build()
                    )
                    .copyHeaders(aiResponseWrapperMessage.getHeaders()).build();
        };
    }

    private @NotNull IVCSService getIvscService(VCSAvailableServicesEnum vcsAvailableServicesEnum) {
        IVCSService iVCSService = applicationContext.getBean(vcsAvailableServicesEnum.getService());
        iVCSService.validate();
        return iVCSService;
    }
}
