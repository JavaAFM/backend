package org.AFM.rssbridge.news.service;

import org.AFM.rssbridge.dto.request.AddSourceRequest;
import org.AFM.rssbridge.dto.response.SourceDto;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.news.model.Source;

import java.util.List;

public interface SourceService {
    List<SourceDto> getAllSources();
    Source getSourceByName(String name) throws NotFoundException;
    List<String> allTypes();
    List<String> allNames();
    void addNewsSource(AddSourceRequest addSourceRequest);
}
