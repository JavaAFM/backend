package org.AFM.rssbridge.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AFM.rssbridge.dto.request.DecisionRequest;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.news.service.SourceService;
import org.AFM.rssbridge.user.model.SourceRequest;
import org.AFM.rssbridge.user.model.SourceStatus;
import org.AFM.rssbridge.user.model.UserLog;
import org.AFM.rssbridge.user.service.RequestService;
import org.AFM.rssbridge.user.service.impl.UserLogServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class AdminController {
    private final RequestService requestService;
    private final SourceService sourceService;
    private final UserLogServiceImpl userLogService;
    @PutMapping("/decision")
    public ResponseEntity<String> makeDecision(
            @RequestBody DecisionRequest decisionRequest
            ) throws NotFoundException {
        SourceRequest sourceRequest = requestService.getRequestById(decisionRequest.getRequestId());

        sourceRequest.setStatus(SourceStatus.valueOf(decisionRequest.getStatus()));
        sourceRequest.setReason(decisionRequest.getReason());

        requestService.save(sourceRequest);

        if (SourceStatus.valueOf(decisionRequest.getStatus()) == SourceStatus.ACCEPTED) {
            log.info("Adding new source...");
            sourceService.addNewsSource(sourceRequest);
        }
        return ResponseEntity.ok("Decision was made.");
    }
    @PostMapping("/logs")
    public String logAction(@RequestParam Long userId, @RequestParam String action, @RequestParam String details) {
        userLogService.logAction(userId, action, details);
        return "Log created successfully";
    }

    /**
     * Get logs by user ID.
     */
    @GetMapping("/logs/user/{userId}")
    public List<UserLog> getLogsByUser(@PathVariable Long userId) {
        return userLogService.getLogsByUser(userId);
    }

    /**
     * Get logs by action.
     */
    @GetMapping("/logs/action/{action}")
    public List<UserLog> getLogsByAction(@PathVariable String action) {
        return userLogService.getLogsByAction(action);
    }

    /**
     * Get logs by user ID and action.
     */
    @GetMapping("/logs/user/{userId}/action/{action}")
    public List<UserLog> getLogsByUserAndAction(@PathVariable Long userId, @PathVariable String action) {
        return userLogService.getLogsByUserAndAction(userId, action);
}}
