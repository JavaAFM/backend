package org.AFM.rssbridge.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AFM.rssbridge.config.MyRawWSHandler;
import org.AFM.rssbridge.dto.request.DecisionRequest;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.news.service.SourceService;
import org.AFM.rssbridge.user.model.SourceRequest;
import org.AFM.rssbridge.user.model.SourceStatus;
import org.AFM.rssbridge.user.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class AdminController {
    private final RequestService requestService;
    private final SourceService sourceService;
    private final MyRawWSHandler myRawWSHandler;

    @PutMapping("/decision")
    public ResponseEntity<String> makeDecision(
            @RequestBody DecisionRequest decisionRequest
            ) throws NotFoundException {
        SourceRequest sourceRequest = requestService.getRequestById(decisionRequest.getRequestId());

        sourceRequest.setStatus(SourceStatus.valueOf(decisionRequest.getStatus()));
        sourceRequest.setReason(decisionRequest.getReason());

        requestService.save(sourceRequest);


        String decisionMessage = "Your request (ID: " + sourceRequest.getId() + ") was "
                + decisionRequest.getStatus() + ". Reason: " + decisionRequest.getReason();

        if (SourceStatus.valueOf(decisionRequest.getStatus()) == SourceStatus.ACCEPTED) {
            log.info("Adding new source...");
            sourceService.addNewsSource(sourceRequest);
        }
        log.info("Sending to user...");
        myRawWSHandler.broadcastToUser(sourceRequest, decisionMessage);
        return ResponseEntity.ok("Decision was made.");
    }
}
