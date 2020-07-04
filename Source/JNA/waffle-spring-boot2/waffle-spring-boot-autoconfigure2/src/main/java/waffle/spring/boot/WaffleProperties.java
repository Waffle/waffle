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
package waffle.spring.boot;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import waffle.windows.auth.PrincipalFormat;

/**
 * The configuration properties that can be used with Spring Boot to configure WAFFLE.
 */
@ConfigurationProperties(prefix = "waffle")
public class WaffleProperties {

    /** The principal format can be any of the options specified by {@link PrincipalFormat}. */
    private String principalFormat = "fqn";

    /** The principal format can be any of the options specified by {@link PrincipalFormat}. */
    private String roleFormat = "fqn";

    /** Enable or disable guest login. */
    private boolean allowGuestLogin = false;

    /** Configuration properties for single-sign-on. */
    private SingleSignOnProperties sso;

    /**
     * Gets the principal format.
     *
     * @return the principal format
     */
    public String getPrincipalFormat() {
        return this.principalFormat;
    }

    /**
     * Sets the principal format.
     *
     * @param principalFormat
     *            the new principal format
     */
    public void setPrincipalFormat(final String principalFormat) {
        this.principalFormat = principalFormat;
    }

    /**
     * Gets the role format.
     *
     * @return the role format
     */
    public String getRoleFormat() {
        return this.roleFormat;
    }

    /**
     * Sets the role format.
     *
     * @param roleFormat
     *            the new role format
     */
    public void setRoleFormat(final String roleFormat) {
        this.roleFormat = roleFormat;
    }

    /**
     * Checks if is allow guest login.
     *
     * @return true, if is allow guest login
     */
    public boolean isAllowGuestLogin() {
        return this.allowGuestLogin;
    }

    /**
     * Sets the allow guest login.
     *
     * @param allowGuestLogin
     *            the new allow guest login
     */
    public void setAllowGuestLogin(final boolean allowGuestLogin) {
        this.allowGuestLogin = allowGuestLogin;
    }

    /**
     * Gets the sso.
     *
     * @return the sso
     */
    public SingleSignOnProperties getSso() {
        return this.sso;
    }

    /**
     * Sets the sso.
     *
     * @param sso
     *            the new sso
     */
    public void setSso(final SingleSignOnProperties sso) {
        this.sso = sso;
    }

    /**
     * The Class SingleSignOnProperties.
     */
    public static class SingleSignOnProperties {

        /** Enable or disable single-sign-on using Negotiate protocol. */
        private boolean enabled = false;

        /** Enable fall back to Basic protocol for unsupported browsers. */
        private boolean basicEnabled = false;

        /** List of protocols to support: Can be Negotiate, NTLM. */
        private List<String> protocols = Arrays.asList("Negotiate", "NTLM");

        /** Enable WAFFLE impersonate option. */
        private boolean impersonate = false;

        /**
         * Checks if is enabled.
         *
         * @return true, if is enabled
         */
        public boolean isEnabled() {
            return this.enabled;
        }

        /**
         * Sets the enabled.
         *
         * @param enabled
         *            the new enabled
         */
        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * Checks if is basic enabled.
         *
         * @return true, if is basic enabled
         */
        public boolean isBasicEnabled() {
            return this.basicEnabled;
        }

        /**
         * Sets the basic enabled.
         *
         * @param basicEnabled
         *            the new basic enabled
         */
        public void setBasicEnabled(final boolean basicEnabled) {
            this.basicEnabled = basicEnabled;
        }

        /**
         * Gets the protocols.
         *
         * @return the protocols
         */
        public List<String> getProtocols() {
            return this.protocols;
        }

        /**
         * Sets the protocols.
         *
         * @param protocols
         *            the new protocols
         */
        public void setProtocols(final List<String> protocols) {
            this.protocols = protocols;
        }

        /**
         * Checks if is impersonate.
         *
         * @return true, if is impersonate
         */
        public boolean isImpersonate() {
            return this.impersonate;
        }

        /**
         * Sets the impersonate.
         *
         * @param impersonate
         *            the new impersonate
         */
        public void setImpersonate(final boolean impersonate) {
            this.impersonate = impersonate;
        }

    }
}
