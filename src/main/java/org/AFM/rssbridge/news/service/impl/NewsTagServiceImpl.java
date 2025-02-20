package org.AFM.rssbridge.news.service.impl;

import lombok.AllArgsConstructor;
import org.AFM.rssbridge.news.model.NewsTag;
import org.AFM.rssbridge.news.repository.spec.NewsTagRepository;
import org.AFM.rssbridge.news.service.NewsTagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NewsTagServiceImpl implements NewsTagService {
    private final NewsTagRepository newsTagRepository;
    @Override
    public List<NewsTag> getAll() {
        return newsTagRepository.findAll();
    }
}
