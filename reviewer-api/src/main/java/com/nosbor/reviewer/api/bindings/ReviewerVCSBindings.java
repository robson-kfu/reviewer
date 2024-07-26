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

            String pullRequestId = requestRevision.getPullRequestId();

            PullRequestContextTO context = new PullRequestContextTO();
            context.setDiff(ivscService.getPullRequestDiff(pullRequestId));
            context.setContext(ivscService.getPullRequestContext(pullRequestId));
            context.setPullRequestId(pullRequestId);

            log.info("Reposta do sistema de versionamento {}", context);
            return MessageBuilder.withPayload(context).copyHeaders(mergeRevision.getHeaders()).build();
        };
    }

    @Bean
    Function<Message<IAResponseTO>, Message<ProcessStatusTO>> returnComments() {
        return iaResponse -> {
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
                                    .pullRequestId(iaResponse.getPayload().getPullRequestId())
                                    .message("Processo finalizado!")
                                    .status(StatusEnum.FINALIZADO)
                                    .createdAt(LocalDate.now())
                                    .build()
                    )
                    .copyHeaders(iaResponse.getHeaders()).build();
        };
    }

    private @NotNull IVSCService getIvscService(RequestRevisionTO requestRevision) {
        return applicationContext.getBean(requestRevision.getVcs().getService());
    }
}
