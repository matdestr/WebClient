package be.kdg.kandoe.frontend.controller.resources.cards;


import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

public class CreateCardsResource {
    @NotEmpty(message = "{card.wrong.name}")
    private String name;
    @NotEmpty(message = "{card.wrong.url}")
    @URL
    private String imageUrl;
}
