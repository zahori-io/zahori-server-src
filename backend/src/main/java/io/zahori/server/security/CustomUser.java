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

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * The type Custom user.
 */
public class CustomUser extends User {

    private static final long serialVersionUID = -2687295108096788363L;

    private Long clientId;

    /**
     * Instantiates a new Custom user.
     *
     * @param username    the username
     * @param password    the password
     * @param authorities the authorities
     * @param clientId    the client id
     */
    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities, Long clientId) {
        super(username, password, authorities);
        this.clientId = clientId;
    }

    /**
     * Instantiates a new Custom user.
     *
     * @param username              the username
     * @param password              the password
     * @param enabled               the enabled
     * @param accountNonExpired     the account non expired
     * @param credentialsNonExpired the credentials non expired
     * @param accountNonLocked      the account non locked
     * @param authorities           the authorities
     * @param clientId              the client id
     */
    public CustomUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities, Long clientId) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.clientId = clientId;
    }

    /**
     * Gets client id.
     *
     * @return the client id
     */
    public Long getClientId() {
        return clientId;
    }

    /**
     * Sets client id.
     *
     * @param clientId the client id
     */
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

}
