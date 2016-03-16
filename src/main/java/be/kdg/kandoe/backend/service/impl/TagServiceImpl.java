package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.organizations.Tag;
import be.kdg.kandoe.backend.persistence.api.TagRepository;
import be.kdg.kandoe.backend.service.api.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    public Tag getTag(int tagId){
        return tagRepository.findOne(tagId);
    }
}
