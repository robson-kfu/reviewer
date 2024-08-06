package com.nosbor.reviewer.api.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AIResponseWrapper {
    @NotNull
    private String pullRequestId;
    List<AIResponseTO> aiResponseTOList;
}
