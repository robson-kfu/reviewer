package com.nosbor.reviewer.api.services.impl;

import com.nosbor.reviewer.api.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.stream.function.StreamBridge;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ReviewerServiceImplTest {

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private ReviewerServiceImpl reviewerService;

    @BeforeEach
    void setUp() throws Exception {
        AutoCloseable autoCloseable = MockitoAnnotations.openMocks(this);
        autoCloseable.close();
    }

    @Test
    void testRequestRevision_Success() {
        RequestRevisionTO request = new RequestRevisionTO();
        request.setIdMergeRequest("123");
        request.setVcs(VCSAvailableServicesEnum.GITHUB);
        request.setAiRevisor(AIAvailableServicesEnum.OLLAMA);

        when(streamBridge.send(ReviewerServiceImpl.REQUEST_MERGE_REVISION_OUT_0, request)).thenReturn(true);

        ProcessStatusTO status = reviewerService.requestRevision(request);

        assertEquals(StatusEnum.INICIADO, status.getStatus());
        assertEquals("Processo de revisão iniciado.", status.getMessage());
        assertEquals(LocalDate.now(), status.getCreatedAt());
    }

    @Test
    void testRequestRevision_Failure() {
        RequestRevisionTO request = new RequestRevisionTO();
        request.setIdMergeRequest("123");
        request.setVcs(VCSAvailableServicesEnum.GITHUB);
        request.setAiRevisor(AIAvailableServicesEnum.OLLAMA);

        when(streamBridge.send(ReviewerServiceImpl.REQUEST_MERGE_REVISION_OUT_0, request)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                reviewerService.requestRevision(request)
        );

        assertEquals("Fila indisponível. Tente novamente!", exception.getMessage());
    }
}

