package be.kdg.kandoe.frontend.controller.resources.sessions.create;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.AssertTrue;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value =
        CreateAsynchronousSessionResource.class, name = CreateAsynchronousSessionResource.TYPE), @JsonSubTypes.Type(value = CreateSynchronousSessionResource.class, name = CreateSynchronousSessionResource.TYPE) })
public abstract class CreateSessionResource {
    private Integer topicId;
    @Range(min = 1, message = "{session.wrong.organization-id}")
    private int organizationId;
    private boolean commentsAllowed;
    private boolean userCanAddCards;
    @Range(min = 1, max = 100, message = "{session.wrong.min-cardnumber}")
    private int minNumberOfCards;
    @Range(min = 1, max = 100, message = "{session.wrong.max-cardnumber}")
    private int maxNumberOfCards;

    private final String type;

    protected CreateSessionResource(String type) {
        this.type = type;
    }

    @AssertTrue(message = "{session.wrong.invalid-cardnumber}")
    private boolean isValid() {
        return minNumberOfCards <= maxNumberOfCards;
    }
}
