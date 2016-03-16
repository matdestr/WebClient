package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Tag;

import java.util.List;

public interface TagService {
    List<Tag> getTags();
    Tag getTag(int tagId);

}
