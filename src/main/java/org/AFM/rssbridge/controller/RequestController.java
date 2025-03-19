package org.AFM.rssbridge.controller;

import lombok.RequiredArgsConstructor;
import org.AFM.rssbridge.config.socket.MyRawWSHandler;
import org.AFM.rssbridge.dto.request.AddSourceRequestWrapper;
import org.AFM.rssbridge.dto.response.SourceRequestDto;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.user.model.SourceRequest;
import org.AFM.rssbridge.user.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/addRequest")
    public ResponseEntity<String> createRequest(
            @RequestBody AddSourceRequestWrapper wrapper
            ) throws NotFoundException {
        requestService.createRequest(wrapper.getRequest(), wrapper.getIin());
        return ResponseEntity.ok("Request has been delivered.");
    }

    @GetMapping("/allRequests")
    public ResponseEntity<List<SourceRequestDto>> allRequests(){
        return ResponseEntity.ok(requestService.allRequests());
    }

    @GetMapping("/myRequests")
    private ResponseEntity<List<SourceRequest>> userRequests(
            @RequestParam String iin
    ){
        List<SourceRequest> requests = requestService.getAllRequestsFromUser(iin);
        return ResponseEntity.ok(requests);
    }
}
