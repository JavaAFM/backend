package org.AFM.rssbridge.news.service.impl;

import lombok.AllArgsConstructor;
import org.AFM.rssbridge.dto.request.FilterRequest;
import org.AFM.rssbridge.dto.request.TagRequest;
import org.AFM.rssbridge.dto.response.DocumentScore;
import org.AFM.rssbridge.dto.response.FilterResponse;
import org.AFM.rssbridge.dto.response.ModelResponse;
import org.AFM.rssbridge.dto.response.PercentageScore;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.news.model.News;
import org.AFM.rssbridge.news.model.Source;
import org.AFM.rssbridge.news.repository.spec.NewsRepository;
import org.AFM.rssbridge.news.repository.NewsSpecification;
import org.AFM.rssbridge.news.repository.spec.SourceRepository;
import org.AFM.rssbridge.news.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;

    private final RestTemplate restTemplate;
    private final String fastApiUrl = "http://192.168.122.104:8000/predict/";
    private static Logger LOGGER = LoggerFactory.getLogger(NewsServiceImpl.class);


    @Override
    @Transactional(transactionManager = "newsTransactionManager")
    public Page<News> getAllNews(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }

    @Override
    @Transactional(transactionManager = "newsTransactionManager")
    public FilterResponse getAllNewsFromSource(Pageable pageable,
                                               FilterRequest filterRequest, TagRequest tagRequest) {
        LOGGER.info("Starting getAllNewsFromSource with filterRequest: {} and tagRequest: {}", filterRequest, tagRequest);
        FilterResponse response = new FilterResponse();
        Page<News> filteredNews;

        if (filterRequest.isNeg() || tagRequest.getTag() != null || tagRequest.is_specific()) {
            if (tagRequest.getTag() == null) {
                tagRequest.setTag("");
            }
            DocumentScore[] documentScores = new DocumentScore[0];

            // If a prediction tag is provided
            if (tagRequest != null && tagRequest.getTag() != null && !tagRequest.getTag().isEmpty()) {
                LOGGER.info("Tag provided: {}. Sending predict request...", tagRequest.getTag());
                TagRequest predictionRequest = new TagRequest();
                predictionRequest.setTag(tagRequest.getTag());
                predictionRequest.set_specific(tagRequest.is_specific());

                ResponseEntity<DocumentScore[]> modelResponse = restTemplate.postForEntity(fastApiUrl, predictionRequest, DocumentScore[].class);
                documentScores = modelResponse.getBody();
                int scoreCount = documentScores != null ? documentScores.length : 0;
                LOGGER.info("Received {} document scores from predict endpoint", scoreCount);

                if (documentScores != null && documentScores.length > 0) {
                    // If negative filter is requested
                    if (filterRequest.isNeg()) {
                        filterRequest.setNeg(false);
                        LOGGER.info("Processing negative predictions...");
                        List<Long> negativeNewsIds = Arrays.stream(documentScores)
                                .filter(DocumentScore::is_negative)
                                .map(DocumentScore::getId)
                                .toList();
                        List<News> negativeNews = newsRepository.findAllById(negativeNewsIds);
                        LOGGER.info("Found {} negative news items", negativeNews.size());

                        response.setNewsWithScores(negativeNews, List.of(documentScores), pageable);
                        long negativeCount = negativeNews.size();
                        long totalCount = documentScores.length;
                        double negativePercentage = (totalCount > 0) ? (negativeCount / (double) totalCount) * 100 : 0;
                        double positivePercentage = 100 - negativePercentage;
                        PercentageScore percentageScore = new PercentageScore();
                        percentageScore.setNegative(negativePercentage);
                        percentageScore.setPositive(positivePercentage);
                        percentageScore.setOverall(totalCount);
                        percentageScore.setTag(tagRequest.getTag());
                        response.setPercentageScore(percentageScore);
                    } else {
                        LOGGER.info("Processing positive predictions...");
                        List<Long> predictedNewsIds = Arrays.stream(documentScores)
                                .filter(docScore -> !docScore.is_negative())
                                .map(DocumentScore::getId)
                                .toList();
                        List<News> predictedNews = newsRepository.findAllById(predictedNewsIds);
                        LOGGER.info("Found {} predicted (positive) news items", predictedNews.size());

                        response.setNewsWithScores(predictedNews, List.of(documentScores), pageable);
                        long negativeCount = predictedNews.size();
                        long totalCount = documentScores.length;
                        double negativePercentage = (totalCount > 0) ? (negativeCount / (double) totalCount) * 100 : 0;
                        double positivePercentage = 100 - negativePercentage;
                        PercentageScore percentageScore = new PercentageScore();
                        percentageScore.setNegative(negativePercentage);
                        percentageScore.setPositive(positivePercentage);
                        percentageScore.setOverall(totalCount);
                        percentageScore.setTag(tagRequest.getTag());
                        response.setPercentageScore(percentageScore);
                    }
                }
            }

            // If only negative filtering is requested
            if (filterRequest.isNeg()) {
                LOGGER.info("Processing solely negative filtering request...");
                TagRequest predictionRequest = new TagRequest();
                predictionRequest.setTag(tagRequest.getTag());
                predictionRequest.set_specific(tagRequest.is_specific());
                ResponseEntity<DocumentScore[]> modelResponse = restTemplate.postForEntity(fastApiUrl, predictionRequest, DocumentScore[].class);
                documentScores = modelResponse.getBody();
                int scoreCount = documentScores != null ? documentScores.length : 0;
                LOGGER.info("Received {} document scores for negative filtering", scoreCount);

                if (documentScores != null && documentScores.length > 0) {
                    List<Long> negativeNewsIds = Arrays.stream(documentScores)
                            .filter(DocumentScore::is_negative)
                            .map(DocumentScore::getId)
                            .toList();
                    List<News> negativeNews = newsRepository.findAllById(negativeNewsIds);
                    LOGGER.info("Found {} negative news items", negativeNews.size());
                    response.setNewsWithScores(negativeNews, List.of(documentScores), pageable);

                    long negativeCount = negativeNews.size();
                    long totalCount = documentScores.length;
                    double negativePercentage = (totalCount > 0) ? (negativeCount / (double) totalCount) * 100 : 0;
                    double positivePercentage = 100 - negativePercentage;
                    PercentageScore percentageScore = new PercentageScore();
                    percentageScore.setNegative(negativePercentage);
                    percentageScore.setPositive(positivePercentage);
                    percentageScore.setOverall(totalCount);
                    percentageScore.setTag(tagRequest.getTag());
                    response.setPercentageScore(percentageScore);
                }
            }

            LOGGER.info("Filtering news using model response...");
            filteredNews = newsRepository.filterNews(pageable, filterRequest, response.getOnlyNews());
            LOGGER.info("After filtering, found {} news items", filteredNews.getTotalElements());
            response.setNewsWithScores(filteredNews.getContent(), List.of(documentScores), pageable);
        } else {
            LOGGER.info("No prediction tag provided; applying direct filtering...");
            if ("allSources".equals(filterRequest.getSource_name())) {
                LOGGER.info("Fetching last news from all sources...");
                List<News> lastNews = newsRepository.getLastNews(pageable).getContent();
                filteredNews = newsRepository.filterNews(pageable, filterRequest, lastNews);
            } else {
                LOGGER.info("Filtering news for source: {}", filterRequest.getSource_name());
                filteredNews = newsRepository.findAll(NewsSpecification.filterByCriteria(filterRequest), pageable);
            }
            response.setNewsWithScores(filteredNews.getContent(), null, pageable);
        }
        LOGGER.info("getAllNewsFromSource completed; returning {} news items", response.getOnlyNews().size());
        return response;
    }

    @Override
    @Transactional(transactionManager = "newsTransactionManager")
    public Page<News> filter(FilterRequest filterRequest, Pageable pageable) {
        return newsRepository.findAll(NewsSpecification.filterByCriteria(filterRequest), pageable);
    }

    @Override
    @Transactional(transactionManager = "newsTransactionManager")
    public Page<News> lastNews(Pageable pageable) {
        return newsRepository.getLastNews(pageable);
    }

    @Override
    @Transactional(transactionManager = "newsTransactionManager")
    public Page<News> lastNewsOfSource(String source, Pageable pageable) {
        return newsRepository.getLastNewsOfSource(source, pageable);
    }

    @Override
    public News getNewsById(Long id) throws NotFoundException {
        return newsRepository.getNewsById(id).orElseThrow(() -> new NotFoundException("News not found.."));
    }

    @Override
    public PercentageScore predict(TagRequest request) {

        ResponseEntity<DocumentScore[]> response = restTemplate.postForEntity(fastApiUrl, request, DocumentScore[].class);
        DocumentScore[] modelResponse = response.getBody();

        if (modelResponse != null) {
            long negativeCount = Arrays.stream(modelResponse)
                    .filter(DocumentScore::is_negative)
                    .count();

            long totalCount = modelResponse.length;

            double negativePercentage = (totalCount > 0) ? (negativeCount / (double) totalCount) * 100 : 0;
            double positivePercentage = 100 - negativePercentage;

            PercentageScore percentageScore = new PercentageScore();
            percentageScore.setNegative(negativePercentage);
            percentageScore.setPositive(positivePercentage);
            percentageScore.setOverall(totalCount);
            percentageScore.setTag(request.getTag());
            return percentageScore;
        }
        return null;
    }


    @Override
    public List<News> allPredictedAnswers(TagRequest request) throws NotFoundException {

        ResponseEntity<DocumentScore[]> response = restTemplate.postForEntity(fastApiUrl, request, DocumentScore[].class);
        DocumentScore[] documentScores = response.getBody();

        List<News> selectedNews = new ArrayList<>();
        if (documentScores != null && documentScores.length > 0) {
            ModelResponse modelResponse = new ModelResponse();
            modelResponse.setDocument_scores(List.of(documentScores));

            for (DocumentScore documentScore : documentScores) {
                News news = getNewsById(documentScore.getId());
                selectedNews.add(news);
            }
        }
        return selectedNews;
    }
}
