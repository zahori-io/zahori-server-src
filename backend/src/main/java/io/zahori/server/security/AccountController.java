package io.zahori.server.security;

/*-
 * #%L
 * zahori-server
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2021 - 2022 PANEL SISTEMAS INFORMATICOS,S.L
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
import io.zahori.server.email.EmailDto;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Disabled for security until user management is implemented
    // @PostMapping("/account/sign-up")
    public ResponseEntity<Object> signUp(@RequestBody AccountDto user) {
        LOG.info("Create account: " + user);
        accountService.createAccount(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/account/verify-email/{token}")
    public ResponseEntity<Object> verifyEmail(@PathVariable UUID token) {
        accountService.verifyEmail(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/api/account/password")
    public ResponseEntity<Object> changePassword(@RequestBody Passwords passwords) {
        accountService.changePassword(passwords);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/account/email")
    public ResponseEntity<Object> getEmails() {
        EmailDto emailDto = accountService.getEmails();
        return new ResponseEntity<>(emailDto, HttpStatus.OK);
    }

    @PostMapping("/api/account/email")
    public ResponseEntity<Object> changeEmail(@RequestBody String newEmail, HttpServletRequest request) {
        String host = StringUtils.substringBefore(request.getRequestURL().toString(), request.getContextPath());
        host = host + request.getContextPath();

        accountService.createChangeEmailRequest(newEmail, host);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
