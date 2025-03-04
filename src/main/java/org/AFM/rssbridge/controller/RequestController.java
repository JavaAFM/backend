package org.AFM.rssbridge.controller;

import lombok.RequiredArgsConstructor;
import org.AFM.rssbridge.config.MyRawWSHandler;
import org.AFM.rssbridge.dto.request.AddSourceRequest;
import org.AFM.rssbridge.dto.request.AddSourceRequestWrapper;
import org.AFM.rssbridge.dto.response.SourceRequestDto;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.user.model.SourceRequest;
import org.AFM.rssbridge.user.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
    private final MyRawWSHandler myRawWSHandler;

    @PostMapping("/addRequest")
    public ResponseEntity<String> createRequest(
            @RequestBody AddSourceRequestWrapper wrapper
            ) throws NotFoundException {
        SourceRequest sourceRequest = requestService.createRequest(wrapper.getRequest(), wrapper.getIin());
        myRawWSHandler.broadcastToAdmins(sourceRequest);
        return ResponseEntity.ok("Request has been delivered.");
    }

    @GetMapping("/allRequests")
    public ResponseEntity<List<SourceRequestDto>> allRequests(){
        return ResponseEntity.ok(requestService.allRequests());
    }
}
