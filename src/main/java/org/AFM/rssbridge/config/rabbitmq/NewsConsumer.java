package org.AFM.rssbridge.config.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AFM.rssbridge.config.socket.MyRawWSHandler;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.news.model.MoodyNews;
import org.AFM.rssbridge.news.model.News;
import org.AFM.rssbridge.news.service.MoodyNewsService;
import org.AFM.rssbridge.news.service.NewsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsConsumer {

    private final MyRawWSHandler myRawWSHandler;
    private final NewsService newsService;
    private final MoodyNewsService moodyNewsService;

    @RabbitListener(queues = "newsQueue")
    public void receiveNewsNotification(String newsTitle) throws NotFoundException, InterruptedException {
        log.info("Received news: {}", newsTitle);
        Thread.sleep(5000);
        MoodyNews moodyNews = moodyNewsService.getMoodyNewsByTitle(newsTitle);

        if(moodyNews.isPre_is_negative()){
            News news = newsService.findByTitle(newsTitle);
            myRawWSHandler.broadcastNewsMessage(news);
        }
    }

}
