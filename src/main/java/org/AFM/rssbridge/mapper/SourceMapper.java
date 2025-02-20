package org.AFM.rssbridge.mapper;

import org.AFM.rssbridge.dto.response.SourceDto;
import org.AFM.rssbridge.news.model.Source;

import java.util.List;

public interface SourceMapper {
    SourceDto fromSourceToDto(Source source, String title);
    List<SourceDto> fromSourcesToDtos(List<Source> sources, List<String> lastTitles);
}
