/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.spring.boot.demo;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import waffle.spring.NegotiateSecurityFilter;
import waffle.spring.NegotiateSecurityFilterEntryPoint;

/**
 * Demo Spring Boot Security configuration that configures the Negotiate filter to require authentication for all
 * requests.
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
    public SecurityConfig(NegotiateSecurityFilter filter, NegotiateSecurityFilterEntryPoint entryPoint) {
        this.filter = filter;
        this.entryPoint = entryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated().and()
                .addFilterBefore(filter, BasicAuthenticationFilter.class).exceptionHandling()
                .authenticationEntryPoint(entryPoint);
    }

}
