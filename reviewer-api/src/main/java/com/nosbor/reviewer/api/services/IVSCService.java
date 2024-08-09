package com.nosbor.reviewer.api.services;

import com.nosbor.reviewer.api.models.AIResponseWrapper;
import com.nosbor.reviewer.api.models.RequestRevisionTO;

public interface IVSCService {
    String getPullRequestDiff(final RequestRevisionTO requestRevisionTO) throws Exception;

    default String getPullRequestContext(final RequestRevisionTO requestRevisionTO) {
        return "";
    }

    void comment(final AIResponseWrapper aiResponseWrapper);

    void validate();
}
