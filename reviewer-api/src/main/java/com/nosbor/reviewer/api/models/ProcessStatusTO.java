package com.nosbor.reviewer.api.models;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessStatusTO {

    @NotNull
    private String pullRequestId;
    @NotNull
    private StatusEnum status;
    @NotNull
    private String message;
    @NotNull
    private LocalDate createdAt;
}
