package io.zahori.server.controller;

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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zahori.server.security.AccountEntity;
import io.zahori.server.security.AccountRepository;
import io.zahori.server.security.Passwords;

@RestController
@RequestMapping("/api/password")
public class PasswordController {
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private AccountRepository accountRepository;

    private static final Logger LOG = LoggerFactory.getLogger(PasswordController.class);

    public PasswordController(BCryptPasswordEncoder bCryptPasswordEncoder, AccountRepository accountRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.accountRepository = accountRepository;
    }

    @PostMapping
    public ResponseEntity<String> changePassword(@RequestBody Passwords passwords) {
        if (StringUtils.isBlank(passwords.getCurrentPassword())) {
            return new ResponseEntity<>("Current password can't be empty", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isBlank(passwords.getNewPassword())) {
            return new ResponseEntity<>("New password can't be empty", HttpStatus.BAD_REQUEST);
        }

        if (!passwords.getNewPassword().equals(passwords.getConfirmPassword())) {
            return new ResponseEntity<>("Confirm password doesn't match New password", HttpStatus.BAD_REQUEST);
        }

        try {
            UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            AccountEntity user = accountRepository.findByUsername(authentication.getPrincipal().toString());

            if (bCryptPasswordEncoder.matches(passwords.getCurrentPassword(), user.getPassword())) {
                user.setPassword(bCryptPasswordEncoder.encode(passwords.getNewPassword()));
                accountRepository.save(user);
                return new ResponseEntity<>("Password updated!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid password", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
