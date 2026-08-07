package com.terangalink.backend.service;

import com.terangalink.backend.exception.business.EmailDeliveryException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final String VERIFICATION_SUBJECT =
            "Bienvenue sur TerangaLink - Verifiez votre adresse email";

    private final JavaMailSender javaMailSender;
    private final String mailFrom;

    public EmailService(JavaMailSender javaMailSender, @Value("${app.mail.from}") String mailFrom) {
        this.javaMailSender = javaMailSender;
        this.mailFrom = mailFrom;
    }

    // Envoie l'email HTML de verification avec le code et le prenom de l'utilisateur.
    public void sendVerificationEmail(String email, String firstName, String code) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setFrom(mailFrom);
            helper.setTo(email);
            helper.setSubject(VERIFICATION_SUBJECT);
            helper.setText(buildVerificationEmailHtml(firstName, code), true);
            
            javaMailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            throw new EmailDeliveryException("Impossible d'envoyer l'email de verification.", exception);
        }
    }

    private String buildVerificationEmailHtml(String firstName, String code) {
        return """
                <!DOCTYPE html>
                <html lang="fr">
                <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <title>Verification de votre adresse email</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f4f7f8;font-family:Arial,Helvetica,sans-serif;">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0" style="background-color:#f4f7f8;padding:24px 12px;">
                        <tr>
                            <td align="center">
                                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0" style="max-width:600px;background-color:#ffffff;border-radius:16px;overflow:hidden;">
                                    <tr>
                                        <td style="background-color:#00343a;padding:32px 32px 24px 32px;text-align:center;">
                                            <h1 style="margin:0;font-size:28px;line-height:36px;color:#ffffff;font-weight:700;">TerangaLink</h1>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:32px;">
                                            <p style="margin:0 0 16px 0;font-size:16px;line-height:24px;color:#181c1d;">Bonjour %s,</p>
                                            <p style="margin:0 0 24px 0;font-size:16px;line-height:24px;color:#181c1d;">
                                                Bienvenue sur TerangaLink.
                                                <br />
                                                Votre code de verification est :
                                            </p>
                                            <div style="margin:0 0 24px 0;padding:20px;border:1px solid #d6dde0;border-radius:12px;background-color:#f7fafb;text-align:center;">
                                                <span style="display:inline-block;font-size:32px;line-height:40px;letter-spacing:0.2em;font-weight:700;color:#00343a;">
                                                    %s
                                                </span>
                                            </div>
                                            <p style="margin:0 0 16px 0;font-size:14px;line-height:22px;color:#40484a;">
                                                Ce code est valable 10 minutes.
                                            </p>
                                            <p style="margin:0;font-size:14px;line-height:22px;color:#40484a;">
                                                Si vous n'etes pas a l'origine de cette inscription, ignorez simplement cet email.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(firstName, code);
    }
}
