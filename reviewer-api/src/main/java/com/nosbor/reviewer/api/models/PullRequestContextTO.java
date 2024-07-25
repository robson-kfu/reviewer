package com.nosbor.reviewer.api.models;

import lombok.Data;

@Data
public class PullRequestContextTO {
    private String pathUrl;
    private String context;
}
