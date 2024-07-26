package com.nosbor.reviewer.api.repos.entities;

import com.nosbor.reviewer.api.models.StatusEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class ProcessStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @NotNull
    private String pullRequestId;
    @NotNull
    private StatusEnum status;
    @NotNull
    private String message;
    @NotNull
    private LocalDate createdAt;
}
