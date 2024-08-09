package com.nosbor.reviewer.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentTO {
    private Integer line;
    private String comment;
}
