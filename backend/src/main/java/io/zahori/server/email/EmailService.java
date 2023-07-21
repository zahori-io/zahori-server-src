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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${zahori.email.enable}")
    private boolean enabled;

    @Value("${spring.mail.username}")
    private String sender;

    public boolean isEnabled() {
        return enabled;
    }

    public void send(EmailDetails details) {
        try {
            sendEmail(details);
        } catch (Exception e) {
            LOG.error("Error sending email: {}", e.getMessage());
            throw new RuntimeException("Error sending email");
        }
    }

    @Async
    public void sendAsync(EmailDetails details) {
        try {
            sendEmail(details);
        } catch (Exception e) {
            LOG.error("Error sending email: {}", e.getMessage());
        }
    }

    private void sendEmail(EmailDetails details) {
        if (!isEnabled()) {
            return;
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(sender);
        mailMessage.setTo(details.getRecipient());
        mailMessage.setText(details.getMsgBody());
        mailMessage.setSubject(details.getSubject());

        javaMailSender.send(mailMessage);
        LOG.info("Email sent successfully");
    }
}
