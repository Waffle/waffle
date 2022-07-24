/*
 * MIT License
 *
 * Copyright (c) 2010-2022 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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

    /** The properties. */
    private final WaffleProperties properties;

    /**
     * Instantiates a new waffle auto configuration.
     *
     * @param properties
     *            the properties
     */
    public WaffleAutoConfiguration(final WaffleProperties properties) {
        this.properties = properties;
    }

    /**
     * The {@link WindowsAuthProviderImpl} instance.
     *
     * @return the windows auth provider impl
     */
    @Bean
    @ConditionalOnMissingBean
    public WindowsAuthProviderImpl waffleWindowsAuthProvider() {
        return new WindowsAuthProviderImpl();
    }

    /**
     * The default {@link GrantedAuthority} that is applied to all users. Default can be overridden by defining a bean
     * of type {@link GrantedAuthority} with name "defaultGrantedAuthority".
     *
     * @return the granted authority
     */
    @Bean
    @ConditionalOnMissingBean(name = "defaultGrantedAuthority")
    public GrantedAuthority defaultGrantedAuthority() {
        return WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY;
    }

    /**
     * The default {@link GrantedAuthorityFactory} that is used. Default can be overridden by defining a bean of type
     * {@link GrantedAuthorityFactory}.
     *
     * @return the granted authority factory
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
     *            the waffle windows auth provider
     * @param defaultGrantedAuthority
     *            the default granted authority
     * @param grantedAuthorityFactory
     *            the granted authority factory
     *
     * @return the windows authentication provider
     */
    @Bean
    @ConditionalOnMissingBean
    public WindowsAuthenticationProvider waffleSpringAuthenticationProvider(
            final WindowsAuthProviderImpl waffleWindowsAuthProvider,
            @Qualifier("defaultGrantedAuthority") final GrantedAuthority defaultGrantedAuthority,
            final GrantedAuthorityFactory grantedAuthorityFactory) {
        final WindowsAuthenticationProvider bean = new WindowsAuthenticationProvider();
        bean.setAuthProvider(waffleWindowsAuthProvider);
        bean.setPrincipalFormat(this.properties.getPrincipalFormat());
        bean.setRoleFormat(this.properties.getRoleFormat());
        bean.setAllowGuestLogin(this.properties.isAllowGuestLogin());
        bean.setDefaultGrantedAuthority(defaultGrantedAuthority);
        bean.setGrantedAuthorityFactory(grantedAuthorityFactory);
        return bean;
    }

    /**
     * The {@link NegotiateSecurityFilterProvider} that provides single-sign-on authentication using Negotiate with the
     * configured protocols. Instantiated only when sso is enabled.
     *
     * @param windowsAuthProvider
     *            the windows auth provider
     *
     * @return the negotiate security filter provider
     */
    @Bean
    @ConditionalOnProperty("waffle.sso.enabled")
    @ConditionalOnMissingBean
    public NegotiateSecurityFilterProvider negotiateSecurityFilterProvider(
            final WindowsAuthProviderImpl windowsAuthProvider) {
        final NegotiateSecurityFilterProvider bean = new NegotiateSecurityFilterProvider(windowsAuthProvider);
        bean.setProtocols(this.properties.getSso().getProtocols());
        return bean;
    }

    /**
     * The {@link BasicSecurityFilterProvider} that provides Basic authentication fall back when using single-sign-on
     * with unsupported browser. Instantiated only when sso is enabled.
     *
     * @param windowsAuthProvider
     *            the windows auth provider
     *
     * @return the basic security filter provider
     */
    @Bean
    @ConditionalOnProperty("waffle.sso.enabled")
    @ConditionalOnMissingBean
    public BasicSecurityFilterProvider basicSecurityFilterProvider(final WindowsAuthProviderImpl windowsAuthProvider) {
        return new BasicSecurityFilterProvider(windowsAuthProvider);
    }

    /**
     * The {@link SecurityFilterProviderCollection} that includes {@link NegotiateSecurityFilterProvider} and/or
     * {@link BasicSecurityFilterProvider} depending on configuration. Instantiated only when sso is enabled.
     *
     * @param negotiateProvider
     *            the negotiate provider
     * @param basicProvider
     *            the basic provider
     *
     * @return the security filter provider collection
     */
    @Bean
    @ConditionalOnProperty("waffle.sso.enabled")
    @ConditionalOnMissingBean
    public SecurityFilterProviderCollection waffleSecurityFilterProviderCollection(
            final NegotiateSecurityFilterProvider negotiateProvider, final BasicSecurityFilterProvider basicProvider) {
        SecurityFilterProvider[] providers;
        if (this.properties.getSso().isBasicEnabled()) {
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
     *            the providers
     *
     * @return the negotiate security filter entry point
     */
    @Bean
    @ConditionalOnProperty("waffle.sso.enabled")
    @ConditionalOnMissingBean
    public NegotiateSecurityFilterEntryPoint negotiateSecurityFilterEntryPoint(
            final SecurityFilterProviderCollection providers) {
        final NegotiateSecurityFilterEntryPoint bean = new NegotiateSecurityFilterEntryPoint();
        bean.setProvider(providers);
        return bean;
    }

    /**
     * The {@link NegotiateSecurityFilter} to be used by Spring Security {@link AuthenticationManager} when using
     * single-sign-on. Instantiated only when sso is enabled.
     *
     * @param providers
     *            the providers
     * @param defaultGrantedAuthority
     *            the default granted authority
     * @param grantedAuthorityFactory
     *            the granted authority factory
     *
     * @return the negotiate security filter
     */
    @Bean
    @ConditionalOnProperty("waffle.sso.enabled")
    @ConditionalOnMissingBean
    public NegotiateSecurityFilter waffleNegotiateSecurityFilter(final SecurityFilterProviderCollection providers,
            @Qualifier("defaultGrantedAuthority") final GrantedAuthority defaultGrantedAuthority,
            final GrantedAuthorityFactory grantedAuthorityFactory) {
        final NegotiateSecurityFilter bean = new NegotiateSecurityFilter();
        bean.setProvider(providers);
        bean.setPrincipalFormat(this.properties.getPrincipalFormat());
        bean.setRoleFormat(this.properties.getRoleFormat());
        bean.setAllowGuestLogin(this.properties.isAllowGuestLogin());
        bean.setImpersonate(this.properties.getSso().isImpersonate());
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
     *
     * @return the filter registration bean
     */
    @Bean
    @ConditionalOnProperty("waffle.sso.enabled")
    public FilterRegistrationBean<NegotiateSecurityFilter> waffleNegotiateSecurityFilterRegistrationBean(
            final NegotiateSecurityFilter filter) {
        final FilterRegistrationBean<NegotiateSecurityFilter> bean = new FilterRegistrationBean<>(filter);
        bean.setEnabled(false);
        return bean;
    }

}
