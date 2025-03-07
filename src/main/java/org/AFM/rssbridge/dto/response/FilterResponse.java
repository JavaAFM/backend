package org.AFM.rssbridge.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.AFM.rssbridge.news.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class FilterResponse {
    private Page<NewsWithScore> newsWithScore;

    @Nullable
    @Setter
    private PercentageScore percentageScore;

    public void setNewsWithScores(List<News> news, List<DocumentScore> documentScores, Pageable pageable){

        if (news == null || news.isEmpty()) {
            this.newsWithScore = Page.empty(pageable);
            return;
        }
        Map<Long, DocumentScore> scoreMap = (documentScores != null)
                ? documentScores.stream().collect(Collectors.toMap(DocumentScore::getId, score -> score))
                : Map.of();

        List<NewsWithScore> newsWithScores = news.stream()
                .map(n -> {
                    NewsWithScore nws = new NewsWithScore();
                    nws.setNews(n);
                    nws.setDocumentScore(scoreMap.getOrDefault(n.getId(), null));
                    return nws;
                })
                .toList();
        this.newsWithScore = new PageImpl<>(newsWithScores, pageable, news.size());



    }

    @JsonIgnore
    public List<News> getOnlyNews(){
        if(newsWithScore == null || newsWithScore.isEmpty()){
            return List.of();
        }
        return newsWithScore.stream()
                .map(NewsWithScore::getNews)
                .toList();
    }
}
