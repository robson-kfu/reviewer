package com.nosbor.reviewer.api.clients.github.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GitHubPullRequestTO {

    private String diffUrl;
    private String commentsUrl;
    private String reviewCommentsUrl;
    private String issueUrl;
}
