package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.Invitation;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.persistence.api.SessionRepository;
import be.kdg.kandoe.backend.service.api.EmailService;
import be.kdg.kandoe.backend.service.api.InvitationService;
import be.kdg.kandoe.backend.service.properties.MailProperties;
import be.kdg.kandoe.backend.utils.JobScheduler;
import be.kdg.kandoe.backend.utils.jobs.MailJob;
import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.TransportStrategy;
import org.codemonkey.simplejavamail.email.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import java.util.Date;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private MailProperties mailProperties;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private JobScheduler jobScheduler;

    private Mailer mailer;

    @Value("${app.baseUrl}")
    private String baseUrl;

    @PostConstruct
    public void initializeMailer(){
        this.mailer = new Mailer(
                mailProperties.getHost(),
                mailProperties.getPort(),
                mailProperties.getUsername(),
                mailProperties.getPassword(),
                TransportStrategy.SMTP_SSL);
    }

    @Override
    public void inviteUnexistingUsersToOrganization(Organization organization, User requester, List<String> emails) {
        for (String emailAddress : emails) {
            if (emailAddress != null) {
                Email email = new Email();

                invitationService.generateInvitationForUnexistingUser(emailAddress, organization);

                System.out.println("Invitation created for " + emailAddress);

                email.setFromAddress("CanDo Team E", mailProperties.getUsername());
                email.setSubject("CanDo: Invitation to join organization " + organization.getName());
                email.addRecipient("", emailAddress, Message.RecipientType.TO);
                email.setTextHTML("<body style=\"font-family: Arial;\">Hi,<br><br>You have been invited to join the CanDo organization <b>" + organization.getName() + "</b> by <b>" + requester.getName() + " " + requester.getSurname() + ".</b><br>You can join by creating an account here: " + baseUrl + ". You can accept the invitation in your newly created profile.<br><br>Regards,<br>Team Cando</body>");

                jobScheduler.scheduleJob(new MailJob(mailer, email), new Date());
            }
        }
    }

    @Override
    public void inviteUsersToOrganization(Organization organization, User requester, List<User> users) {
        for (User user : users) {
            String emailAddress = user.getEmail();

            if (emailAddress != null && !emailAddress.isEmpty()) {

                invitationService.generateInvitation(user, organization);

                String profileUrl = String.format("<a href=\"%s/profile?username=%s\">here</a>", baseUrl, user.getUsername());

                Email email = new Email();
                email.setFromAddress("CanDo Team E", mailProperties.getUsername());
                email.setSubject("CanDo: Invitation to join organization " + organization.getName());
                email.addRecipient("", emailAddress, Message.RecipientType.TO);
                email.setTextHTML("<body style=\"font-family: Arial;\">Hi,<br><br>You have been invited to join the CanDo organization <b>" + organization.getName() + "</b> by <b>" + requester.getName() + " " + requester.getSurname() + ".</b><br>You can join by checking your profile " + profileUrl + ".<br><br>Regards,<br>Team Cando</body>");

                jobScheduler.scheduleJob(new MailJob(mailer, email), new Date());
            }
        }
    }

    @Override
    public void sendSessionInvitationToUser(Session session, User organizer, User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty())
            return;

        // TODO : Find better solution for user without email

        String invitationUrl = baseUrl + String.format("sessions/%s/join", session.getSessionId());

        Email email = new Email();

        email.setFromAddress("CanDo Team E", mailProperties.getUsername());
        email.setSubject("CanDo: Invitation to join session");
        email.addRecipient("", user.getEmail(), Message.RecipientType.TO);
        email.setTextHTML(
                String.format("\"<body style=\"font-family: Arial;\">" +
                        "<p>Hello %s</p>" +
                        "<p>%s has invited you to join their session." +
                        "<br>You can accept the invite by clicking <a href=\"%s\">this link</a></p>" +
                        "<p>Team CanDo</p>", user.getName(), organizer.getName(), invitationUrl)
        );

        jobScheduler.scheduleJob(new MailJob(mailer, email), new Date());
    }
}
