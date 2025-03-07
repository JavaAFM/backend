package org.AFM.rssbridge.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.AFM.rssbridge.dto.response.SourceRequestDto;
import org.AFM.rssbridge.mapper.RequestMapper;
import org.AFM.rssbridge.user.model.SourceRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestMapperImpl implements RequestMapper {
    @Override
    public SourceRequestDto fromRequestToDto(SourceRequest request) {
        SourceRequestDto sourceRequestDto = new SourceRequestDto();
        sourceRequestDto.setId(request.getId());
        sourceRequestDto.setIin(request.getUser().getIin());
        sourceRequestDto.setFio(request.getUser().getName() + " " + request.getUser().getSurname() + " " + request.getUser().getFathername());
        sourceRequestDto.setStatus(String.valueOf(request.getStatus()));
        sourceRequestDto.setLink(request.getLink());
        sourceRequestDto.setReason(request.getReason());
        sourceRequestDto.setPublishedDate(request.getPublishedDate());
        sourceRequestDto.setName(request.getName());


        return sourceRequestDto;
    }

    @Override
    public List<SourceRequestDto> fromRequestsToDtos(List<SourceRequest> requests) {
        List<SourceRequestDto> sourceRequestDtos = new ArrayList<>();
        for(SourceRequest sourceRequest : requests){
            sourceRequestDtos.add(fromRequestToDto(sourceRequest));
        }

        return sourceRequestDtos;
    }
}
