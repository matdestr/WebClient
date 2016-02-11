package be.kdg.kandoe.backend.persistence;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.persistence.api.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class DatabaseSeeder {
    @Autowired
    private UserRepository userRepository;

    public DatabaseSeeder(){

    }

    @PostConstruct
    public void Seed(){
        userRepository.save(new User("user", "pass"));
    }
}
