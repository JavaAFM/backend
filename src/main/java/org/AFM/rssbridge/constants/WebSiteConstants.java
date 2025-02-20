package org.AFM.rssbridge.constants;

import lombok.Getter;

@Getter
public enum WebSiteConstants {
    TENGRI_NEWS("https://tengrinews.kz/news/"),
    TENGRI_MAIN("https://tengrinews.kz/"),
    KAZTAG_NEWS("https://kaztag.kz/ru/news/"),
    KAZTAG_MAIN("https://kaztag.kz"),
    AZATTYQ_NEWS("https://rus.azattyq.org/z/360"),
    AZATTYQ_MAIN("https://rus.azattyq.org/"),
    ORDA_NEWS("https://orda.kz/last-news/"),
    ORDA_MAIN("https://orda.kz/"),
    ZAKON_NEWS("https://www.zakon.kz/news/"),
    ZAKON_MAIN("https://www.zakon.kz/");

    private String label;
    WebSiteConstants(String label) {
        this.label = label;
    }
}
