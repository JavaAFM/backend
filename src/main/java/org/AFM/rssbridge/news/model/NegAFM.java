package org.AFM.rssbridge.news.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name = "notification_neg_afm")
public class NegAFM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artice_id")
    private Long article_id;
}
