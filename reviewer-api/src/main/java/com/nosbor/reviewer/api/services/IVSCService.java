package com.nosbor.reviewer.api.services;

public interface IVSCService {
    String getPullRequestDiff(final String pullRequestId);

    String getPullRequestContext(final String pullRequestId);
}
