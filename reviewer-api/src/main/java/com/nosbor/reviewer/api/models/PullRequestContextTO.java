package com.nosbor.reviewer.api.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PullRequestContextTO {
    private String pathUrl;
    private String context;
    @NotNull
    private String pullRequestId;
}
