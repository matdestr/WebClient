package be.kdg.kandoe.backend.utils.jobs;


import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.email.Email;

public class MailJob implements Runnable {
    private final Mailer mailer;
    private final Email email;

    public MailJob(Mailer mailer, Email email) {
        this.mailer = mailer;
        this.email = email;

    }

    @Override
    public void run() {
        System.out.println("Sending email...");
        mailer.sendMail(email);
    }
}
