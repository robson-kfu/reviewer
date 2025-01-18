package com.nosbor.reviewer.api.models;

import com.nosbor.reviewer.api.services.IVCSService;
import com.nosbor.reviewer.api.services.impl.GitHubServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VCSAvailableServicesEnum {
    GITHUB(GitHubServiceImpl.class);

    private final Class<? extends IVCSService> service;
}
