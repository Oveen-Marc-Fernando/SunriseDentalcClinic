package mail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 *
 * @author oveen
 */
public final class MailSender {

    private static final String CONFIG_RESOURCE = "/mail/mail.properties";
    private static final Properties CONFIG = loadConfig();

    private MailSender() {
    }

    private static Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream in = MailSender.class.getResourceAsStream(CONFIG_RESOURCE)) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            System.err.println("[MailSender] Failed to load mail.properties: " + e.getMessage());
        }
        return props;
    }

    public static boolean isConfigured() {
        String from = CONFIG.getProperty("mail.from", "");
        String password = CONFIG.getProperty("mail.password", "");
        return from != null && !from.trim().isEmpty() && password != null && !password.trim().isEmpty();
    }

    public static void sendBillEmail(String toEmail, String patientName, String billingId, byte[] pdfBytes)
            throws MessagingException {
        String name = patientName != null && !patientName.isEmpty() ? patientName : "Patient";
        sendPdfEmail(toEmail,
                "Your Bill from Sunrise Dental Clinic — " + billingId,
                "Dear " + name + ",\n\n"
                        + "Thank you for visiting Sunrise Dental Clinic. Your bill (" + billingId + ") is attached "
                        + "as a PDF.\n\n"
                        + "If you have any questions about this bill, please contact us.\n\n"
                        + "Best regards,\nSunrise Dental Clinic",
                "Bill_" + billingId + ".pdf",
                pdfBytes);
    }

    public static void sendAppointmentEmail(String toEmail, String patientName, String appointmentId, byte[] pdfBytes)
            throws MessagingException {
        String name = patientName != null && !patientName.isEmpty() ? patientName : "Patient";
        sendPdfEmail(toEmail,
                "Your Appointment Receipt from Sunrise Dental Clinic — " + appointmentId,
                "Dear " + name + ",\n\n"
                        + "Thank you for booking with Sunrise Dental Clinic. Your appointment receipt ("
                        + appointmentId + ") is attached as a PDF.\n\n"
                        + "If you have any questions about this appointment, please contact us.\n\n"
                        + "Best regards,\nSunrise Dental Clinic",
                "Appointment_" + appointmentId + ".pdf",
                pdfBytes);
    }

    // =========================================================================
    // Registration / approval — plain-text, no attachment (Register.java,
    // AD_APR_UserLogins). Patient accounts are auto-approved (no admin
    // review needed), so they only ever get the "you're in" email; Dentist
    // (and any other role that still self-registers PENDING) gets a
    // "submitted, awaiting approval" email now and a separate "approved,
    // you can log in" email later once an Administrator actually approves it.
    // =========================================================================

    /** Sent immediately after a Patient registers — their account is already usable. */
    public static void sendRegistrationSuccessEmail(String toEmail, String fullName) throws MessagingException {
        String name = displayName(fullName);
        sendPlainEmail(toEmail,
                "Welcome to Sunrise Dental Clinic!",
                "Dear " + name + ",\n\n"
                        + "Your account has been created successfully. You can log in right away and start "
                        + "booking appointments.\n\n"
                        + "Best regards,\nSunrise Dental Clinic");
    }

    /** Sent immediately after a Dentist (or any other role that still needs review) registers. */
    public static void sendRegistrationPendingEmail(String toEmail, String fullName, String roleLabel)
            throws MessagingException {
        String name = displayName(fullName);
        String role = roleLabel != null && !roleLabel.trim().isEmpty() ? roleLabel.trim() : "account";
        sendPlainEmail(toEmail,
                "Sunrise Dental Clinic — Registration Received",
                "Dear " + name + ",\n\n"
                        + "Thank you for registering as a " + role + " with Sunrise Dental Clinic. Your account "
                        + "is now pending Administrator approval — we'll let you know as soon as it's approved "
                        + "and you can log in.\n\n"
                        + "Best regards,\nSunrise Dental Clinic");
    }

    /** Sent when an Administrator approves a previously-pending account (AD_APR_UserLogins). */
    public static void sendAccountApprovedEmail(String toEmail, String fullName, String username) throws MessagingException {
        String name = displayName(fullName);
        sendPlainEmail(toEmail,
                "Sunrise Dental Clinic — Your Account Has Been Approved",
                "Dear " + name + ",\n\n"
                        + "Good news — your account (username: " + username + ") has been approved by our "
                        + "Administrator. You can now log in to your dashboard.\n\n"
                        + "Best regards,\nSunrise Dental Clinic");
    }

    /**
     * Sent when Office Staff creates a login on someone else's behalf
     * (Dentist Management / Patient Management — see
     * DentistManagementController / PatientManagementController) — the only
     * place the temporary password is ever revealed; it's never shown on
     * screen. The recipient is expected to set their own password the first
     * time they log in (Dentist_Dashboard / Patient_Dashboard forces the
     * Edit Profile popup open until they do). Deliberately role-neutral
     * wording — same email either way.
     */
    public static void sendTemporaryPasswordEmail(String toEmail, String fullName, String username, String tempPassword)
            throws MessagingException {
        String name = displayName(fullName);
        sendPlainEmail(toEmail,
                "Sunrise Dental Clinic — Your Account Is Ready",
                "Dear " + name + ",\n\n"
                        + "An account has been created for you at Sunrise Dental Clinic. Here are your temporary "
                        + "login details:\n\n"
                        + "Username: " + username + "\n"
                        + "Temporary Password: " + tempPassword + "\n\n"
                        + "Please log in and set your own password right away — you'll be asked to change it the "
                        + "first time you sign in.\n\n"
                        + "Best regards,\nSunrise Dental Clinic");
    }

    private static String displayName(String fullName) {
        return fullName != null && !fullName.trim().isEmpty() ? fullName.trim() : "there";
    }

    /** Plain-text email, no attachment — registration/approval notices. */
    private static void sendPlainEmail(String toEmail, String subject, String bodyText) throws MessagingException {
        String from = CONFIG.getProperty("mail.from", "").trim();
        String fromName = CONFIG.getProperty("mail.from.name", "Sunrise Dental Clinic");
        String password = CONFIG.getProperty("mail.password", "").trim();
        String host = CONFIG.getProperty("mail.smtp.host", "smtp.gmail.com").trim();
        String port = CONFIG.getProperty("mail.smtp.port", "587").trim();

        Properties smtpProps = new Properties();
        smtpProps.put("mail.smtp.host", host);
        smtpProps.put("mail.smtp.port", port);
        smtpProps.put("mail.smtp.auth", "true");
        smtpProps.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(smtpProps, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, fromName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(bodyText);
            Transport.send(message);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new MessagingException("Invalid sender name encoding", e);
        }
    }

    private static void sendPdfEmail(String toEmail, String subject, String bodyText, String attachmentFileName,
            byte[] pdfBytes) throws MessagingException {
        String from = CONFIG.getProperty("mail.from", "").trim();
        String fromName = CONFIG.getProperty("mail.from.name", "Sunrise Dental Clinic");
        String password = CONFIG.getProperty("mail.password", "").trim();
        String host = CONFIG.getProperty("mail.smtp.host", "smtp.gmail.com").trim();
        String port = CONFIG.getProperty("mail.smtp.port", "587").trim();

        Properties smtpProps = new Properties();
        smtpProps.put("mail.smtp.host", host);
        smtpProps.put("mail.smtp.port", port);
        smtpProps.put("mail.smtp.auth", "true");
        smtpProps.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(smtpProps, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, fromName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(bodyText);

            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource dataSource = new ByteArrayDataSource(pdfBytes, "application/pdf");
            attachmentPart.setDataHandler(new DataHandler(dataSource));
            attachmentPart.setFileName(attachmentFileName);

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);
            message.setContent(multipart);

            Transport.send(message);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new MessagingException("Invalid sender name encoding", e);
        }
    }
}
