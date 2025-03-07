package org.AFM.rssbridge.news.service.impl;

import lombok.RequiredArgsConstructor;
import org.AFM.rssbridge.news.model.MoodyNews;
import org.AFM.rssbridge.news.repository.spec.MoodyNewsRepository;
import org.AFM.rssbridge.news.service.MoodyNewsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoodyNewsServiceImpl implements MoodyNewsService {
    private final MoodyNewsRepository repository;
    @Override
    public MoodyNews getMoodyNewsByTitle(String title) {
        return repository.getMoodyNewsByTitle(title);
    }
}
