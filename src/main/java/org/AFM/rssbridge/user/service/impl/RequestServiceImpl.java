package org.AFM.rssbridge.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.AFM.rssbridge.dto.request.AddSourceRequest;
import org.AFM.rssbridge.dto.response.SourceRequestDto;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.mapper.RequestMapper;
import org.AFM.rssbridge.user.model.SourceRequest;
import org.AFM.rssbridge.user.repository.RequestRepository;
import org.AFM.rssbridge.user.service.RequestService;
import org.AFM.rssbridge.user.model.User;
import org.AFM.rssbridge.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<SourceRequest> getAllRequestsFromUser(String iin) {
        return requestRepository.getAddSourceRequestsByUserIin(iin);
    }

    @Override
    public SourceRequest createRequest(AddSourceRequest request, String iin) throws NotFoundException {
        User user = userRepository.findByIin(iin).orElseThrow(()-> new NotFoundException("User not found..."));
        SourceRequest sourceRequest = new SourceRequest();

        sourceRequest.setUser(user);
        sourceRequest.setName(request.getName());
        sourceRequest.setLink(request.getLink());

        return requestRepository.save(sourceRequest);
    }

    @Override
    public SourceRequest getRequestById(Long id) throws NotFoundException {
        return requestRepository.getSourceRequestById(id).orElseThrow(()-> new NotFoundException("Request not found..."));
    }

    @Override
    public void save(SourceRequest sourceRequest) {
        requestRepository.save(sourceRequest);
    }

    @Override
    public List<SourceRequestDto> allRequests() {
        return requestMapper.fromRequestsToDtos(requestRepository.findAll());
    }
}
