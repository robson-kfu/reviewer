package com.nosbor.reviewer.api.services.impl;

import com.nosbor.reviewer.api.models.ProcessStatusTO;
import com.nosbor.reviewer.api.models.RequestRevisionTO;
import com.nosbor.reviewer.api.models.StatusEnum;
import com.nosbor.reviewer.api.services.IReviewerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class ReviewerServiceImpl implements IReviewerService {

    public static final String REQUEST_MERGE_REVISION_OUT_0 = "requestMergeRevision-out-0";
    private final StreamBridge streamBridge;

    public ReviewerServiceImpl(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Override
    public ProcessStatusTO requestRevision(RequestRevisionTO requestRevisionTO) {
        log.info("Processando solicitação de revisão do MR id {} do " +
                        "sistema de versão {} com a IA {}", requestRevisionTO.getIdMergeRequest(),
                requestRevisionTO.getVcs(), requestRevisionTO.getAiRevisor());

        boolean sent = streamBridge.send(REQUEST_MERGE_REVISION_OUT_0, requestRevisionTO);

        if (sent) {
            log.info("Revisão colocada na pipeline de execução");
            return ProcessStatusTO.builder()
                    .status(StatusEnum.INICIADO)
                    .message("Processo de revisão iniciado.")
                    .createdAt(LocalDate.now())
                    .build();
        } else {
            log.info("Erro ao escrever na fila {}", REQUEST_MERGE_REVISION_OUT_0);
            throw new RuntimeException("Fila indisponível. Tente novamente!");
        }
    }
}
