package com.nosbor.reviewer.api.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestRevisionTO {

    @NotNull
    private VCSAvailableServicesEnum vcs;
    @NotNull
    private String pullRequestId;
    @NotNull
    private AIAvailableServicesEnum aiRevisor;
}
