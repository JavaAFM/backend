package org.AFM.rssbridge.controller;

import lombok.AllArgsConstructor;
import org.AFM.rssbridge.dto.request.FilterRequest;
import org.AFM.rssbridge.dto.request.NewsRequest;
import org.AFM.rssbridge.dto.request.TagRequest;
import org.AFM.rssbridge.dto.response.DocumentScore;
import org.AFM.rssbridge.dto.response.FilterResponse;
import org.AFM.rssbridge.dto.response.ModelResponse;
import org.AFM.rssbridge.dto.response.PercentageScore;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.news.model.News;
import org.AFM.rssbridge.news.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping("/allNews")
    private ResponseEntity<Page<News>> getAllNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(newsService.getAllNews(pageable));
    }

    @PostMapping("/getNewsBySource")
    private ResponseEntity<FilterResponse> getNewsBySource(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestBody NewsRequest newsRequest

    ) throws NotFoundException {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(newsService.getAllNewsFromSource(pageable, newsRequest.getFilterRequest(),
                newsRequest.getTagRequest()));
    }

    @PostMapping("/filter")
    private ResponseEntity<Page<News>> filter(
            @RequestBody FilterRequest filterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(newsService.filter(filterRequest, pageable));
    }

    @GetMapping("/lastNews")
    private ResponseEntity<Page<News>> lastNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(newsService.lastNews(pageable));
    }

    @GetMapping("/lastNewsOfSource")
    private ResponseEntity<Page<News>> lastNewsOfSource(
            @RequestParam String source,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(newsService.lastNewsOfSource(source, pageable));
    }

    @PostMapping("/predict")
    public ResponseEntity<PercentageScore> predict(
            @RequestBody TagRequest request
    ) {
        PercentageScore percentageScore = newsService.predict(request);
        return percentageScore != null ? ResponseEntity.ok(percentageScore) : ResponseEntity.noContent().build();
    }

    @PostMapping("/allPredictedAnswers")
    public ResponseEntity<List<News>> allPredictedAnswers(
            @RequestBody TagRequest request
    ) throws NotFoundException {
        List<News> selectedNews = newsService.allPredictedAnswers(request);
        return !selectedNews.isEmpty() ? ResponseEntity.ok(selectedNews) : ResponseEntity.noContent().build();
    }
}


