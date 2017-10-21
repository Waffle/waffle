/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2017 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.spring.boot;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;

import waffle.servlet.spi.BasicSecurityFilterProvider;
import waffle.servlet.spi.NegotiateSecurityFilterProvider;
import waffle.servlet.spi.SecurityFilterProvider;
import waffle.servlet.spi.SecurityFilterProviderCollection;
import waffle.spring.GrantedAuthorityFactory;
import waffle.spring.NegotiateSecurityFilter;
import waffle.spring.NegotiateSecurityFilterEntryPoint;
import waffle.spring.WindowsAuthenticationProvider;
import waffle.spring.WindowsAuthenticationToken;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * Auto configuration for Spring Boot that configures beans based on properties defined in {@link WaffleProperties}.
 */
@Configuration
@EnableConfigurationProperties(WaffleProperties.class)
public class WaffleAutoConfiguration {

    private WaffleProperties properties;

    public WaffleAutoConfiguration(WaffleProperties properties) {
        this.properties = properties;
    }

    /**
     * The {@link WindowsAuthProviderImpl} instance.
     */
    @Bean
    @ConditionalOnMissingBean
    public WindowsAuthProviderImpl waffleWindowsAuthProvider() {
        return new WindowsAuthProviderImpl();
    }

    /**
     * The default {@link GrantedAuthority} that is applied to all users. Default can be overridden by defining a bean
     * of type {@link GrantedAuthority} with name "defaultGrantedAuthority".
     */
    @Bean
    @ConditionalOnMissingBean(name = "defaultGrantedAuthority")
    public GrantedAuthority defaultGrantedAuthority() {
        return WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY;
    }

    /**
     * The default {@link GrantedAuthorityFactory} that is used. Default can be overridden by defining a bean of type
     * {@link GrantedAuthorityFactory}.
     */
    @Bean
    @ConditionalOnMissingBean
    public GrantedAuthorityFactory grantedAuthorityFactory() {
        return WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY_FACTORY;
    }

    /**
     * The {@link WindowsAuthenticationProvider} that can be used by Spring Security by an {@link AuthenticationManager}
     * to provide authentication.
     * 
     * @param waffleWindowsAuthProvider
     * @param defaultGrantedAuthority
     * @param grantedAuthorityFactory
     */
    @Bean
    @ConditionalOnMissingBean
    public WindowsAuthenticationProvider waffleSpringAuthenticationProvider(
            WindowsAuthProviderImpl waffleWindowsAuthProvider,
            @Qualifier("defaultGrantedAuthority") GrantedAuthority defaultGrantedAuthority,
            GrantedAuthorityFactory grantedAuthorityFactory) {
        WindowsAuthenticationProvider bean = new WindowsAuthenticationProvider();
        bean.setAuthProvider(waffleWindowsAuthProvider);
        bean.setPrincipalFormat(properties.getPrincipalFormat());
        bean.setRoleFormat(properties.getRoleFormat());
        bean.setAllowGuestLogin(properties.isAllowGuestLogin());
        bean.setDefaultGrantedAuthority(defaultGrantedAuthority);
        bean.setGrantedAuthorityFactory(grantedAuthorityFactory);
        return bean;
    }

    /**
     * The {@link NegotiateSecurityFilterProvider} that provides single-sign-on authentication using Negotiate with the
     * configured protocols. Instantiated only when sso is enabled.
     * 
     * @param windowsAuthProvider
     */
    @Bean
    @ConditionalOnProperty("waffle.sso.enabled")
    @ConditionalOnMissingBean
    public NegotiateSecurityFilterProvider negotiateSecurityFilterProvider(
            WindowsAuthProviderImpl windowsAuthProvider) {
        NegotiateSecurityFilterProvider bean = new NegotiateSecurityFilterProvider(windowsAuthProvider);
        bean.setProtocols(properties.getSso().getProtocols());
        return bean;
    }

    /**
     * The {@link BasicSecurityFilterProvider} that provides Basic authentication fall back when using single-sign-on
     * with unsupported browser. Instantiated only when sso is enabled.
     * 
     * @param windowsAuthProvider
     */
    @Bean
    @ConditionalOnProperty("waffle.sso.enabled")
    @ConditionalOnMissingBean
    public BasicSecurityFilterProvider basicSecurityFilterProvider(WindowsAuthProviderImpl windowsAuthProvider) {
        return new BasicSecurityFilterProvider(windowsAuthProvider);
    }

    /**
     * The {@link SecurityFilterProviderCollection} that includes {@link NegotiateSecurityFilterProvider} and/or
     * {@link BasicSecurityFilterProvider} depending on configuration. Instantiated only when sso is enabled.
     * 
     * @param negotiateProvider
     * @param basicProvider
     */
    @Bean
    @ConditionalOnProperty("waffle.sso.enabled")
    @ConditionalOnMissingBean
    public SecurityFilterProviderCollection waffleSecurityFilterProviderCollection(
            NegotiateSecurityFilterProvider negotiateProvider, BasicSecurityFilterProvider basicProvider) {
        SecurityFilterProvider[] providers;
        if (properties.getSso().isBasicEnabled()) {
            providers = new SecurityFilterProvider[] { negotiateProvider, basicProvider };
        } else {
            providers = new SecurityFilterProvider[] { negotiateProvider };
        }
        return new SecurityFilterProviderCollection(providers);
    }

    /**
     * The {@link NegotiateSecurityFilterEntryPoint} for use by the Spring Security {@link AuthenticationManager} when
     * using single-sign-on. Instantiated only when sso is enabled.
     * 
     * @param providers
     */
    @Bean
    @ConditionalOnProperty("waffle.sso.enabled")
    @ConditionalOnMissingBean
    public NegotiateSecurityFilterEntryPoint negotiateSecurityFilterEntryPoint(
            SecurityFilterProviderCollection providers) {
        NegotiateSecurityFilterEntryPoint bean = new NegotiateSecurityFilterEntryPoint();
        bean.setProvider(providers);
        return bean;
    }

    /**
     * The {@link NegotiateSecurityFilter} to be used by Spring Security {@link AuthenticationManager} when using
     * single-sign-on. Instantiated only when sso is enabled.
     * 
     * @param providers
     * @param defaultGrantedAuthority
     * @param grantedAuthorityFactory
     */
    @Bean
    @ConditionalOnProperty("waffle.sso.enabled")
    @ConditionalOnMissingBean
    public NegotiateSecurityFilter waffleNegotiateSecurityFilter(SecurityFilterProviderCollection providers,
            @Qualifier("defaultGrantedAuthority") GrantedAuthority defaultGrantedAuthority,
            GrantedAuthorityFactory grantedAuthorityFactory) {
        NegotiateSecurityFilter bean = new NegotiateSecurityFilter();
        bean.setProvider(providers);
        bean.setPrincipalFormat(properties.getPrincipalFormat());
        bean.setRoleFormat(properties.getRoleFormat());
        bean.setAllowGuestLogin(properties.isAllowGuestLogin());
        bean.setImpersonate(properties.getSso().isImpersonate());
        bean.setDefaultGrantedAuthority(defaultGrantedAuthority);
        bean.setGrantedAuthorityFactory(grantedAuthorityFactory);
        return bean;
    }

    /**
     * When using Spring Boot, {@link Filter}s are automatically registered. In this case, the filter must be manually
     * configured within an {@link AuthenticationManager} and so we must prevent Spring Boot from registering it a
     * second time.
     * 
     * @param filter
     *            The filter that we will be disabling from auto registration.
     */
    @Bean
    @ConditionalOnProperty("waffle.sso.enabled")
    public FilterRegistrationBean waffleNegotiateSecurityFilterRegistrationBean(NegotiateSecurityFilter filter) {
        FilterRegistrationBean bean = new FilterRegistrationBean(filter);
        bean.setEnabled(false);
        return bean;
    }

}
