package org.AFM.rssbridge.news.repository.spec;

import org.AFM.rssbridge.news.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> getTagById(Long id);
    Optional<Tag> getTagByName(String name);
    List<Tag> getTagsByGroup(String group);
}
