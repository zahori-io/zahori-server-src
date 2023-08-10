package io.zahori.server.security;

/*-
 * #%L
 * zahori-server
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2021 PANEL SISTEMAS INFORMATICOS,S.L
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
import io.zahori.server.email.Email;
import io.zahori.server.email.EmailDto;
import io.zahori.server.email.EmailService;
import io.zahori.server.email.EmailVerification;
import io.zahori.server.email.EmailVerificationRepository;
import io.zahori.server.exception.BadRequestException;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountRepository accountRepository;
    private final EmailVerificationRepository emailVerificationRepository;

    @Autowired
    public AccountService(EmailService emailService, BCryptPasswordEncoder bCryptPasswordEncoder, AccountRepository accountRepository, EmailVerificationRepository emailVerificationRepository) {
        this.emailService = emailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.accountRepository = accountRepository;
        this.emailVerificationRepository = emailVerificationRepository;
    }

    private AccountEntity getUser() {
        try {
            UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            AccountEntity user = accountRepository.findByUsername(authentication.getPrincipal().toString());
            return user;
        } catch (Exception e) {
            throw new BadRequestException("Invalid session");
        }
    }

    public void createAccount(AccountEntity user) {
        // TODO validate user does not exist yet (validate username and email)
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        accountRepository.save(user);
    }

    public void changePassword(Passwords passwords) {
        if (StringUtils.isBlank(passwords.getCurrentPassword())) {
            throw new BadRequestException("Current password can't be empty");
        }

        if (StringUtils.isBlank(passwords.getNewPassword())) {
            throw new BadRequestException("New password can't be empty");
        }

        if (!passwords.getNewPassword().equals(passwords.getConfirmPassword())) {
            throw new BadRequestException("Confirm password doesn't match New password");
        }

        AccountEntity user = getUser();

        if (!bCryptPasswordEncoder.matches(passwords.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(bCryptPasswordEncoder.encode(passwords.getNewPassword()));
        accountRepository.save(user);
    }

    public EmailDto getEmails() {
        EmailDto emailDto = new EmailDto();

        // Get user and current email
        AccountEntity user = getUser();
        emailDto.setEmail(user.getEmail());

        // Get new email request (if exists)
        Optional<EmailVerification> emailVerificationOptional = emailVerificationRepository.findByAccountId(user.getId());
        if (!emailVerificationOptional.isPresent()) {
            return emailDto;
        }

        EmailVerification emailVerification = emailVerificationOptional.get();

        // Review if token has expired
        if (System.currentTimeMillis() > emailVerification.getTokenExpiration()) {
            emailVerificationRepository.delete(emailVerification);
            emailDto.setNewEmail("");
        } else {
            emailDto.setNewEmail(emailVerification.getNewEmail());
        }

        return emailDto;
    }

    // TODO: metodo que haga: trim, toLower, regExp y buscar si ya existe en BD
    public boolean isValidEmail(String email) {
        final String regExp = "^[a-z0-9]+([\\._-]?[a-z0-9]+)*@[a-z0-9]+([\\.-]?[a-z0-9]+)*(\\.[a-z0-9]{2,})+$";

        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(StringUtils.trim(email));
        return matcher.matches();
    }

    @Transactional
    public void createChangeEmailRequest(String newEmail, String host) {
        if (StringUtils.isBlank(newEmail)) {
            throw new BadRequestException("Invalid email");
        }

        newEmail = newEmail.trim().toLowerCase();

        if (!isValidEmail(newEmail)) {
            throw new BadRequestException("Invalid email");
        }

        AccountEntity user = getUser();

        // Verify if already exist an email change request, if so, remove it and generate a new one
        Optional<EmailVerification> emailVerificationOptional = emailVerificationRepository.findByAccountId(user.getId());
        if (emailVerificationOptional.isPresent()) {
            emailVerificationRepository.delete(emailVerificationOptional.get());
        }

        // Create new email change request in DB
        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setNewEmail(newEmail);
        emailVerification.setAccountId(user.getId());
        UUID uuid = UUID.randomUUID();
        emailVerification.setToken(uuid);
        emailVerification.setTokenExpiration(System.currentTimeMillis() + 1800000L);
        emailVerificationRepository.save(emailVerification);

        // Send verification email
        Email emailDetails = new Email();
        emailDetails.setRecipient(newEmail);
        emailDetails.setSubject("Zahori - Email verification");
        String verifyLink = host + "/#/account/verify-email/" + uuid;
        emailDetails.setMsgText(getVerifyEmailText(verifyLink));
        emailDetails.setMsgHtml(getVerifyEmailHtml(verifyLink));
        emailService.send(emailDetails);
    }

    @Transactional
    public void verifyEmail(UUID token) {
        Optional<EmailVerification> emailVerificationOptional = emailVerificationRepository.findByToken(token);
        if (emailVerificationOptional.isEmpty()) {
            throw new BadRequestException("Token has expired");
        }

        EmailVerification emailVerification = emailVerificationOptional.get();

        // Validate token expiration
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp > emailVerification.getTokenExpiration()) {

            // Remove change email request from DB
            emailVerificationRepository.delete(emailVerification);

            throw new BadRequestException("Token has expired");
        }

        Optional<AccountEntity> userOptional = accountRepository.findById(emailVerification.getAccountId());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User does not exist");
        }

        // Update user email
        AccountEntity user = userOptional.get();
        user.setEmail(emailVerification.getNewEmail());
        accountRepository.save(user);

        // Remove change email request from DB
        emailVerificationRepository.delete(emailVerification);
    }

    private String getVerifyEmailText(String verifyLink) {
        return "¡Hola!\n"
                + "\n"
                + "Hemos recibido una solicitud para actualizar tu dirección de correo electrónico en Zahori. Para completar el cambio, por favor haz clic en el siguiente enlace de verificación:\n"
                + verifyLink + "\n"
                + "Si no solicitaste este cambio, puedes ignorar este correo electrónico y tu dirección de correo actual permanecerá sin cambios.\n"
                + "\n"
                + "¡Gracias por ser parte de nuestra comunidad!\n"
                + "El equipo Zahorí.";
    }

    private String getVerifyEmailHtml(String verifyLink) {
        return "<p>&iexcl;Hola!</p>" //
                + "<p>Hemos recibido una solicitud para actualizar tu direcci&oacute;n de correo electr&oacute;nico en Zahori. Para completar el cambio, por favor haz clic en el siguiente enlace de verificaci&oacute;n:</p>" //
                + "<p><a href=\"" + verifyLink + "\" target=\"_blank\">Verificar Email</a></p>" //
                + "<p>Si no solicitaste este cambio, puedes ignorar este correo electr&oacute;nico y tu direcci&oacute;n de correo actual permanecer&aacute; sin cambios.</p>" //
                + "<p>&iexcl;Gracias por ser parte de nuestra comunidad!</p>" //
                + "<p>El equipo Zahor&iacute;.</p>" //
                + "<p>&nbsp;</p>";
    }
}
