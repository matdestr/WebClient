package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.CardService;
import be.kdg.kandoe.backend.service.api.CategoryService;
import be.kdg.kandoe.backend.service.api.SessionService;
import be.kdg.kandoe.backend.service.api.TopicService;
import be.kdg.kandoe.frontend.controller.resources.cards.CardDetailsResource;
import be.kdg.kandoe.frontend.controller.resources.cards.CreateCardDetailsResource;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/carddetails")
public class CardDetailsRestController {
    private CardService cardService;
    private TopicService topicService;
    private CategoryService categoryService;
    private SessionService sessionService;

    private MapperFacade mapperFacade;

    @Autowired
    public CardDetailsRestController(CardService cardService, TopicService topicService,
                                     CategoryService categoryService, SessionService sessionService, 
                                     MapperFacade mapperFacade) {
        this.cardService = cardService;
        this.topicService = topicService;
        this.categoryService = categoryService;
        this.sessionService = sessionService;
        this.mapperFacade = mapperFacade;
    }

    // TODO : Write test + implement later
    /*@RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<CardDetailsResource>> getCardDetailsOfSession(@AuthenticationPrincipal User user, @RequestParam("sessionId") int sessionId) {
        
    }*/
    
    @RequestMapping(value = "/topics/{topicId}", method = RequestMethod.GET)
    public ResponseEntity<List<CardDetailsResource>> getCardDetailsOfTopic(@AuthenticationPrincipal User user,
                                                                           @PathVariable("topicId") int topicId) {
        Organization organization = this.topicService.getTopicByTopicId(topicId).getCategory().getOrganization();

        if (!this.checkUserIsOrganizer(user, organization)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Set<CardDetails> cardDetailsSet = cardService.getCardDetailsOfTopic(topicId);

        return new ResponseEntity<>(mapperFacade.mapAsList(cardDetailsSet, CardDetailsResource.class), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.GET)
    public ResponseEntity<List<CardDetailsResource>> getCardDetailsOfCategory(@AuthenticationPrincipal User user,
                                                                              @PathVariable("categoryId") int categoryId) {
        Category category = this.categoryService.getCategoryById(categoryId);

        if (!this.checkUserIsOrganizer(user, category.getOrganization()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        
        Set<CardDetails> cardDetailsSet = cardService.getCardDetailsOfCategory(categoryId);

        return new ResponseEntity<>(mapperFacade.mapAsList(cardDetailsSet, CardDetailsResource.class), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<CardDetailsResource> createCardDetails(@AuthenticationPrincipal User user,
                                                                 @RequestParam("topicId") int topicId,
                                                                 @Valid @RequestBody CreateCardDetailsResource resource) {
        Organization organization = this.topicService.getTopicByTopicId(topicId).getCategory().getOrganization();

        if (!this.checkUserIsOrganizer(user, organization))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        CardDetails cardDetails = mapperFacade.map(resource, CardDetails.class);
        cardDetails.setCreator(user);

        Topic topic = this.topicService.getTopicByTopicId(topicId);
        cardDetails = this.cardService.addCardDetailsToTopic(topic, cardDetails);

        return new ResponseEntity<>(mapperFacade.map(cardDetails, CardDetailsResource.class), HttpStatus.CREATED);
    }

    private boolean checkUserIsOrganizer(User user, Organization organization) {
        if (organization.getOwner().equals(user) || organization.getOrganizers().contains(user))
            return true;

        return false;
    }
    
    private boolean checkUserIsMember(User user, Session session) {
        return session.getParticipants().contains(user);
    }
}
