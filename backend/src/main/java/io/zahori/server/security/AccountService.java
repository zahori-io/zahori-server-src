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
import io.zahori.server.i18n.Language;
import io.zahori.server.model.Client;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class AccountService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountRepository accountRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;

    @Autowired
    public AccountService(EmailService emailService, BCryptPasswordEncoder bCryptPasswordEncoder, AccountRepository accountRepository, EmailVerificationRepository emailVerificationRepository, TemplateEngine templateEngine, MessageSource messageSource) {
        this.emailService = emailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.accountRepository = accountRepository;
        this.emailVerificationRepository = emailVerificationRepository;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
    }

    public AccountView getAccountView() {
        try {
            UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            return accountRepository.findAccountViewByUsernameOrEmail(authentication.getPrincipal().toString());
        } catch (Exception e) {
            throw new BadRequestException("Invalid session");
        }
    }

    private Account getAccount() {
        try {
            UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            return accountRepository.findByUsername(authentication.getPrincipal().toString());
        } catch (Exception e) {
            throw new BadRequestException("Invalid session");
        }
    }

    public void createAccount(AccountSignupDto accountDto) {
        validateEmail(accountDto.getEmail());
        accountDto.setEmail(accountDto.getEmail().trim().toLowerCase());

        Account account = new Account();
        account.setEmail(accountDto.getEmail());
        // TODO receive and validate username?
        account.setUsername(accountDto.getEmail());
        account.setPassword(bCryptPasswordEncoder.encode(accountDto.getPassword()));

        // Client
        // TODO set clientId from user authenticated that created this user
        Client client = new Client();
        client.setClientId(1L);
        account.setClient(client);

        accountRepository.save(account);
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

        Account user = getAccount();

        if (!bCryptPasswordEncoder.matches(passwords.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(bCryptPasswordEncoder.encode(passwords.getNewPassword()));
        accountRepository.save(user);
    }

    public EmailDto getEmails() {
        EmailDto emailDto = new EmailDto();

        // Get user and current email
        Account user = getAccount();
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

    public void validateEmail(String email) {
        final String invalidEmail = "Invalid email";

        if (StringUtils.isBlank(email)) {
            throw new BadRequestException(invalidEmail);
        }

        email = email.trim().toLowerCase();

        if (isInvalidEmailFormat(email)) {
            throw new BadRequestException(invalidEmail);
        }

        if (isEmailAlreadyRegistered(email)) {
            LOG.info("Email already registered: {}", email);
            throw new BadRequestException(invalidEmail);
        }
    }

    private boolean isEmailAlreadyRegistered(String email) {
        Account account = accountRepository.findByEmail(email);
        return account != null;
    }

    private boolean isInvalidEmailFormat(String email) {
        final String regExp = "^[a-z0-9]+([\\._-]?[a-z0-9]+)*@[a-z0-9]+([\\.-]?[a-z0-9]+)*(\\.[a-z0-9]{2,})+$";

        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }

    @Transactional
    public void createChangeEmailRequest(String newEmail, String host, Model model) {
        validateEmail(newEmail);
        newEmail = newEmail.trim().toLowerCase();

        Account user = getAccount();

        // Verify if user already has an email change request, if so, remove it and generate a new one
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
        Locale locale = LocaleContextHolder.getLocale();
        Context context = new Context(locale);

        String verifyLink = host + "/#/account/verify-email/" + uuid;
        context.setVariable("verifyLink", verifyLink);
        context.setVariable("locale", locale.getLanguage());

        String emailContentTxt = templateEngine.process("verify-email.txt", context);
        String emailContentHtml = templateEngine.process("verify-email.html", context);

        Email emailDetails = new Email();
        emailDetails.setSubject(messageSource.getMessage("i18n.verify-email.subject", null, locale));
        emailDetails.setRecipient(newEmail);
        emailDetails.setMsgText(emailContentTxt);
        emailDetails.setMsgHtml(emailContentHtml);
        emailService.send(emailDetails);
    }

    @Transactional
    public void verifyEmail(UUID token) {
        final String tokenExpired = "Token expired";
        Optional<EmailVerification> emailVerificationOptional = emailVerificationRepository.findByToken(token);
        if (emailVerificationOptional.isEmpty()) {
            throw new BadRequestException(tokenExpired);
        }

        EmailVerification emailVerification = emailVerificationOptional.get();

        // Validate token expiration
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp > emailVerification.getTokenExpiration()) {

            // Remove change email request from DB
            emailVerificationRepository.delete(emailVerification);

            throw new BadRequestException(tokenExpired);
        }

        Optional<Account> userOptional = accountRepository.findById(emailVerification.getAccountId());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User does not exist");
        }

        // Update user email
        Account user = userOptional.get();
        user.setEmail(emailVerification.getNewEmail());
        accountRepository.save(user);

        // Remove change email request from DB
        emailVerificationRepository.delete(emailVerification);
    }

    public void changeLanguage(Language language) {
        Account user = getAccount();
        user.setLanguage(language);
        accountRepository.save(user);
    }

}
