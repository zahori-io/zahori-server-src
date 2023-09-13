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
import io.zahori.server.utils.Validator;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class AccountService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountRepository accountRepository;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;

    @Autowired
    public AccountService(EmailService emailService, BCryptPasswordEncoder bCryptPasswordEncoder, AccountRepository accountRepository, ForgotPasswordRepository forgotPasswordRepository, EmailVerificationRepository emailVerificationRepository, TemplateEngine templateEngine, MessageSource messageSource) {
        this.emailService = emailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.accountRepository = accountRepository;
        this.forgotPasswordRepository = forgotPasswordRepository;
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

    // @Async
    public void createAccount(AccountSignupDto accountDto, Locale locale) {
        validateEmail(accountDto.getEmail(), locale);
        accountDto.setEmail(accountDto.getEmail().trim().toLowerCase());

        if (isEmailRegistered(accountDto.getEmail())) {
            LOG.info("Email already registered: {}", accountDto.getEmail());
            return; // No error to avoid user enumeration
        }

        // TODO send confirmation email before creating the account in the database
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

    public void changePassword(Passwords passwords, Locale locale) {
        if (StringUtils.isBlank(passwords.getCurrentPassword())) {

            throw new BadRequestException(messageSource.getMessage("i18n.change-password.currentPassword.empty", null, locale));
        }

        if (!Validator.isValidPassword(passwords.getNewPassword())) {
            throw new BadRequestException(messageSource.getMessage(Validator.INVALID_PASSWORD, null, locale));
        }

        if (!passwords.getNewPassword().equals(passwords.getConfirmPassword())) {
            throw new BadRequestException(messageSource.getMessage("i18n.change-password.confirmPassword.match", null, locale));
        }

        Account user = getAccount();

        if (!bCryptPasswordEncoder.matches(passwords.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException(messageSource.getMessage("i18n.change-password.currentPassword.incorrect", null, locale));
        }

        user.setPassword(bCryptPasswordEncoder.encode(passwords.getNewPassword()));
        accountRepository.save(user);
    }

    @Async
    @Transactional
    public void createForgotPasswordRequest(String email, String host, Locale locale) { // Locale from controller due to @Async creates new thread
        validateEmail(email, locale);
        email = email.trim().toLowerCase();

        Account user = accountRepository.findByEmail(email);
        if (user == null) {
            LOG.info("Email not present: {}", email);
            return;
        }

        // Verify if user already has a forgot password request, if so, remove it and generate a new one
        Optional<ForgotPassword> forgotPasswordOptional = forgotPasswordRepository.findByAccountId(user.getId());
        LOG.info("forgot password request present: {}", forgotPasswordOptional.isPresent());
        if (forgotPasswordOptional.isPresent()) {
            forgotPasswordRepository.delete(forgotPasswordOptional.get());
        }

        // Create new forgot password request in DB
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setAccountId(user.getId());
        UUID uuid = UUID.randomUUID();
        forgotPassword.setToken(uuid);
        forgotPassword.setTokenExpiration(System.currentTimeMillis() + 600000L); // 600000ms = 10 minutes
        forgotPasswordRepository.save(forgotPassword);

        // Send email with link
        Context context = new Context(locale);

        String resetPasswordLink = host + "/#/account/forgot-password/" + uuid;
        context.setVariable("resetPasswordLink", resetPasswordLink);
        context.setVariable("locale", locale.getLanguage());

        String emailContentTxt = templateEngine.process("forgot-password.txt", context);
        String emailContentHtml = templateEngine.process("forgot-password.html", context);

        Email emailDetails = new Email();
        emailDetails.setSubject(messageSource.getMessage("i18n.forgot-password.email.subject", null, locale));
        emailDetails.setRecipient(email);
        emailDetails.setMsgText(emailContentTxt);
        emailDetails.setMsgHtml(emailContentHtml);
        emailService.send(emailDetails);
    }

    public void forgotPasswordReset(ForgotPasswordDto forgotPasswordDto, Locale locale) {
        String tokenExpiredMessage = messageSource.getMessage("i18n.forgot-password.reset.tokenExpired", null, locale);

        if (forgotPasswordDto.getToken() == null) {
            throw new BadRequestException(tokenExpiredMessage);
        }

        if (!Validator.isValidPassword(forgotPasswordDto.getNewPassword())) {
            throw new BadRequestException(messageSource.getMessage(Validator.INVALID_PASSWORD, null, locale));
        }

        if (!forgotPasswordDto.getNewPassword().equals(forgotPasswordDto.getConfirmPassword())) {
            throw new BadRequestException(messageSource.getMessage("i18n.forgot-password.passwordsNotMatch", null, locale));
        }

        Optional<ForgotPassword> forgotPasswordOptional = forgotPasswordRepository.findByToken(forgotPasswordDto.getToken());
        if (forgotPasswordOptional.isEmpty()) {
            throw new BadRequestException(tokenExpiredMessage);
        }
        ForgotPassword forgotPassword = forgotPasswordOptional.get();

        // Validate token expiration
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp > forgotPassword.getTokenExpiration()) {

            // Remove forgot password request from DB
            forgotPasswordRepository.delete(forgotPassword);

            throw new BadRequestException(tokenExpiredMessage);
        }

        Optional<Account> userOptional = accountRepository.findById(forgotPassword.getAccountId());
        if (userOptional.isEmpty()) {
            throw new BadRequestException(tokenExpiredMessage);
        }
        Account user = userOptional.get();

        user.setPassword(bCryptPasswordEncoder.encode(forgotPasswordDto.getNewPassword()));
        accountRepository.save(user);

        forgotPasswordRepository.delete(forgotPassword);
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

    public void validateEmail(String email, Locale locale) {
        if (!Validator.isValidEmail(email)) {
            throw new BadRequestException(messageSource.getMessage(Validator.INVALID_EMAIL, null, locale));
        }
    }

    private boolean isEmailRegistered(String email) {
        Account account = accountRepository.findByEmail(email);
        return account != null;
    }

    @Transactional
    public void createChangeEmailRequest(String newEmail, String host, Locale locale) {
        validateEmail(newEmail, locale);
        newEmail = newEmail.trim().toLowerCase();

        if (isEmailRegistered(newEmail)) {
            LOG.info("Email already registered: {}", newEmail);
            return; // No error to avoid user enumeration
        }

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
        emailVerification.setTokenExpiration(System.currentTimeMillis() + 1800000L); // 1800000ms = 30 minutes
        emailVerificationRepository.save(emailVerification);

        // Send verification email
        Context context = new Context(locale);

        String verifyLink = host + "/#/account/verify-email/" + uuid;
        context.setVariable("verifyLink", verifyLink);
        context.setVariable("locale", locale.getLanguage());

        String emailContentTxt = templateEngine.process("verify-email.txt", context);
        String emailContentHtml = templateEngine.process("verify-email.html", context);

        Email emailDetails = new Email();
        emailDetails.setSubject(messageSource.getMessage("i18n.verify-email.email.subject", null, locale));
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
            LOG.info("Account does not exist: {}", emailVerification.getAccountId());
            throw new BadRequestException(tokenExpired);
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
