package com.nosbor.reviewer.api.services;

import com.nosbor.reviewer.api.models.AIResponseWrapper;
import com.nosbor.reviewer.api.models.RequestRevisionTO;

public interface IVCSService {
    String getPullRequestDiff(final RequestRevisionTO requestRevisionTO);

    default String getPullRequestContext(final RequestRevisionTO requestRevisionTO) {
        return "";
    }

    void comment(final AIResponseWrapper aiResponseWrapper);

    void validate();
}
