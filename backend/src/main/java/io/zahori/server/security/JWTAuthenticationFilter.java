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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * The type Jwt authentication filter.
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private AuthenticationManager authenticationManager;

    /**
     * Instantiates a new Jwt authentication filter.
     *
     * @param authenticationManager the authentication manager
     */
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        LOG.debug("JWTAuthenticationFilter.attemptAuthentication");

        try {
            AccountEntity creds = new ObjectMapper().readValue(req.getInputStream(), AccountEntity.class);

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername().toLowerCase(), creds.getPassword(), new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth)
            throws IOException, ServletException {
        LOG.info("JWTAuthenticationFilter.successfulAuthentication");

        CustomUser user = (CustomUser) auth.getPrincipal();
        String authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        String token = Jwts.builder() //
                .setSubject(user.getUsername()) //
                .claim(SecurityConstants.AUTHORITIES_KEY, authorities) //
                .claim(SecurityConstants.CLIENT_ID, user.getClientId()) //
                .setIssuedAt(new Date(System.currentTimeMillis())) //
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME)) //
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET.getBytes()).compact();

        res.getOutputStream().print("{\"token\":\"" + SecurityConstants.TOKEN_PREFIX + token + "\"}");
        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
    }
}
