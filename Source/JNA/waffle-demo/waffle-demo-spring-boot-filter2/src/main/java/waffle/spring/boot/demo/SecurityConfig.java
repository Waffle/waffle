/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.spring.boot.demo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import waffle.spring.NegotiateSecurityFilter;
import waffle.spring.NegotiateSecurityFilterEntryPoint;

/**
 * Demo Spring Boot Security configuration that configures the Negotiate filter to require authentication for all
 * requests.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Filter chain.
     *
     * @param http
     *            the http
     * @param filter
     *            the filter
     * @param entryPoint
     *            the entry point
     *
     * @return the security filter chain
     *
     * @throws Exception
     *             the exception
     */
    @SuppressFBWarnings("OCP_OVERLY_CONCRETE_PARAMETER")
    @Bean
    SecurityFilterChain filterChain(final HttpSecurity http, final NegotiateSecurityFilter filter,
            final NegotiateSecurityFilterEntryPoint entryPoint) throws Exception {
        http.authorizeHttpRequests(requests -> requests.anyRequest().authenticated())
                .addFilterBefore(filter, BasicAuthenticationFilter.class)
                .exceptionHandling(handling -> handling.authenticationEntryPoint(entryPoint));
        return http.build();
    }

}
