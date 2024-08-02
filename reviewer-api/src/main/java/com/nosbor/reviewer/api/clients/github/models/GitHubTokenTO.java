package com.nosbor.reviewer.api.clients.github.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GitHubTokenTO {
    @ToString.Exclude
    private String token;
    private Instant expiresAt;
    private Permissions permissions;
    private String repositorySelection;

    @Data
    public static class Permissions {
        private String issues;
        private String metadata;
        private String pullRequests;
    }
}
