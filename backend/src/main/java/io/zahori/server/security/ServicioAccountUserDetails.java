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
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * The type Servicio account user details.
 */
@Service
public class ServicioAccountUserDetails implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(ServicioAccountUserDetails.class);

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOG.debug("ServicioAccountUserDetails.loadUserByUsername");

        if (StringUtils.isBlank(username)) {
            return null;
        }

        AccountEntity account = accountRepository.findByUsernameOrEmail(username);
        if (account == null) {
            // Cuenta no encontrada
            return null;
        }
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (RoleEntity role : account.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getCode()));
        }

        return new CustomUser(account.getUsername(), account.getPassword(), account.isEnabled(), !account.isExpired(), !account.isCredentialsexpired(),
                !account.isLocked(), grantedAuthorities, account.getClient().getClientId());
    }
}
