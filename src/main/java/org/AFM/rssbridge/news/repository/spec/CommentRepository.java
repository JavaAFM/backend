package org.AFM.rssbridge.news.repository.spec;

import org.AFM.rssbridge.news.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
