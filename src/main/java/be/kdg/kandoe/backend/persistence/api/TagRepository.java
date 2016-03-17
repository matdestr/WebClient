package be.kdg.kandoe.backend.persistence.api;


import be.kdg.kandoe.backend.model.organizations.Tag;
import be.kdg.kandoe.backend.model.sessions.Session;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Proxy interface for the CRUD Repository for the {@link Tag} model
 */

public interface TagRepository extends JpaRepository<Tag,Integer> {
}
