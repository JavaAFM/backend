package org.AFM.rssbridge.news.repository.spec;

import org.AFM.rssbridge.news.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SourceRepository extends JpaRepository<Source, Long> {
    Optional<Source> getSourceByName(String name);
}
