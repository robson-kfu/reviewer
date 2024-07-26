package com.nosbor.reviewer.api.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class IAResponseTO {
    private String context;
    private List<String> response;
    @NotNull
    private String pullRequestId;
}
