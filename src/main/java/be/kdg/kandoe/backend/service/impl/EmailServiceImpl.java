package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.EmailService;
import be.kdg.kandoe.backend.service.properties.MailProperties;
import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.TransportStrategy;
import org.codemonkey.simplejavamail.email.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private MailProperties mailProperties;

    private Mailer mailer;

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
                email.setFromAddress("CanDo Team E", mailProperties.getUsername());
                email.setSubject("CanDo: Invitation to join organization " + organization.getName());
                email.addRecipient("", emailAddress, Message.RecipientType.TO);
                email.setTextHTML("<body style=\"font-family: Arial;\">Hi,<br><br>You have been invited to join the CanDo organization <b>" + organization.getName() + "</b> by <b>" + requester.getName() + " " + requester.getSurname() + ".</b><br><br>Regards,<br>Team Cando</body>");
                mailer.sendMail(email);
            }
        }
    }

    @Override
    public void inviteUsersToOrganization(Organization organization, User requester, List<User> users) {
        for (User user : users) {
            String emailAddress = user.getEmail();

            if (emailAddress != null && !emailAddress.isEmpty()) {
                Email email = new Email();
                email.setFromAddress("CanDo Team E", mailProperties.getUsername());
                email.setSubject("CanDo: Invitation to join organization " + organization.getName());
                email.setTextHTML("<body style=\"font-family: Arial;\">Hi,<br><br>You have been invited to join the CanDo organization <b>" + organization.getName() + "</b> by <b>" + requester.getName() + " " + requester.getSurname() + ".</b><br>You can join by clicking the following link: <br><br>Regards,<br>Team Cando</body>");
                email.addRecipient("", emailAddress, Message.RecipientType.TO);

                mailer.sendMail(email);
            }
        }
    }
}
