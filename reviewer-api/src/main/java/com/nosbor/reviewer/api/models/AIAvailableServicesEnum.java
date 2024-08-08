package com.nosbor.reviewer.api.models;

import com.nosbor.reviewer.api.services.IAIService;
import com.nosbor.reviewer.api.services.impl.OllamaServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AIAvailableServicesEnum {
    OLLAMA(OllamaServiceImpl.class);

    private final Class<? extends IAIService> service;
}
