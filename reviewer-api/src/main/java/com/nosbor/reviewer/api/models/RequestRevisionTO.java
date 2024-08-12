package com.nosbor.reviewer.api.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestRevisionTO {

    @NotNull
    protected VCSAvailableServicesEnum vcs;
    @NotNull
    protected String pullRequestId;
    @NotNull
    protected AIAvailableServicesEnum aiRevisor;
    @NotNull
    protected String owner;
    @NotNull
    protected String repoName;
}
