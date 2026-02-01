/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.spring.boot.demo;

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

    private NegotiateSecurityFilter filter;
    private NegotiateSecurityFilterEntryPoint entryPoint;

    /**
     * Autowire constructor injects bean auto-configured by Starter.
     *
     * @param filter
     *            the filter
     * @param entryPoint
     *            the entry point
     */
    public SecurityConfig(final NegotiateSecurityFilter filter, final NegotiateSecurityFilterEntryPoint entryPoint) {
        this.filter = filter;
        this.entryPoint = entryPoint;
    }

    @Bean
    SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests.anyRequest().authenticated())
                .addFilterBefore(filter, BasicAuthenticationFilter.class)
                .exceptionHandling(handling -> handling.authenticationEntryPoint(entryPoint));
        return http.build();
    }

}
