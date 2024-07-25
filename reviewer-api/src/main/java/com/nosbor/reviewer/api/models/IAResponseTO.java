package com.nosbor.reviewer.api.models;

import lombok.Data;

import java.util.List;

@Data
public class IAResponseTO {
    private String context;
    private List<String> response;
}
