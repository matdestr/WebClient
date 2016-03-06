package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.Invitation;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.EmailService;
import be.kdg.kandoe.backend.service.api.InvitationService;
import be.kdg.kandoe.backend.service.properties.MailProperties;
import be.kdg.kandoe.backend.utils.JobScheduler;
import be.kdg.kandoe.backend.utils.jobs.MailJob;
import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.TransportStrategy;
import org.codemonkey.simplejavamail.email.Email;
import org.springframework.beans.factory.annotation.Autowired;
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
    private JobScheduler jobScheduler;

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
                jobScheduler.scheduleJob(new MailJob(mailer, email), new Date());
            }
        }
    }

    @Override
    public void inviteUsersToOrganization(Organization organization, User requester, List<User> users) {
        for (User user : users) {
            String emailAddress = user.getEmail();

            if (emailAddress != null && !emailAddress.isEmpty()) {

                Invitation invitation = invitationService.generateInvitation(user, organization);

                String url = createAcceptUrl(invitation.getAcceptId(), invitation.getOrganization().getOrganizationId());

                Email email = new Email();
                email.setFromAddress("CanDo Team E", mailProperties.getUsername());
                email.setSubject("CanDo: Invitation to join organization " + organization.getName());
                email.addRecipient("", emailAddress, Message.RecipientType.TO);
                email.setTextHTML("<body style=\"font-family: Arial;\">Hi,<br><br>You have been invited to join the CanDo organization <b>" + organization.getName() + "</b> by <b>" + requester.getName() + " " + requester.getSurname() + ".</b><br>You can join by clicking the following link: <a href=\"" + url + "\">" + url + "</a>.<br><br>Regards,<br>Team Cando</body>");

                jobScheduler.scheduleJob(new MailJob(mailer, email), new Date());
            }
        }
    }

    private String createAcceptUrl(String acceptId, int organizationId){
        String baseUrl = System.getProperty("app.baseUrl");
        String acceptUrl = baseUrl == null ? "http://localhost:8080/kandoe" : baseUrl;
        acceptUrl += String.format("/#/organization/accept?acceptId=%s&organizationId=%d", acceptId, organizationId);

        return acceptUrl;
    }
}
