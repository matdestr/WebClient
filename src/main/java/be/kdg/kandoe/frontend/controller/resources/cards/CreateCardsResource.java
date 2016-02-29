package be.kdg.kandoe.frontend.controller.resources.cards;


import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

public class CreateCardsResource {
    @NotEmpty
    private String name;
    @NotEmpty
    @URL
    private String imageUrl;
}
