package org.AFM.rssbridge.user.service;

import org.AFM.rssbridge.dto.request.AddSourceRequest;
import org.AFM.rssbridge.dto.response.SourceRequestDto;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.user.model.SourceRequest;
import org.springframework.stereotype.Service;

import java.util.List;

public interface RequestService {
    List<SourceRequest> getAllRequestsFromUser(String iin);
    SourceRequest createRequest(AddSourceRequest request, String iin) throws NotFoundException;
    SourceRequest getRequestById(Long id) throws NotFoundException;
    void save(SourceRequest sourceRequest);
    List<SourceRequestDto> allRequests();
}
