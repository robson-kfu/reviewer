package com.nosbor.reviewer.api.services;

import com.nosbor.reviewer.api.models.ProcessStatusTO;
import com.nosbor.reviewer.api.models.RequestRevisionTO;

public interface IReviewerService {

    ProcessStatusTO requestRevision(final RequestRevisionTO requestRevisionTO);
}
