package com.nosbor.reviewer.api.controllers;

import com.nosbor.reviewer.api.models.ProcessStatusTO;
import com.nosbor.reviewer.api.services.IProcessFeedbackService;
import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/reviewer/{pullRequestId}")
public class ProcessFeedbackController {

    private final IProcessFeedbackService processFeedback;

    public ProcessFeedbackController(IProcessFeedbackService processFeedback) {
        this.processFeedback = processFeedback;
    }

    @GetMapping("/statuses")
    ResponseEntity<List<ProcessStatusTO>> listStatuses(@PathParam("pullRequestId") String pullRequestId) {
        return new ResponseEntity<>(processFeedback.getProcessStatus(pullRequestId), OK);
    }
}
