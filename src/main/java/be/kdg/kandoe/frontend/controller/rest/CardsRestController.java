package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.frontend.controller.resources.cards.CreateCardsResource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/cards")
public class CardsRestController {


    @RequestMapping(method = RequestMethod.POST)
    public void createCards(@AuthenticationPrincipal User user, @Valid CreateCardsResource cardsResource){

    }
}
