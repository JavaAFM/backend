package org.AFM.rssbridge.news.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name = "sources")
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "link")
    private String link;
}
