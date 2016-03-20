package be.kdg.kandoe.frontend.controller.resources.cards;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import javax.annotation.Nullable;

/**
 * Resource for the creation of a CardDetails
 */

@Data
@NoArgsConstructor
public class CreateCardDetailsResource {
    @NotEmpty(message = "{carddetails.wrong.text}")
    private String text;

    @URL(regexp = "^$|.*\\.(jpg|jpeg|png)$", message = "{carddetails.wrong.imageurl}")
    private String imageUrl;
}
