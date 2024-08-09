package com.nosbor.reviewer.api.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PullRequestContextTO extends RequestRevisionTO {
    private String diff;
    private String context;
}
