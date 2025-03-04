package org.AFM.rssbridge.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.news.model.News;
import org.AFM.rssbridge.news.service.NewsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsConsumer {

    private final MyRawWSHandler myRawWSHandler;
    private final NewsService newsService;

    @RabbitListener(queues = "newsQueue")
    public void receiveNewsNotification(String newsTitle) throws NotFoundException {
        log.info("Received news: {}", newsTitle);
        News news = newsService.findByTitle(newsTitle);
        myRawWSHandler.broadcastNewsMessage(news);
    }
}
