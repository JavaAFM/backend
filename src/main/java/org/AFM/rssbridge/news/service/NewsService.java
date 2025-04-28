package org.AFM.rssbridge.news.service;

import org.AFM.rssbridge.dto.request.FilterRequest;
import org.AFM.rssbridge.dto.request.TagRequest;
import org.AFM.rssbridge.dto.response.FilterResponse;
import org.AFM.rssbridge.dto.response.PercentageScore;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.news.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NewsService {
    Page<News> getAllNews(Pageable pageable);
    FilterResponse getAllNewsFromSource(Pageable pageable,
                                        FilterRequest filterRequest, TagRequest tagRequest) throws NotFoundException;
    Page<News> lastNews(Pageable pageable);
    Page<News> lastNewsOfSource(String source, Pageable pageable);
    News findByTitle(String title) throws NotFoundException;
    News findById(Long id) throws NotFoundException;
}
