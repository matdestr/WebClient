package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Tag;

import java.util.List;

public interface TagService {
    /**
     * Retrieves a list of all existing tags.
     * */
    List<Tag> getTags();
    
    /**
     * Retrieves a tag by its given unique ID.
     * */
    Tag getTag(int tagId);
}
