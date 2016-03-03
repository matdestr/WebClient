package be.kdg.kandoe.backend.model.users;

import be.kdg.kandoe.backend.model.organizations.Organization;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Invitation implements Serializable {
    @Id
    @GeneratedValue
    @Setter(value = AccessLevel.NONE)
    private Integer invitationId;

    @OneToOne(fetch = FetchType.EAGER)
    private User invitedUser;

    @OneToOne(fetch = FetchType.EAGER)
    private Organization organization;

    @Column(nullable = false, updatable = false, unique = true)
    private String acceptId;

    private boolean accepted;
}
