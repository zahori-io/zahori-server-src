package io.zahori.server.email;

/*-
 * #%L
 * zahori-server
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2021 - 2023 PANEL SISTEMAS INFORMATICOS,S.L
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
import io.zahori.server.exception.ServiceUnavailableException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MessageSource messageSource;

    @Value("${zahori.email.enable}")
    private boolean enabled;

    @Value("${spring.mail.username}")
    private String sender;

    public void isEnabled() {
        if (!enabled) {
            throw new ServiceUnavailableException(messageSource.getMessage("i18n.email-service.ko", null, LocaleContextHolder.getLocale()));
        }
    }

    public void send(Email email) {
        sendEmail(email);
    }

    @Async
    public void sendAsync(Email email) {
        sendEmail(email);
    }

    private void sendEmail(Email email) {
        isEnabled();

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            boolean multipartMode = true;
            MimeMessageHelper helper = new MimeMessageHelper(message, multipartMode);

            helper.setFrom(sender);
            helper.setTo(email.getRecipient());
            helper.setSubject(email.getSubject());

            // HTML and plain text message
            if (email.hasMsgText() && email.hasMsgHtml()) {
                helper.setText(email.getMsgText(), email.getMsgHtml());
            }

            // Plain text message
            if (email.hasMsgText() && !email.hasMsgHtml()) {
                helper.setText(email.getMsgText());
            }

            // HTML message
            if (!email.hasMsgText() && email.hasMsgHtml()) {
                helper.setText(email.getMsgHtml(), true);
            }

            javaMailSender.send(message);
            LOG.info("Email sent successfully");

        } catch (Exception e) {
            throw new RuntimeException(String.format("Error sending email: %s", e.getMessage()));
        }
    }
}
