package com.nosbor.reviewer.api.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AIResponseTO {
    private Integer line;
    private String comment;
    @NotNull
    private String pullRequestId;
}
