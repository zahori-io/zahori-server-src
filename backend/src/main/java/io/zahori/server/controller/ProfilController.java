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
import io.zahori.server.email.EmailDto;
import io.zahori.server.security.Passwords;
import io.zahori.server.service.ProfileService;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfilController {

    private final ProfileService profileService;

    private static final Logger LOG = LoggerFactory.getLogger(ProfilController.class);

    public ProfilController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/password")
    public ResponseEntity<Object> changePassword(@RequestBody Passwords passwords) {
        profileService.changePassword(passwords);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/email")
    public ResponseEntity<Object> getEmails() {
        EmailDto emailDto = profileService.getEmails();
        return new ResponseEntity<>(emailDto, HttpStatus.OK);
    }

    @PostMapping("/email")
    public ResponseEntity<Object> changeEmail(@RequestBody String newEmail, HttpServletRequest request) {
        String host = StringUtils.substringBefore(request.getRequestURL().toString(), request.getContextPath());
        host = host + request.getContextPath();

        profileService.createChangeEmailRequest(newEmail, host);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
