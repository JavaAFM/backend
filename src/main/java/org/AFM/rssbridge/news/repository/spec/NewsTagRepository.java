package org.AFM.rssbridge.news.repository.spec;

import org.AFM.rssbridge.news.model.NewsTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsTagRepository extends JpaRepository<NewsTag, Long> {
}
