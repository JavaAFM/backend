package org.AFM.rssbridge.news.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@Entity
@Table(name = "news")
public class News implements Comparable<News>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "news_title")
    private String title;

    @Column(name = "news_link")
    private String url;

    @ManyToOne
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;

    @Column(name = "summary")
    private String summary;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<NewsTag> tags;

    @Column(name = "news_body")
    private String mainText;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Comment> comments;

    @Column(name = "created_at")
    private LocalDateTime publicationDate;

    @Column(name = "pre_is_negative")
    private Boolean pre_is_negative;

    @Column(name = "pre_is_positive")
    private Boolean pre_is_positive;

    @Override
    public int compareTo(News other) {
        return other.publicationDate.compareTo(this.publicationDate);
    }
}
