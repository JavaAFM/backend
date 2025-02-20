package org.AFM.rssbridge.mapper.impl;

import lombok.AllArgsConstructor;
import org.AFM.rssbridge.dto.response.SourceDto;
import org.AFM.rssbridge.mapper.SourceMapper;
import org.AFM.rssbridge.news.model.Source;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class SourceMapperImpl implements SourceMapper {
    @Override
    public SourceDto fromSourceToDto(Source source, String title) {
        SourceDto sourceDto = new SourceDto();
        sourceDto.setName(source.getName());
        sourceDto.setType(source.getType());
        sourceDto.setLink(source.getLink());
        sourceDto.setTitle(title);

        return sourceDto;
    }

    @Override
    public List<SourceDto> fromSourcesToDtos(List<Source> sources, List<String> lastTitles) {
        List<SourceDto> sourceDtos = new ArrayList<>();
        int minSize = Math.min(sources.size(), lastTitles.size());
        for (int i = 0; i < minSize; i++) {
            Source source = sources.get(i);
            String title = lastTitles.get(i);

            SourceDto sourceDto = fromSourceToDto(source, title);
            sourceDtos.add(sourceDto);
        }
        return sourceDtos;
    }
}
