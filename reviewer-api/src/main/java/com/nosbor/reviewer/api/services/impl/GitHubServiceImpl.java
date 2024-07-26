package com.nosbor.reviewer.api.services.impl;

import com.nosbor.reviewer.api.services.IVSCService;
import org.springframework.stereotype.Service;

@Service
public class GitHubServiceImpl implements IVSCService {
    @Override
    public String getPullRequestDiff(String pullRequestId) {
        return "implementar";
    }

    @Override
    public String getPullRequestContext(String pullRequestId) {
        return "implementar";
    }
}
