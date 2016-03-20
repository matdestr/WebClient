package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.sessions.AsynchronousSession;
import be.kdg.kandoe.backend.model.sessions.ParticipantInfo;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.AsynchronousSessionSchedulerService;
import be.kdg.kandoe.backend.service.api.SessionGameService;
import be.kdg.kandoe.backend.service.api.SessionService;
import be.kdg.kandoe.backend.service.properties.MailProperties;
import be.kdg.kandoe.backend.utils.JobScheduler;
import be.kdg.kandoe.backend.utils.jobs.MailJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.TransportStrategy;
import org.codemonkey.simplejavamail.email.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

@Service
public class AsynchronousSessionSchedulerServiceImpl implements AsynchronousSessionSchedulerService {
    private static final double NOTIFICATION_TRIGGER_TIME_PERCENTAGE = 0.75;

    private final MailProperties mailProperties;
    private final JobScheduler jobScheduler;

    private final Logger logger;
    private Mailer mailer;

    private SessionService sessionService;
    private SessionGameService sessionGameService;

    private Set<ScheduledFutureEntryParticipantNotification> participantNotificationScheduledFutureEntries;
    private Set<ScheduledFutureEntryParticipantAssignment> participantAssignmentScheduledFutureEntries;

    @Value("${app.baseUrl}")
    private String baseUrl;

    @Autowired
    public AsynchronousSessionSchedulerServiceImpl(MailProperties mailProperties, JobScheduler jobScheduler, SessionService sessionService) {
        this.mailProperties = mailProperties;
        this.jobScheduler = jobScheduler;
        this.sessionService = sessionService;

        this.participantNotificationScheduledFutureEntries = new HashSet<>();
        this.participantAssignmentScheduledFutureEntries = new HashSet<>();
        this.logger = LogManager.getLogger(this.getClass());

        this.initializeMailer();
    }

    private void initializeMailer() {
        this.mailer = new Mailer(
                mailProperties.getHost(), mailProperties.getPort(),
                mailProperties.getUsername(), mailProperties.getPassword(),
                TransportStrategy.SMTP_SSL
        );
    }

    @Autowired
    public void setSessionGameService(SessionGameService sessionGameService) {
        this.sessionGameService = sessionGameService;
    }

    @Override
    public void scheduleParticipantNotificationTurnAboutToExpire(AsynchronousSession session, User user) {
        String userEmail = user.getEmail();

        if (userEmail == null || userEmail.isEmpty()) {
            logger.error(String.format("Attempted to schedule a notification for user '%s', but no email address was available (this shouldn't happen)", user.getUsername()));
            return;
        }

        Email email = new Email();

        email.setFromAddress("CanDo Team E", mailProperties.getUsername());
        email.setSubject("CanDo: Session reminder");
        email.addRecipient("", userEmail, Message.RecipientType.TO);
        email.setTextHTML(
                "<body style=\"font-family: Arial;\">" +
                        "<p>Hello " + user.getUsername() + "</p>" +
                        "<p>Your turn for your current session of the organization '" + session.getCategory().getOrganization().getName() + "' is about to expire." +
                        "Please move a card on the circle or your turn will be skipped.</p>" +
                        "<p>CanDo Team E</p>" +
                        "</body>");

        int secondsToAddToCurrentTime = (int) (session.getSecondsBetweenMoves() * NOTIFICATION_TRIGGER_TIME_PERCENTAGE);

        ScheduledFuture scheduledFuture = jobScheduler.scheduleJob(
                new MailJob(mailer, email),
                Date.from(new Date().toInstant().plusSeconds(secondsToAddToCurrentTime))
        );

        ScheduledFutureEntryParticipantNotification scheduledFutureEntryParticipantNotification =
                new ScheduledFutureEntryParticipantNotification(user.getUserId(), session.getSessionId(), scheduledFuture);

        this.participantNotificationScheduledFutureEntries.add(scheduledFutureEntryParticipantNotification);
    }

    @Override
    public void cancelParticipantNotification(AsynchronousSession session, User user) {
        Optional<ScheduledFutureEntryParticipantNotification> optional =
                this.participantNotificationScheduledFutureEntries
                        .stream()
                        .filter(e -> e.getUserId() == user.getUserId())
                        .findFirst();

        if (!optional.isPresent()) {
            logger.warn("Attempted to cancel a scheduled notification for user '" + user.getUsername() + "', but there was no scheduled action for this user.");
            return;
        }

        ScheduledFutureEntryParticipantNotification entry = optional.get();

        entry.getScheduledFuture().cancel(false);
        this.participantNotificationScheduledFutureEntries.remove(entry);
    }

    @Override
    public void scheduleNextParticipantAssignment(AsynchronousSession session) {
        Date dateToPerformActionAt = Date.from(new Date().toInstant().plusSeconds(session.getSecondsBetweenMoves()));
        ScheduledFuture scheduledFuture = jobScheduler.scheduleJob(new Runnable() {
            @Override
            public void run() {
                ParticipantInfo nextParticipant = sessionGameService.getNextParticipant(session);
                session.setCurrentParticipantPlaying(nextParticipant);
                sessionService.updateSession(session);
            }
        }, dateToPerformActionAt);

        ScheduledFutureEntryParticipantAssignment entry =
                new ScheduledFutureEntryParticipantAssignment(session.getSessionId(), scheduledFuture);

        this.participantAssignmentScheduledFutureEntries.add(entry);
    }

    @Override
    public void cancelNextParticipantAssignment(AsynchronousSession session) {
        Optional<ScheduledFutureEntryParticipantAssignment> optional =
                this.participantAssignmentScheduledFutureEntries
                        .stream()
                        .filter(e -> e.getSessionId() == session.getSessionId())
                        .findFirst();

        if (!optional.isPresent()) {
            logger.error("Attempted to cancel a scheduled participant assignment action for session with ID '" + session.getSessionId() + "', but there was no scheduled action for this session.");
            return;
        }

        ScheduledFutureEntryParticipantAssignment entry = optional.get();

        entry.getScheduledFuture().cancel(false);
        this.participantAssignmentScheduledFutureEntries.remove(entry);
    }

    private class ScheduledFutureEntryParticipantNotification {
        private final int userId;
        private final int sessionId;
        private final ScheduledFuture<?> scheduledFuture;

        public ScheduledFutureEntryParticipantNotification(int userId, int sessionId, ScheduledFuture<?> scheduledFuture) {
            this.userId = userId;
            this.sessionId = sessionId;
            this.scheduledFuture = scheduledFuture;
        }

        public int getUserId() {
            return userId;
        }

        public int getSessionId() {
            return sessionId;
        }

        public ScheduledFuture<?> getScheduledFuture() {
            return scheduledFuture;
        }
    }

    private class ScheduledFutureEntryParticipantAssignment {
        private final int sessionId;
        private final ScheduledFuture<?> scheduledFuture;

        public ScheduledFutureEntryParticipantAssignment(int sessionId, ScheduledFuture<?> scheduledFuture) {
            this.sessionId = sessionId;
            this.scheduledFuture = scheduledFuture;
        }

        public int getSessionId() {
            return sessionId;
        }

        public ScheduledFuture<?> getScheduledFuture() {
            return scheduledFuture;
        }
    }
}