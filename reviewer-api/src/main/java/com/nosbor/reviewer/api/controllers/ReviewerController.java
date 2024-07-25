package com.nosbor.reviewer.api.controllers;

import com.nosbor.reviewer.api.models.ProcessStatusTO;
import com.nosbor.reviewer.api.models.RequestRevisionTO;
import com.nosbor.reviewer.api.services.IReviewerService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.ACCEPTED;

@RestController
@RequestMapping("/reviewer")
public class ReviewerController {

    private final IReviewerService reviewerService;

    public ReviewerController(IReviewerService reviewerService) {
        this.reviewerService = reviewerService;
    }

    @PostMapping
    ResponseEntity<ProcessStatusTO> start(@RequestBody @NotNull RequestRevisionTO requestRevisionTO) {
        return new ResponseEntity<>(reviewerService.requestRevision(requestRevisionTO), ACCEPTED);
    }
}
