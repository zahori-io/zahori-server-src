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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * The type Web security config.
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

    private UserDetailsService userDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Instantiates a new Web security config.
     *
     * @param userDetailsService    the user details service
     * @param bCryptPasswordEncoder the b crypt password encoder
     */
    public WebSecurityConfig(UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        LOG.debug("WebSecurityConfig.configure(WebSecurity web)");

        web.ignoring().antMatchers("/**/*.js");
        web.ignoring().antMatchers("/**/*.json");
        web.ignoring().antMatchers("/**/*.css");
        web.ignoring().antMatchers("/**/*.svg");
        web.ignoring().antMatchers("/**/*.ttf");
        web.ignoring().antMatchers("/**/*.png");
        web.ignoring().antMatchers("/**/*.gif");
        web.ignoring().antMatchers("/**/*.ico");
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        LOG.debug("WebSecurityConfig.configure(HttpSecurity http)");

        httpSecurity
                // we don't need CSRF because we store token in header
                .csrf().disable()

                // don't create session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() //

                .authorizeRequests() //

                // allow authenticated requests to api
                .antMatchers("/api/**").authenticated() //
                // .antMatchers("/api/admin/**").hasRole("ADMIN").anyRequest().authenticated()

                // allow anonymous resource requests, including /login
                .antMatchers(HttpMethod.GET, "/**").permitAll() //

                // alow sign-up
                .antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll() //

                // alow process registration
                .antMatchers(HttpMethod.POST, SecurityConstants.PROCESS_REGISTRATION_URL).permitAll() //

                //
                .and() //
                .addFilter(new JWTAuthenticationFilter(authenticationManager())) //
                .addFilter(new JWTAuthorizationFilter(authenticationManager()));
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        LOG.debug("WebSecurityConfig.configure(AuthenticationManagerBuilder auth)");

        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
}
