/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2017 Application Security, Inc.
 * Copyright (c) 2017 Michael Goldgeier <mbgold@gmail.com>
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
 * The configuration properties that can be used with Spring Boot to
 * configure WAFFLE.
 */
@ConfigurationProperties(prefix = "waffle")
public class WaffleProperties {

	/** The principal format can be any of the options specified by {@link PrincipalFormat} */
	private String principalFormat = "fqn";
	/** The principal format can be any of the options specified by {@link PrincipalFormat} */
	private String roleFormat = "fqn";
	/** Enable or disable guest login */
	private boolean allowGuestLogin = false;
	/** Configuration properties for single-sign-on */
	private SingleSignOnProperties sso;
	
	public String getPrincipalFormat() {
		return principalFormat;
	}
	public void setPrincipalFormat(String principalFormat) {
		this.principalFormat = principalFormat;
	}
	public String getRoleFormat() {
		return roleFormat;
	}
	public void setRoleFormat(String roleFormat) {
		this.roleFormat = roleFormat;
	}
	public boolean isAllowGuestLogin() {
		return allowGuestLogin;
	}
	public void setAllowGuestLogin(boolean allowGuestLogin) {
		this.allowGuestLogin = allowGuestLogin;
	}
	public SingleSignOnProperties getSso() {
		return sso;
	}
	public void setSso(SingleSignOnProperties sso) {
		this.sso = sso;
	}

	public static class SingleSignOnProperties {
		
		/** Enable or disable single-sign-on using Negotiate protocol */
		private boolean enabled = false;
		/** Enable fall back to Basic protocol for unsupported browsers */
		private boolean basicEnabled = false;
		/** List of protocols to support: Can be Negotiate, NTLM */
		private List<String> protocols = Arrays.asList("Negotiate", "NTLM");
		/** Enable WAFFLE impersonate option */
		private boolean impersonate = false;
		
		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		public boolean isBasicEnabled() {
			return basicEnabled;
		}
		public void setBasicEnabled(boolean basicEnabled) {
			this.basicEnabled = basicEnabled;
		}
		public List<String> getProtocols() {
			return protocols;
		}
		public void setProtocols(List<String> protocols) {
			this.protocols = protocols;
		}
		public boolean isImpersonate() {
			return impersonate;
		}
		public void setImpersonate(boolean impersonate) {
			this.impersonate = impersonate;
		}
		
	}
}
