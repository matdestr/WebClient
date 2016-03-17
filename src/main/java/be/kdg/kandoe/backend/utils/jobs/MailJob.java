package be.kdg.kandoe.backend.utils.jobs;


import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.email.Email;

/**
 * Class that extends from {@link Runnable} and is used for sending scheduled mails in {@link be.kdg.kandoe.backend.utils.JobScheduler}
 */

public class MailJob implements Runnable {
    private final Mailer mailer;
    private final Email email;

    public MailJob(Mailer mailer, Email email) {
        this.mailer = mailer;
        this.email = email;

    }

    @Override
    public void run() {
        mailer.sendMail(email);
    }
}
