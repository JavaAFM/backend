package org.AFM.rssbridge.news.service;

import org.AFM.rssbridge.news.model.MoodyNews;

public interface MoodyNewsService {
    MoodyNews getMoodyNewsByTitle(String title);
}
