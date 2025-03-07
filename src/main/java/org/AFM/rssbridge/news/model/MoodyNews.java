package org.AFM.rssbridge.news.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

@Entity
@Table(name = "news")
@Immutable
@Data
public class MoodyNews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "news_title")
    private String title;

    @Column(name = "pre_is_negative")
    private boolean pre_is_negative;


    @Column(name = "pre_is_positive")
    private boolean pre_is_positive;
}
