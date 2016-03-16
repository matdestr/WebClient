package be.kdg.kandoe.frontend.controller.resources.organizations.categories;

import be.kdg.kandoe.backend.model.organizations.Tag;
import lombok.Data;

import java.util.List;

@Data
public class TagResource {
    private String name;
    private int tagId;
}
