package org.AFM.rssbridge.news.repository;

import org.AFM.rssbridge.dto.request.FilterRequest;
import org.AFM.rssbridge.news.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NewsRepo {
    Page<News> filterNews(Pageable pageable, FilterRequest filterRequest, List<News> newsList);
}
