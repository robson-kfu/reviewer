package com.nosbor.reviewer.api.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AIResponseWrapper extends RequestRevisionTO {
    List<CommentTO> comments;
}
