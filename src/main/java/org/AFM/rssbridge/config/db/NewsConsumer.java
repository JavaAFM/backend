package org.AFM.rssbridge.config.db;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AFM.rssbridge.config.socket.MyRawWSHandler;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.news.model.News;
import org.AFM.rssbridge.news.service.NewsService;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsConsumer {

    @Value("${spring.datasource.news.url}")
    private String dbUrl;

    @Value("${spring.datasource.news.username}")
    private String dbUser;

    @Value("${spring.datasource.news.password}")
    private String dbPassword;

    @Value("${pg.listener.channel}")
    private String channelName;

    private final MyRawWSHandler myRawWSHandler;
    private final NewsService newsService;
    private final String name = "Persistent-Notification-Listener";

    @PostConstruct
    public void init() {
        new Thread(this::listenForever, name).start();
    }

    private void listenForever() {
        while (!Thread.currentThread().isInterrupted()) {
            Connection conn = null;
            try {
                conn = createConnection();
                conn.setAutoCommit(true);
                PGConnection pgConn = conn.unwrap(PGConnection.class);
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("LISTEN " + channelName);
                }
                log.info("Persistent listener started on channel" + channelName);

                try (Statement dummyStmt = conn.createStatement()) {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            dummyStmt.executeQuery("SELECT now()");
                            PGNotification[] notifications = pgConn.getNotifications();
                            if (notifications != null && notifications.length > 0) {
                                log.info("Found {} notification(s): {}", notifications.length, Arrays.toString(notifications));
                                for (PGNotification notification : notifications) {
                                    String newsIdStr = notification.getParameter();
                                    Long newsId = Long.parseLong(newsIdStr);
                                    processNews(newsId);
                                }
                            }
                        } catch (Exception innerEx) {
                            log.error("Error processing notifications: {}", innerEx.getMessage(), innerEx);
                        }
                        Thread.sleep(300);
                    }
                }
            } catch (Exception e) {
                log.error("Error in persistent listener loop: {}", e.getMessage(), e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        log.error("Error closing persistent connection: {}", e.getMessage(), e);
                    }
                }
            }
            log.info("Reconnecting to PostgreSQL in 2 seconds...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    private void processNews(Long id) throws NotFoundException {
        log.info("Processing negative news: {}", id);
        News news = newsService.findById(id);
        log.info("Fetched news: {}", news);
        if (news.getPre_is_negative()) {
            log.info("Sending news to websocket...");
            myRawWSHandler.broadcastNewsMessage(news);
        }
    }
}
