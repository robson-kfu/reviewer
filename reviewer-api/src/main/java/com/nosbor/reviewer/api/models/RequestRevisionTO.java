package com.nosbor.reviewer.api.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestRevisionTO {

    @NotNull
    private VCSAvailableServicesEnum vcs;
    @NotNull
    private String idMergeRequest;
    @NotNull
    private AIAvailableServicesEnum aiRevisor;
}
