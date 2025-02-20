package org.AFM.rssbridge.news.service;

import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.news.model.Tag;

import java.util.List;

public interface TagService {
    List<Tag> getAllTags();
    Tag getTagById(Long id) throws NotFoundException;
    Tag getTagByName(String name) throws NotFoundException;
    List<Tag> getTagsByGroup(String group);
}
