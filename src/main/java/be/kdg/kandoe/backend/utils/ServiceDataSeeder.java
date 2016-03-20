package be.kdg.kandoe.backend.utils;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.model.users.roles.RoleType;
import be.kdg.kandoe.backend.service.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Seeds the application with default data.
 * */
@Component
public class ServiceDataSeeder {
    private UserService userService;
    private OrganizationService organizationService;
    private CategoryService categoryService;
    private TopicService topicService;
    private TagService tagService;
    private CardService cardService;
    private SessionService sessionService;

    @Autowired
    public ServiceDataSeeder(UserService userService, OrganizationService organizationService,
                             CategoryService categoryService, TopicService topicService,
                             TagService tagService, CardService cardService, SessionService sessionService) {
        this.userService = userService;
        this.organizationService = organizationService;
        this.categoryService = categoryService;
        this.topicService = topicService;
        this.tagService = tagService;
        this.cardService = cardService;
        this.sessionService = sessionService;
    }

    @PostConstruct
    private void seedData() {
        User testUser = new User("user", "pass");
        testUser.setName("Dummy");
        testUser.setSurname("User");
        testUser.setEmail("testuser@localhost.com");
        testUser.setProfilePictureUrl("profilepictures/default.png");
        testUser.addRole(RoleType.ROLE_CLIENT);

        User harold = new User("harold", "harold");
        harold.setName("Harold");
        harold.setSurname("Painhider");
        harold.setEmail("harold@haroldmail.pain");
        harold.setProfilePictureUrl("profilepictures/harold.jpg");
        harold.addRole(RoleType.ROLE_CLIENT);

        testUser = userService.addUser(testUser);
        harold = userService.addUser(harold);

        Organization organizationKdG = new Organization("KdG", testUser);
        organizationKdG.addOrganizer(harold);
        organizationKdG = organizationService.addOrganization(organizationKdG);

        Organization organizationGovernment = new Organization("Government", harold);
        organizationGovernment.addOrganizer(testUser);
        organizationGovernment = organizationService.addOrganization(organizationGovernment);

        Category categoryEducation = new Category();
        categoryEducation.setOrganization(organizationKdG);
        categoryEducation.setName("Education");
        categoryEducation.setDescription("What are the most important competences for graduating IT students?");
        categoryEducation = categoryService.addCategory(categoryEducation);

        Category categoryCommunication = new Category();
        categoryCommunication.setOrganization(organizationKdG);
        categoryCommunication.setName("Communication");
        categoryCommunication.setDescription("Communication at Karel De Grote");
        categoryCommunication = categoryService.addCategory(categoryCommunication);

        Category categoryTravel = new Category();
        categoryTravel.setOrganization(organizationGovernment);
        categoryTravel.setName("Traveling, passports and living abroad");
        categoryTravel.setDescription("Includes renewing passports and travel advice");
        categoryTravel = categoryService.addCategory(categoryTravel);

        Category categoryMoneyAndTax = new Category();
        categoryMoneyAndTax.setOrganization(organizationGovernment);
        categoryMoneyAndTax.setName("Money and tax");
        categoryMoneyAndTax.setDescription("Self assessment and more");
        categoryMoneyAndTax = categoryService.addCategory(categoryMoneyAndTax);

        /*Topic topicAppliedInformatics = new Topic();
        topicAppliedInformatics.setCategory(categoryEducation);
        topicAppliedInformatics.setName("Applied Informatics");
        topicAppliedInformatics.setDescription("What is important in IT education?");
        topicAppliedInformatics = topicService.addTopic(topicAppliedInformatics);

        Topic topicMedia = new Topic();
        topicMedia.setCategory(categoryCommunication);
        topicMedia.setName("Media");
        topicMedia.setDescription("Which types of media does KdG need to use to communicate?");
        topicMedia = topicService.addTopic(topicMedia);*/

        Topic topicTax = new Topic();
        topicTax.setCategory(categoryMoneyAndTax);
        topicTax.setName("Self assessment");
        topicTax.setDescription("How can we make the self assessment process easier?");
        topicTax = topicService.addTopic(topicTax);

        Topic topicPassportProcess = new Topic();
        topicPassportProcess.setCategory(categoryTravel);
        topicPassportProcess.setName("Applying for a passport");
        topicPassportProcess.setDescription("What can be improved in the process of applying for a passport?");
        topicPassportProcess = topicService.addTopic(topicPassportProcess);

        seedCardDetailsKdGEducation(categoryEducation, testUser);
    }

    private void seedCardDetailsKdGEducation(Category category, User creator) {
        CardDetails cardDetails1 = new CardDetails();
        //cardDetails1.setCategory(category);
        cardDetails1.setCreator(creator);
        cardDetails1.setText("KDG");
        cardDetails1.setImageUrl("https://pbs.twimg.com/profile_images/664027982718177280/YUs5qbQb.png");

        CardDetails cardDetails2 = new CardDetails();
        //cardDetails2.setCategory(category);
        cardDetails2.setCreator(creator);
        cardDetails2.setText("ICT");
        cardDetails2.setImageUrl("http://www.themiddleeastmagazine.com/wp-content/uploads/2014/11/ICT-image.jpg");

        CardDetails cardDetails3 = new CardDetails();
        //cardDetails3.setCategory(category);
        cardDetails3.setCreator(creator);
        cardDetails3.setText("Marketing");
        cardDetails3.setImageUrl("http://fortunednagroup.com/wp-content/uploads/marketing1.jpg");

        CardDetails cardDetails4 = new CardDetails();
        //cardDetails4.setCategory(category);
        cardDetails4.setCreator(creator);
        cardDetails4.setText("School");
        cardDetails4.setImageUrl("http://cdn.mtlblog.com/uploads/2015/08/students-during-the-lecture.jpg");

        CardDetails cardDetails5 = new CardDetails();
        //cardDetails5.setCategory(category);
        cardDetails5.setCreator(creator);
        cardDetails5.setText("Several types of media");
        cardDetails5.setImageUrl("http://www.wietblog.com/wp-content/uploads/2015/06/EU-Media-Futures-Forum-pic_0.jpg");

        CardDetails cardDetails6 = new CardDetails();
        //cardDetails6.setCategory(category);
        cardDetails6.setCreator(creator);
        cardDetails6.setText("Social media");
        cardDetails6.setImageUrl("http://blogs-images.forbes.com/insider/files/2014/11/social_media_strategy111.jpg");

        CardDetails cardDetails7 = new CardDetails();
        //cardDetails7.setCategory(category);
        cardDetails7.setCreator(creator);
        cardDetails7.setText("Communication skills");
        cardDetails7.setImageUrl("http://www.bestadvice.co.uk/wp-content/uploads/2012/11/listening-feedback-communication.jpg");

        CardDetails cardDetails8 = new CardDetails();
        //cardDetails8.setCategory(category);
        cardDetails8.setCreator(creator);
        cardDetails8.setText("Financial management");
        cardDetails8.setImageUrl("https://developer.ibm.com/apimanagement/wp-content/uploads/sites/23/2015/03/financial-services.png");

        CardDetails cardDetails9 = new CardDetails();
        //cardDetails9.setCategory(category);
        cardDetails9.setCreator(creator);
        cardDetails9.setText("Collaborating with other people");
        cardDetails9.setImageUrl("http://www.phoenix.k12.or.us/SIB/images/collaboration%20image%202.jpg");

        CardDetails cardDetails10 = new CardDetails();
        //cardDetails10.setCategory(category);
        cardDetails10.setCreator(creator);
        cardDetails10.setText("Functional programming");

        CardDetails cardDetails11 = new CardDetails();
        //cardDetails11.setCategory(category);
        cardDetails11.setCreator(creator);
        cardDetails11.setText("Object oriented programming");

        CardDetails cardDetails12 = new CardDetails();
        //cardDetails12.setCategory(category);
        cardDetails12.setCreator(creator);
        cardDetails12.setText("SQL Databases");
        cardDetails12.setImageUrl("http://www.a2ftweaks.com/img/network-database.png");

        // TODO : When there are problems with this, just add the cards to the topic without adding them to the category first (weird Hibernate bug)
        cardDetails1 = cardService.addCardDetailsToCategory(category, cardDetails1);
        cardDetails2 = cardService.addCardDetailsToCategory(category, cardDetails2);
        cardDetails3 = cardService.addCardDetailsToCategory(category, cardDetails3);
        cardDetails4 = cardService.addCardDetailsToCategory(category, cardDetails4);
        cardDetails5 = cardService.addCardDetailsToCategory(category, cardDetails5);
        cardDetails6 = cardService.addCardDetailsToCategory(category, cardDetails6);
        cardDetails7 = cardService.addCardDetailsToCategory(category, cardDetails7);
        cardDetails8 = cardService.addCardDetailsToCategory(category, cardDetails8);
        cardDetails9 = cardService.addCardDetailsToCategory(category, cardDetails9);
        cardDetails10 = cardService.addCardDetailsToCategory(category, cardDetails10);
        cardDetails11 = cardService.addCardDetailsToCategory(category, cardDetails11);
        cardDetails12 = cardService.addCardDetailsToCategory(category, cardDetails12);

        Topic topicBusinessKnowledge = new Topic();
        topicBusinessKnowledge.setCategory(category);
        topicBusinessKnowledge.setName("Business knowledge");
        topicBusinessKnowledge.setDescription("What is the most important business knowledge?");
        topicBusinessKnowledge = topicService.addTopic(topicBusinessKnowledge);

        Topic topicTechnicalKnowledge = new Topic();
        topicTechnicalKnowledge.setCategory(category);
        topicTechnicalKnowledge.setName("Technical knowledge");
        topicTechnicalKnowledge.setDescription("What is the most important technical knowledge?");
        topicTechnicalKnowledge = topicService.addTopic(topicTechnicalKnowledge);

        /*List<CardDetails> cardDetailsTopicBusinessKnowledge = new ArrayList<>();
        cardDetailsTopicBusinessKnowledge.add(cardDetails2);
        cardDetailsTopicBusinessKnowledge.add(cardDetails3);
        cardDetailsTopicBusinessKnowledge.add(cardDetails7);
        cardDetailsTopicBusinessKnowledge.add(cardDetails8);
        cardDetailsTopicBusinessKnowledge.add(cardDetails9);*/

        cardService.addCardDetailsToTopic(topicBusinessKnowledge, cardDetails2);
        topicBusinessKnowledge = topicService.getTopicByTopicId(topicBusinessKnowledge.getTopicId());
        cardService.addCardDetailsToTopic(topicBusinessKnowledge, cardDetails3);
        topicBusinessKnowledge = topicService.getTopicByTopicId(topicBusinessKnowledge.getTopicId());
        cardService.addCardDetailsToTopic(topicBusinessKnowledge, cardDetails7);
        topicBusinessKnowledge = topicService.getTopicByTopicId(topicBusinessKnowledge.getTopicId());
        cardService.addCardDetailsToTopic(topicBusinessKnowledge, cardDetails8);
        topicBusinessKnowledge = topicService.getTopicByTopicId(topicBusinessKnowledge.getTopicId());
        cardService.addCardDetailsToTopic(topicBusinessKnowledge, cardDetails9);

        //cardService.addAllCardDetailsToTopic(topicBusinessKnowledge, cardDetailsTopicBusinessKnowledge);

        cardService.addCardDetailsToTopic(topicTechnicalKnowledge, cardDetails2);
        topicTechnicalKnowledge = topicService.getTopicByTopicId(topicTechnicalKnowledge.getTopicId());
        cardService.addCardDetailsToTopic(topicTechnicalKnowledge, cardDetails6);
        topicTechnicalKnowledge = topicService.getTopicByTopicId(topicTechnicalKnowledge.getTopicId());
        cardService.addCardDetailsToTopic(topicTechnicalKnowledge, cardDetails9);
        topicTechnicalKnowledge = topicService.getTopicByTopicId(topicTechnicalKnowledge.getTopicId());
        cardService.addCardDetailsToTopic(topicTechnicalKnowledge, cardDetails10);
        topicTechnicalKnowledge = topicService.getTopicByTopicId(topicTechnicalKnowledge.getTopicId());
        cardService.addCardDetailsToTopic(topicTechnicalKnowledge, cardDetails11);
        topicTechnicalKnowledge = topicService.getTopicByTopicId(topicTechnicalKnowledge.getTopicId());
        cardService.addCardDetailsToTopic(topicTechnicalKnowledge, cardDetails12);

        /*List<CardDetails> cardDetailsTopicTechnicalKnowledge = new ArrayList<>();
        cardDetailsTopicTechnicalKnowledge.add(cardDetails2);
        cardDetailsTopicTechnicalKnowledge.add(cardDetails6);
        cardDetailsTopicTechnicalKnowledge.add(cardDetails9);
        cardDetailsTopicTechnicalKnowledge.add(cardDetails10);
        cardDetailsTopicTechnicalKnowledge.add(cardDetails11);
        cardDetailsTopicTechnicalKnowledge.add(cardDetails12);

        cardService.addAllCardDetailsToTopic(topicTechnicalKnowledge, cardDetailsTopicTechnicalKnowledge);*/
    }
}
