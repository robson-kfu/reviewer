package com.nosbor.reviewer.api.bindings;

import com.nosbor.reviewer.api.models.*;
import com.nosbor.reviewer.api.services.IVSCService;
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

    public ReviewerVCSBindings(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    Function<Message<RequestRevisionTO>, Message<PullRequestContextTO>> requestContext() {
        return mergeRevision -> {
            log.info("Processando requisição de revisão de PR {}", mergeRevision);
            RequestRevisionTO requestRevision = mergeRevision.getPayload();

            IVSCService ivscService = getIvscService(requestRevision);
            ivscService.validate();
            String pullRequestId = requestRevision.getPullRequestId();

            PullRequestContextTO context = new PullRequestContextTO();
            context.setAiAvailableServicesEnum(requestRevision.getAiRevisor());
            try {
                context.setDiff(ivscService.getPullRequestDiff(requestRevision));
            } catch (Exception e) {
                log.error("Erro buscando diff {}", e.getMessage());
                //TODO - Escrever no topico de feedback?
                throw new RuntimeException(e);
            }
            context.setContext(ivscService.getPullRequestContext(requestRevision));
            context.setPullRequestId(pullRequestId);

            log.info("Finalizando recuperação dos diffs.");
            return MessageBuilder.withPayload(context).copyHeaders(mergeRevision.getHeaders()).build();
        };
    }

    @Bean
    Function<Message<AIResponseWrapper>, Message<ProcessStatusTO>> returnComments() {
        return aiResponseWrapperMessage -> {
            log.info("Preparando envio dos comentários da IA para o VSC.");
            // monta comentários com base na revisão da IA
//            CommentTO comment = new CommentTO();
//            comment.setLine(1);
//            comment.setPath("file.md");
//            comment.setMessage("Hey just testing!");
//            comment.setPosition(1);
            // Envia comentários para o sistema de versionamento de código

            log.info("Comentários realizados!");
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

    private @NotNull IVSCService getIvscService(RequestRevisionTO requestRevision) {
        return applicationContext.getBean(requestRevision.getVcs().getService());
    }
}
