package com.nosbor.reviewer.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OllamaResponseTO {
    private String response;
    private Boolean done;
}
