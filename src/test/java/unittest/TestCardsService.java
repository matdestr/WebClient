package unittest;

import be.kdg.kandoe.backend.config.BackendContextConfig;
import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.*;
import be.kdg.kandoe.backend.service.exceptions.CardServiceException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

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
    private TopicService topicService;
    
    @Autowired
    private CardService cardService;

    private User user;
    private Organization organization1;
    private Category category1;
    private Category category2;

    private Topic topic1;
    private Topic topic2;
    private Topic topic3;
    
    @Before
    public void setUp(){
        User user = new User("username", "pass");
        this.user = userService.addUser(user);

        Organization organization = new Organization("test-organization1", user);
        this.organization1 = organizationService.addOrganization(organization);

        Category category = new Category();
        category.setOrganization(organization);
        category.setName("test-category1");
        category.setDescription("test-category1-description");
        this.category1 = categoryService.addCategory(category);
        
        Category otherCategory = new Category();
        otherCategory.setOrganization(organization);
        otherCategory.setName("test-category2");
        otherCategory.setDescription("test-category2-description");
        this.category2 = categoryService.addCategory(otherCategory);

        topic1 = new Topic();
        topic1.setCategory(this.category1);
        topic1.setName("My topic");
        topic1.setDescription("My description");

        topic2 = new Topic();
        topic2.setCategory(this.category1);
        topic2.setName("My second topic");
        topic2.setDescription("My second description");

        topic3 = new Topic();
        topic3.setCategory(this.category2);
        topic3.setName("My topic");
        topic3.setDescription("My description");
        
        topic1 = topicService.addTopic(topic1);
        topic2 = topicService.addTopic(topic2);
        topic3 = topicService.addTopic(topic3);
    }
    
    @Test
    public void addCardDetailsToTopic() {
        Set<CardDetails> validCardDetails = new HashSet<>();
        Set<Topic> topics = new HashSet<>();
        topics.add(topic1);
        
        CardDetails cardDetails1 = new CardDetails();
        cardDetails1.setText("My first card");
        cardDetails1.setCreator(this.user);
        
        CardDetails cardDetails2 = new CardDetails();
        cardDetails2.setText("My second card");
        cardDetails2.setCreator(this.user);
        
        validCardDetails.add(cardDetails1);
        validCardDetails.add(cardDetails2);
        
        cardDetails1 = cardService.addCardDetailsToTopic(topic1, cardDetails1);
        cardDetails2 = cardService.addCardDetailsToTopic(topic1, cardDetails2);

        Assert.assertNotNull(cardService.getCardDetailsOfTopic(topic1.getTopicId()));
        Assert.assertEquals(validCardDetails.size(), cardService.getCardDetailsOfTopic(topic1.getTopicId()).size());
        
        Assert.assertTrue(cardService.getCardDetailsOfTopic(topic1.getTopicId()).contains(cardDetails1));
        Assert.assertTrue(cardService.getCardDetailsOfTopic(topic1.getTopicId()).contains(cardDetails2));
        
        Assert.assertEquals(topics, cardDetails1.getTopics());
        Assert.assertEquals(topics, cardDetails2.getTopics());
    }
    
    @Test(expected = CardServiceException.class)
    public void addCardDetailsWithoutTopic() {
        CardDetails cardDetails1 = new CardDetails();
        cardDetails1.setText("My first card");
        cardDetails1.setCreator(this.user);
        
        cardService.addCardDetailsToTopic(null, cardDetails1);
    }

    @Test(expected = CardServiceException.class)
    public void addNullCardDetails() {
        cardService.addCardDetailsToTopic(topic1, null);
    }
    
    @Test(expected = CardServiceException.class)
    public void addCardDetailsWithoutText() {
        CardDetails cardDetails1 = new CardDetails();
        cardDetails1.setCreator(this.user);
        
        cardService.addCardDetailsToTopic(topic1, cardDetails1);
    }
    
    @Test(expected = CardServiceException.class)
    public void addCardDetailsWithExistingText() {
        CardDetails cardDetails1 = new CardDetails();
        cardDetails1.setCreator(user);
        cardDetails1.setText("Duplicate card text");
        
        CardDetails cardDetails2 = new CardDetails();
        cardDetails2.setCreator(user);
        cardDetails2.setText("Duplicate card text");

        cardService.addCardDetailsToTopic(topic1, cardDetails1);
        cardService.addCardDetailsToTopic(topic1, cardDetails2);
    }
    
    @Test
    public void addCardDetailsToMultipleTopics() {
        CardDetails cardDetails = new CardDetails();
        cardDetails.setCreator(this.user);
        cardDetails.setText("Some text");
        
        cardService.addCardDetailsToTopic(topic1, cardDetails);

        try {
            cardService.addCardDetailsToTopic(topic2, cardDetails);
        } catch (CardServiceException e) {
            // nothing to do
        }

        Assert.assertEquals(1, cardService.getCardDetailsOfTopic(topic1.getTopicId()).size());
        Assert.assertEquals(0, cardService.getCardDetailsOfTopic(topic2.getTopicId()).size());
        Assert.assertEquals(1, cardService.getCardDetailsOfCategory(topic1.getCategory().getCategoryId()).size());
    }
    
    @Test(expected = CardServiceException.class)
    public void addCardDetailsToTopicsOfDifferentCategories() {
        CardDetails cardDetails = new CardDetails();
        cardDetails.setCreator(this.user);
        cardDetails.setText("Some text");
        
        cardService.addCardDetailsToTopic(topic1, cardDetails);
        cardService.addCardDetailsToTopic(topic3, cardDetails);
    }
}
