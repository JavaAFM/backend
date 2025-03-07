package org.AFM.rssbridge.news.service.impl;

import lombok.AllArgsConstructor;
import org.AFM.rssbridge.dto.request.FilterRequest;
import org.AFM.rssbridge.dto.request.TagRequest;
import org.AFM.rssbridge.dto.response.DocumentScore;
import org.AFM.rssbridge.dto.response.FilterResponse;
import org.AFM.rssbridge.dto.response.PercentageScore;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.news.model.News;
import org.AFM.rssbridge.news.repository.spec.NewsRepository;
import org.AFM.rssbridge.news.repository.NewsSpecification;
import org.AFM.rssbridge.news.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Service
@AllArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;

    private final RestTemplate restTemplate;
    private final String fastApiUrl = "http://192.168.122.104:8000/predict/";
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsServiceImpl.class);


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

        if (filterRequest.isNeg() || filterRequest.isPos() || tagRequest.getTag() != null || tagRequest.is_specific()) {

            DocumentScore[] documentScores;

            // If a prediction tag is provided
            if (tagRequest != null && tagRequest.getTag() != null && !tagRequest.getTag().isEmpty()) {
                LOGGER.info("Tag provided: {}. Sending predict request...", tagRequest.getTag());

                documentScores = fetchDocumentScoresWithTag(tagRequest);
                int scoreCount = documentScores.length!=0 ? documentScores.length : 0;
                LOGGER.info("Received {} document scores from predict endpoint", scoreCount);

                if (documentScores.length > 0) {
                    // If negative filter is requested
                    if (filterRequest.isNeg()) {
                        filterRequest.setNeg(false);
                        LOGGER.info("Processing negative predictions...");

                        List<News> negativeNews = getNews(documentScores, DocumentScore::is_negative);
                        LOGGER.info("Found {} negative news items", negativeNews.size());

                        negativeNews.sort(News::compareTo);

                        response.setNewsWithScores(negativeNews, List.of(documentScores), pageable);
                        PercentageScore percentageScore = getPercentageScore(tagRequest, negativeNews, documentScores);
                        response.setPercentageScore(percentageScore);
                        LOGGER.info("Filtering news using model response...");
                        filteredNews = newsRepository.filterNews(pageable, filterRequest, response.getOnlyNews());
                        LOGGER.info("After filtering, found {} news items", filteredNews.getTotalElements());
                        response.setNewsWithScores(filteredNews.getContent(), List.of(documentScores), pageable);
                        return response;
                    } else if (filterRequest.isPos()){
                        filterRequest.setPos(false);
                        LOGGER.info("Processing positive predictions...");

                        List<News> predictedNews = getNews(documentScores, DocumentScore::is_positive);
                        LOGGER.info("Found {} positive news items", predictedNews.size());

                        predictedNews.sort(News::compareTo);

                        response.setNewsWithScores(predictedNews, List.of(documentScores), pageable);
                        PercentageScore percentageScore = getPercentageScore(tagRequest, predictedNews, documentScores);
                        response.setPercentageScore(percentageScore);
                        LOGGER.info("Filtering news using model response...");
                        filteredNews = newsRepository.filterNews(pageable, filterRequest, response.getOnlyNews());
                        LOGGER.info("After filtering, found {} news items", filteredNews.getTotalElements());
                        response.setNewsWithScores(filteredNews.getContent(), List.of(documentScores), pageable);
                        return response;
                    }

                    else{
                        filterRequest.setPos(false);
                        filterRequest.setNeg(false);
                        LOGGER.info("Processing just predictions...");

                        List<News> predictedNews = getNews(documentScores, ds -> true);
                        LOGGER.info("Found {} predicted news items", predictedNews.size());

                        predictedNews.sort(News::compareTo);

                        response.setNewsWithScores(predictedNews, List.of(documentScores), pageable);
                        PercentageScore percentageScore = getPercentageScore(tagRequest, predictedNews, documentScores);
                        response.setPercentageScore(percentageScore);
                        LOGGER.info("Filtering news using model response...");
                        filteredNews = newsRepository.filterNews(pageable, filterRequest, response.getOnlyNews());
                        LOGGER.info("After filtering, found {} news items", filteredNews.getTotalElements());
                        response.setNewsWithScores(filteredNews.getContent(), List.of(documentScores), pageable);
                        return response;
                    }
                }
            }

            // If only negative filtering is requested
            if (filterRequest.isNeg()) {
                if(tagRequest == null || tagRequest.getTag() == null){
                    tagRequest.setTag("");
                }
                LOGGER.info("Processing solely negative filtering request...");

                documentScores = fetchDocumentScoresWithTag(tagRequest);
                int scoreCount = documentScores.length!=0 ? documentScores.length : 0;
                LOGGER.info("Received {} document scores for negative filtering", scoreCount);

                if (documentScores.length > 0) {
                    List<News> negativeNews = getNews(documentScores, DocumentScore::is_negative);
                    LOGGER.info("Found {} negative news items", negativeNews.size());
                    negativeNews.sort(News::compareTo);

                    response.setNewsWithScores(negativeNews, List.of(documentScores), pageable);
                    PercentageScore percentageScore = getPercentageScore(tagRequest, negativeNews, documentScores);
                    response.setPercentageScore(percentageScore);
                    LOGGER.info("Filtering news using model response...");
                    filteredNews = newsRepository.filterNews(pageable, filterRequest, response.getOnlyNews());
                    LOGGER.info("After filtering, found {} news items", filteredNews.getTotalElements());
                    response.setNewsWithScores(filteredNews.getContent(), List.of(documentScores), pageable);
                    return response;
                }
            }else if(filterRequest.isPos()){
                LOGGER.info("Processing solely positive filtering request...");
                if(tagRequest == null || tagRequest.getTag() == null){
                    tagRequest.setTag("");
                }

                documentScores = fetchDocumentScoresWithTag(tagRequest);
                int scoreCount = documentScores.length!=0 ? documentScores.length : 0;
                LOGGER.info("Received {} document scores for positive filtering", scoreCount);

                if (documentScores.length > 0) {
                    List<News> positiveNews = getNews(documentScores, DocumentScore::is_positive);
                    LOGGER.info("Found {} positive news items", positiveNews.size());
                    positiveNews.sort(News::compareTo);

                    response.setNewsWithScores(positiveNews, List.of(documentScores), pageable);
                    PercentageScore percentageScore = getPercentageScore(tagRequest, positiveNews, documentScores);
                    response.setPercentageScore(percentageScore);
                    LOGGER.info("Filtering news using model response...");
                    filteredNews = newsRepository.filterNews(pageable, filterRequest, response.getOnlyNews());
                    LOGGER.info("After filtering, found {} news items", filteredNews.getTotalElements());
                    response.setNewsWithScores(filteredNews.getContent(), List.of(documentScores), pageable);
                    return response;
                }
            }else {
                LOGGER.info("Processing solely filtering request...");
                if(tagRequest == null || tagRequest.getTag() == null){
                    tagRequest.setTag("");
                }
                documentScores = fetchDocumentScoresWithTag(tagRequest);
                int scoreCount = documentScores.length!=0 ? documentScores.length : 0;
                LOGGER.info("Received {} document scores for filtering", scoreCount);

                if (documentScores.length > 0) {
                    List<News> predictedNews = getNews(documentScores, ds -> true);
                    LOGGER.info("Found {} news items", predictedNews.size());
                    predictedNews.sort(News::compareTo);

                    response.setNewsWithScores(predictedNews, List.of(documentScores), pageable);
                    PercentageScore percentageScore = getPercentageScore(tagRequest, predictedNews, documentScores);
                    response.setPercentageScore(percentageScore);
                    LOGGER.info("Filtering news using model response...");
                    filteredNews = newsRepository.filterNews(pageable, filterRequest, response.getOnlyNews());
                    LOGGER.info("After filtering, found {} news items", filteredNews.getTotalElements());
                    response.setNewsWithScores(filteredNews.getContent(), List.of(documentScores), pageable);
                    return response;
                }
            }
        }
        else {
            LOGGER.info("No prediction tag provided; applying direct filtering...");
            if ("allSources".equals(filterRequest.getSource_name())) {
                LOGGER.info("Fetching last news from all sources...");
            } else {
                LOGGER.info("Filtering news for source: {}", filterRequest.getSource_name());
            }
            filteredNews = newsRepository.findAll(NewsSpecification.filterByCriteria(filterRequest), pageable);
            response.setNewsWithScores(filteredNews.getContent(), null, pageable);
        }
        LOGGER.info("getAllNewsFromSource completed; returning {} news items", response.getOnlyNews().size());
        return response;
    }
    private PercentageScore getPercentageScore(TagRequest tagRequest, List<News> negativeNews, DocumentScore[] documentScores) {
        long negativeCount = (negativeNews != null) ? negativeNews.size() : 0;
        long totalCount = (documentScores != null) ? documentScores.length : 0;

        double negativePercentage = (totalCount > 0) ? (negativeCount / (double) totalCount) * 100 : 0;
        double positivePercentage = 100 - negativePercentage;
        PercentageScore percentageScore = new PercentageScore();
        percentageScore.setNegative(negativePercentage);
        percentageScore.setPositive(positivePercentage);
        percentageScore.setOverall(totalCount);
        percentageScore.setTag(tagRequest.getTag());
        return percentageScore;
    }
    private DocumentScore[] fetchDocumentScoresWithTag(TagRequest tagRequest) {
        ResponseEntity<DocumentScore[]> modelResponse = restTemplate.postForEntity(fastApiUrl, tagRequest, DocumentScore[].class);
        if (modelResponse.getBody() == null) {
            LOGGER.error("Received null response from FastAPI predict endpoint");
            return new DocumentScore[0];
        }
        LOGGER.info("Received response from FastAPI: {}", Arrays.toString(modelResponse.getBody()));
        return modelResponse.getBody();
    }

    private List<News> getNews(DocumentScore[] documentScores, Predicate<DocumentScore> filterCondition) {
        List<DocumentScore> filteredScores = new ArrayList<>();
        for (DocumentScore ds : documentScores) {
            if (filterCondition.test(ds)) {
                filteredScores.add(ds);
            }
        }
        LOGGER.info("Filtered DocumentScores length: {}", filteredScores.size());

        List<Long> newsIds = new ArrayList<>();
        for (DocumentScore ds : filteredScores) {
            newsIds.add(ds.getId());
        }

        return newsRepository.findAllById(newsIds);
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
    public News findByTitle(String title) throws NotFoundException {
        return newsRepository.getNewsByTitle(title).orElseThrow(() -> new NotFoundException("News not found.."));
    }
}
