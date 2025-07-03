package com.nosbor.reviewer.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentTO {
    private String path;
    private Integer position;
    private String comment;
}
