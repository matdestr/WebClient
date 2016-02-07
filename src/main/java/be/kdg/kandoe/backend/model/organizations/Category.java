package be.kdg.kandoe.backend.model.organizations;

import java.util.List;

/**
 * Created by Vincent on 7/02/2016.
 */
public class Category {
    private String name;
    private String description;
    private Organization organization;
    private List<Tag> tags;
    private List<Topic> topics;
}
