package com.nosbor.reviewer.api.services;

import com.nosbor.reviewer.api.models.AIResponseWrapper;
import com.nosbor.reviewer.api.models.PullRequestContextTO;

public interface IAIService {
    AIResponseWrapper getAIReview(final PullRequestContextTO pullRequestContextTO);

    default String getEmbeddings(final PullRequestContextTO pullRequestContextTO) {
        return "";
    }

    void validate();
}
