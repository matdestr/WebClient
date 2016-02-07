package be.kdg.kandoe.backend.model.organizations;

import be.kdg.kandoe.backend.model.users.User;

import java.util.List;

public class Organization {
    private String name;
    private User owner;
    private List<User> members;
}
