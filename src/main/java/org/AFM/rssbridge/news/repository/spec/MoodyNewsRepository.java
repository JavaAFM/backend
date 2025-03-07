package org.AFM.rssbridge.news.repository.spec;

import org.AFM.rssbridge.news.model.MoodyNews;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MoodyNewsRepository extends JpaRepository<MoodyNews, Long> {
    MoodyNews getMoodyNewsByTitle(String title);
}
