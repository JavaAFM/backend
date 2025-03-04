package org.AFM.rssbridge.mapper;

import org.AFM.rssbridge.dto.response.SourceRequestDto;
import org.AFM.rssbridge.news.model.Source;
import org.AFM.rssbridge.user.model.SourceRequest;

import java.util.List;


public interface RequestMapper {
    SourceRequestDto fromRequestToDto(SourceRequest request);
    List<SourceRequestDto> fromRequestsToDtos(List<SourceRequest> requests);
}
