package org.AFM.rssbridge.dto.response;

import lombok.Data;
import org.AFM.rssbridge.news.model.News;
import org.springframework.lang.Nullable;

@Data
public class NewsWithScore {
    private News news;
    @Nullable
    private DocumentScore documentScore;
}
