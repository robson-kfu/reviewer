package com.nosbor.reviewer.api.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nosbor.reviewer.api.models.ProcessStatusTO;
import com.nosbor.reviewer.api.repos.IProcessStatusRepository;
import com.nosbor.reviewer.api.repos.entities.ProcessStatusEntity;
import com.nosbor.reviewer.api.services.IProcessFeedbackService;
import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@Slf4j
public class ProcessFeedbackServiceServiceImpl implements IProcessFeedbackService {

    private final IProcessStatusRepository processStatusRepository;
    private final ObjectMapper objectMapper;

    public ProcessFeedbackServiceServiceImpl(IProcessStatusRepository processStatusRepository,
                                             ObjectMapper objectMapper) {
        this.processStatusRepository = processStatusRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<ProcessStatusTO> getProcessStatus(String pullRequestId) {
        log.info("Buscando o status do processo de analise da PR {}", pullRequestId);
        ProcessStatusEntity entityExample = new ProcessStatusEntity();
        entityExample.setPullRequestId(pullRequestId);
        List<ProcessStatusEntity> processStatusEntities = processStatusRepository.findAll(Example.of(entityExample));
        log.info("Encontrado {} status.", processStatusEntities.size());
        return objectMapper.convertValue(processStatusEntities, new TypeReference<>() {});
    }

    @Bean
    Consumer<Message<ProcessStatusTO>> processFeedback(){
        return processStatusTOMessage -> {
            ProcessStatusTO processStatusTO = processStatusTOMessage.getPayload();
            log.info("Processando feedback do processo {}. payload {}",
                    processStatusTOMessage.getHeaders().getOrDefault("x-stapa-do-processo", "etapa não informada"),
                    processStatusTO);
            processStatusRepository.save(objectMapper.convertValue(processStatusTO, ProcessStatusEntity.class));
        };
    }
}
