package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.organizations.Tag;
import be.kdg.kandoe.backend.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Proxy interface for the CRUD Repository for the {@link User} model
 */

public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByUsername(String username);
    User findUserByEmail(String email);
}
