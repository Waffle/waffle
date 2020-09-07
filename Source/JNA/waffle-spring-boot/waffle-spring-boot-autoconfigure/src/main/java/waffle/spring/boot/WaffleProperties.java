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
package waffle.spring.boot;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

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
