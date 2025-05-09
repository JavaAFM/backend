package org.AFM.rssbridge.news.repository.spec;

import org.AFM.rssbridge.news.model.News;
import org.AFM.rssbridge.news.repository.NewsRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News>, NewsRepo {
    @Query("SELECT n FROM News n ORDER BY n.publicationDate DESC")
    Page<News> findAll(Pageable pageable);

    @Query("SELECT n FROM News n ORDER BY n.publicationDate DESC")
    Page<News> getLastNews(Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.source.name = :source ORDER BY n.publicationDate DESC")
    Page<News> getLastNewsOfSource(@Param("source") String source, Pageable pageable);
    Optional<News> getNewsByTitle(String title);
    @Query("SELECT n FROM News n WHERE n.pre_is_negative = true ORDER BY n.publicationDate DESC")
    List<News> findNegativeNews(Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.pre_is_positive = true ORDER BY n.publicationDate DESC")
    List<News> findPositiveNews(Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.id IN :ids ORDER BY n.publicationDate DESC")
    List<News> findNewsByIds(@Param("ids") List<Long> ids);
}
