package org.AFM.rssbridge.news.repository.impl;


import lombok.RequiredArgsConstructor;
import org.AFM.rssbridge.dto.request.FilterRequest;
import org.AFM.rssbridge.news.model.News;
import org.AFM.rssbridge.news.model.Source;
import org.AFM.rssbridge.news.repository.NewsRepo;
import org.AFM.rssbridge.news.repository.spec.SourceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NewsRepoImpl implements NewsRepo {
    private final SourceRepository sourceRepository;
    @Override
    public Page<News> filterNews(Pageable pageable, FilterRequest filterRequest, List<News> allNews) {
        if (allNews == null || allNews.isEmpty()) {
            return Page.empty(pageable);
        }

        List<News> filteredNews;

        if (filterRequest.getSource_name().equals("allSources")) {
            filteredNews = allNews;
        } else {
            Source source = sourceRepository.getSourceByName(filterRequest.getSource_name()).orElse(null);
            if (source == null) {
                return Page.empty(pageable);
            }

            filteredNews = allNews.stream()
                    .filter(news -> news.getSource() != null && news.getSource().equals(source))
                    .collect(Collectors.toList());
        }
        filteredNews = filteredNews.stream()
                .filter(news -> filterByDateRange(news, filterRequest.getFrom(), filterRequest.getTo()))
                .collect(Collectors.toList());

        filteredNews.sort(News::compareTo);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredNews.size());

        return new PageImpl<>(filteredNews.subList(start, end), pageable, filteredNews.size());

    }

    private boolean filterByDateRange(News news, LocalDate from, LocalDate to) {
        boolean matchesFrom = from == null || (news.getPublicationDate() != null && !news.getPublicationDate().isBefore(from.atStartOfDay()));
        boolean matchesTo = to == null || (news.getPublicationDate() != null && !news.getPublicationDate().isAfter(to.atTime(23, 59, 59)));

        return matchesFrom && matchesTo;
    }

}