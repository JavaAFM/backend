package org.AFM.rssbridge.mapper.impl;

import org.AFM.rssbridge.mapper.TagMapper;
import org.AFM.rssbridge.news.model.News;
import org.AFM.rssbridge.news.model.NewsTag;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TagMapperImpl implements TagMapper {

    @Override
    public NewsTag toTag(String tag, News news) {
        NewsTag newsTag = new NewsTag();
        newsTag.setNews(news);
        newsTag.setTag(tag);

        return newsTag;
    }

    @Override
    public List<NewsTag> toListOfTags(List<String> tags, News news) {
        List<NewsTag> newsTags = new ArrayList<>();
        for(String tag : tags){
            newsTags.add(toTag(tag, news));
        }
        return newsTags;
    }
}
