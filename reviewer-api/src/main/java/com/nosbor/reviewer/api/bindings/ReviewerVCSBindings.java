package com.nosbor.reviewer.api.bindings;

import com.nosbor.reviewer.api.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.Function;

@Component
@Slf4j
public class ReviewerVCSBindings {

    @Bean
    Function<Message<RequestRevisionTO>, Message<PullRequestContextTO>> requestContext() {
        return mergeRevision -> {
            log.info("Processando requisição de revisão de MR {}", mergeRevision.getPayload());
            // Recuperar Diff do MR
            PullRequestContextTO context = new PullRequestContextTO();
            context.setPathUrl("file.md");
            // Recuperar contexto do MR
            context.setContext("Escreva um hello word in Clojure");

            log.info("Reposta do sistema de versionamento {}", context);
            return MessageBuilder.withPayload(context).copyHeaders(mergeRevision.getHeaders()).build();
        };
    }

    @Bean
    Function<Message<IAResponseTO>, Message<ProcessStatusTO>> returnComments() {
        return iaResponse -> {
            log.info("Processando revisão da IA {}", iaResponse);
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
                                    .message("Processo finalizado!")
                                    .status(StatusEnum.FINALIZADO)
                                    .createdAt(LocalDate.now())
                                    .build()
                    )
                    .copyHeaders(iaResponse.getHeaders()).build();
        };
    }
}
