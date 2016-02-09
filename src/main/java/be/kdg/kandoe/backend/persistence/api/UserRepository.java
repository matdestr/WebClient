package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByUsername(String username);
}
