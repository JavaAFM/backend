package org.AFM.rssbridge.news.model;

import jakarta.persistence.*;
import lombok.ToString;
import lombok.Data;

@Data
@ToString
@Entity
@Table(name = "main_tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name")
    private String group;

    @Column(name = "main_tag_name")
    private String name;
}
