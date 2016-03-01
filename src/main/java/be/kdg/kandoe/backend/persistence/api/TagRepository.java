package be.kdg.kandoe.backend.persistence.api;


import be.kdg.kandoe.backend.model.organizations.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag,Integer> {
}
