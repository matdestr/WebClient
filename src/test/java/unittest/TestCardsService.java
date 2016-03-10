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
        Category sameCategory = category1;
        topic1.setCategory(sameCategory);

        Set<CardDetails> validCardDetails = new HashSet<>();
        Set<Topic> topics = new HashSet<>();
        topics.add(topic1);

        CardDetails cardDetails1 = new CardDetails();
        cardDetails1.setText("My first card");
        cardDetails1.setCreator(this.user);
        cardDetails1.setCategory(sameCategory);

        CardDetails cardDetails2 = new CardDetails();
        cardDetails2.setText("My second card");
        cardDetails2.setCreator(this.user);
        cardDetails2.setCategory(sameCategory);

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
        Category sameCategory = category1;

        topic1.setCategory(sameCategory);

        CardDetails cardDetails1 = new CardDetails();
        cardDetails1.setCreator(this.user);
        cardDetails1.setCategory(sameCategory);

        cardService.addCardDetailsToTopic(topic1, cardDetails1);
    }

    @Test(expected = CardServiceException.class)
    public void addCardDetailsWithExistingText() {
        Category sameCategory = category1;

        topic1.setCategory(sameCategory);

        CardDetails cardDetails1 = new CardDetails();
        cardDetails1.setCreator(user);
        cardDetails1.setText("Duplicate card text");
        cardDetails1.setCategory(sameCategory);

        CardDetails cardDetails2 = new CardDetails();
        cardDetails2.setCreator(user);
        cardDetails2.setText("Duplicate card text");
        cardDetails1.setCategory(sameCategory);

        cardService.addCardDetailsToTopic(topic1, cardDetails1);
        cardService.addCardDetailsToTopic(topic1, cardDetails2);
    }

    @Test
    public void addCardDetailsToMultipleTopics() {
        Category sameCategory = category1;

        topic1.setCategory(sameCategory);

        CardDetails cardDetails = new CardDetails();
        cardDetails.setCreator(this.user);
        cardDetails.setText("Some text");
        cardDetails.setCategory(sameCategory);

        cardService.addCardDetailsToTopic(topic1, cardDetails);

        cardService.addCardDetailsToTopic(topic2, cardDetails);

        Assert.assertEquals(1, cardService.getCardDetailsOfTopic(topic1.getTopicId()).size());
        Assert.assertEquals(1, cardService.getCardDetailsOfTopic(topic2.getTopicId()).size());
        Assert.assertEquals(1, cardService.getCardDetailsOfCategory(topic1.getCategory().getCategoryId()).size());
    }

    @Test(expected = CardServiceException.class)
    public void addCardDetailsToTopicsOfDifferentCategories() {
        Category category = category1;
        Category difCategory = category2;

        topic1.setCategory(category);
        topic3.setCategory(difCategory);

        CardDetails cardDetails = new CardDetails();
        cardDetails.setCreator(this.user);
        cardDetails.setText("Some text");
        cardDetails.setCategory(category);

        cardService.addCardDetailsToTopic(topic1, cardDetails);
        cardService.addCardDetailsToTopic(topic3, cardDetails);
    }
    
    @Test
    public void addCardDetailsToCategory() {
        CardDetails cardDetails = new CardDetails();
        cardDetails.setCreator(this.user);
        cardDetails.setText("Some text on a card");
        
        cardDetails = cardService.addCardDetailsToCategory(this.category1, cardDetails);
        
        Assert.assertEquals(1, this.cardService.getCardDetailsOfCategory(this.category1.getCategoryId()).size());
        Assert.assertTrue(this.cardService.getCardDetailsOfCategory(this.category1.getCategoryId()).contains(cardDetails));
    }
    
    @Test(expected = CardServiceException.class)
    public void addCardDetailsWithoutTextToCategory() {
        CardDetails cardDetails = new CardDetails();
        cardDetails.setCreator(this.user);
        
        cardDetails = cardService.addCardDetailsToCategory(this.category1, cardDetails);
        
        Assert.assertEquals(0, this.cardService.getCardDetailsOfCategory(this.category1.getCategoryId()).size());
        Assert.assertFalse(this.cardService.getCardDetailsOfCategory(this.category1.getCategoryId()).contains(cardDetails));
    }
    
    @Test
    public void addCardDetailsToTopicAndCategory() {
        topic1.setCategory(category1);
        topic2.setCategory(category1);

        CardDetails cardDetails1 = new CardDetails();
        cardDetails1.setCreator(this.user);
        cardDetails1.setText("My first card");
        
        CardDetails cardDetails2 = new CardDetails();
        cardDetails2.setCreator(this.user);
        cardDetails2.setText("My second card");

        cardDetails1 = this.cardService.addCardDetailsToCategory(category1, cardDetails1);
        cardDetails2 = this.cardService.addCardDetailsToCategory(category1, cardDetails2);

        Assert.assertEquals(2, this.cardService.getCardDetailsOfCategory(category1.getCategoryId()).size());
        Assert.assertTrue(this.cardService.getCardDetailsOfCategory(category1.getCategoryId()).contains(cardDetails1));
        Assert.assertTrue(this.cardService.getCardDetailsOfCategory(category1.getCategoryId()).contains(cardDetails2));


        cardDetails1 = this.cardService.addCardDetailsToTopic(topic1, cardDetails1);
        cardDetails2 = this.cardService.addCardDetailsToTopic(topic2, cardDetails2);


        Assert.assertEquals(1, this.cardService.getCardDetailsOfTopic(topic1.getTopicId()).size());
        Assert.assertTrue(this.cardService.getCardDetailsOfTopic(topic1.getTopicId()).contains(cardDetails1));
        Assert.assertTrue(this.cardService.getCardDetailsOfTopic(topic2.getTopicId()).contains(cardDetails2));
    }
}
