package org.AFM.rssbridge.mapper;

import org.AFM.rssbridge.news.model.News;
import org.AFM.rssbridge.news.model.NewsTag;

import java.util.List;

public interface TagMapper {
    NewsTag toTag(String tag, News news);

    List<NewsTag> toListOfTags(List<String> tags, News news);
}
