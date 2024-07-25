package com.nosbor.reviewer.api.models;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class ProcessStatusTO {

    @NotNull
    private StatusEnum status;
    @NotNull
    private String message;
    @NotNull
    private LocalDate createdAt;
}
