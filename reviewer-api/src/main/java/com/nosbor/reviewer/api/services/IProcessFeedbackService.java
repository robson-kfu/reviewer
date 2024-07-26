package com.nosbor.reviewer.api.services;

import com.nosbor.reviewer.api.models.ProcessStatusTO;

import java.util.List;

public interface IProcessFeedbackService {
    List<ProcessStatusTO> getProcessStatus(final String pullRequestId);
}
