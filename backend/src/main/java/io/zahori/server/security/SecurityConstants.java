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

/**
 * The type Security constants.
 */
public class SecurityConstants {
    /**
     * The constant SECRET.
     */
    public static final String SECRET = "SecretKeyToGenJWTs";
    /**
     * The constant EXPIRATION_TIME.
     */
    public static final long EXPIRATION_TIME = 86_400_000; // 1 day
    /**
     * The constant TOKEN_PREFIX.
     */
    public static final String TOKEN_PREFIX = "Bearer ";
    /**
     * The constant HEADER_STRING.
     */
    public static final String HEADER_STRING = "Authorization";
    /**
     * The constant SIGN_UP_URL.
     */
    public static final String SIGN_UP_URL = "/users/sign-up";
    /**
     * The constant PROCESS_REGISTRATION_URL.
     */
    public static final String PROCESS_REGISTRATION_URL = "/process";
    /**
     * The constant AUTHORITIES_KEY.
     */
    public static final String AUTHORITIES_KEY = "CLAIM_TOKEN";
    /**
     * The constant CLIENT_ID.
     */
    public static final String CLIENT_ID = "clientId";

}
