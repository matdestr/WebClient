package unittest;

import be.kdg.kandoe.backend.config.BackendContextConfig;
import be.kdg.kandoe.backend.model.cards.Card;
import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.CardService;
import be.kdg.kandoe.backend.service.api.CategoryService;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.api.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BackendContextConfig.class })
@Transactional
public class TestCardsService {
    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CardService cardService;

    private User user;
    private Organization organization;
    private Category category;

    @Before
    public void setUp(){
        User user = new User("username", "pass");
        this.user = userService.addUser(user);

        Organization organization = new Organization("test-organization", user);
        this.organization = organizationService.addOrganization(organization);

        Category category = new Category();
        category.setOrganization(organization);
        category.setName("test-category");
        category.setDescription("test-category-description");
        this.category = categoryService.addCategory(category);
    }

    @Test
    public void addCard(){
        Card card = new Card();
        card.setCategory(this.category);
        card.setUser(this.user);

        CardDetails cardDetails = new CardDetails();
        cardDetails.setText("test-card");
        card.setCardDetails(cardDetails);



        Card savedCard =  cardService.addCard(card);
        Card fetchedCard = cardService.findCardById(savedCard.getCardId());

        assertEquals(savedCard.getCardId(), fetchedCard.getCardId());
    }
}
