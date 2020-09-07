/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
