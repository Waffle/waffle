/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.servlet.spi.SecurityFilterProvider;
import waffle.servlet.spi.SecurityFilterProviderCollection;
import waffle.util.AuthorizationHeader;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * A Negotiate (NTLM/Kerberos) Security Filter
 * 
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilter implements Filter {

    private static final Logger              LOGGER              = LoggerFactory
                                                                         .getLogger(NegotiateSecurityFilter.class);
    private PrincipalFormat                  principalFormat     = PrincipalFormat.FQN;
    private PrincipalFormat                  roleFormat          = PrincipalFormat.FQN;
    private SecurityFilterProviderCollection providers;
    private IWindowsAuthProvider             auth;
    private boolean                          allowGuestLogin     = true;
    private boolean                          impersonate;
    private static final String              PRINCIPALSESSIONKEY = NegotiateSecurityFilter.class.getName()
                                                                         + ".PRINCIPAL";

    public NegotiateSecurityFilter() {
        LOGGER.debug("[waffle.servlet.NegotiateSecurityFilter] loaded");
    }

    @Override
    public void destroy() {
        LOGGER.info("[waffle.servlet.NegotiateSecurityFilter] stopped");
    }

    @Override
    public void doFilter(final ServletRequest sreq, final ServletResponse sres, final FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) sreq;
        final HttpServletResponse response = (HttpServletResponse) sres;

        LOGGER.debug("{} {}, contentlength: {}", request.getMethod(), request.getRequestURI(),
                Integer.valueOf(request.getContentLength()));

        if (doFilterPrincipal(request, response, chain)) {
            // previously authenticated user
            return;
        }

        final AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);

        // authenticate user
        if (!authorizationHeader.isNull()) {

            // log the user in using the token
            IWindowsIdentity windowsIdentity;
            try {
                windowsIdentity = this.providers.doFilter(request, response);
                if (windowsIdentity == null) {
                    return;
                }
            } catch (IOException e) {
                LOGGER.warn("error logging in user: {}", e.getMessage());
                LOGGER.trace("{}", e);
                sendUnauthorized(response, true);
                return;
            }

            IWindowsImpersonationContext ctx = null;
            try {
                if (!this.allowGuestLogin && windowsIdentity.isGuest()) {
                    LOGGER.warn("guest login disabled: {}", windowsIdentity.getFqn());
                    sendUnauthorized(response, true);
                    return;
                }

                LOGGER.debug("logged in user: {} ({})", windowsIdentity.getFqn(), windowsIdentity.getSidString());

                HttpSession session = request.getSession(true);
                if (session == null) {
                    throw new ServletException("Expected HttpSession");
                }

                Subject subject = (Subject) session.getAttribute("javax.security.auth.subject");
                if (subject == null) {
                    subject = new Subject();
                }

                WindowsPrincipal windowsPrincipal = null;
                if (this.impersonate) {
                    windowsPrincipal = new AutoDisposableWindowsPrincipal(windowsIdentity, this.principalFormat,
                            this.roleFormat);
                } else {
                    windowsPrincipal = new WindowsPrincipal(windowsIdentity, this.principalFormat, this.roleFormat);
                }

                LOGGER.debug("roles: {}", windowsPrincipal.getRolesString());
                subject.getPrincipals().add(windowsPrincipal);
                session.setAttribute("javax.security.auth.subject", subject);

                LOGGER.info("successfully logged in user: {}", windowsIdentity.getFqn());

                request.getSession().setAttribute(PRINCIPALSESSIONKEY, windowsPrincipal);

                NegotiateRequestWrapper requestWrapper = new NegotiateRequestWrapper(request, windowsPrincipal);

                if (this.impersonate) {
                    LOGGER.debug("impersonating user");
                    ctx = windowsIdentity.impersonate();
                }

                chain.doFilter(requestWrapper, response);
            } finally {
                if (this.impersonate && ctx != null) {
                    LOGGER.debug("terminating impersonation");
                    ctx.revertToSelf();
                } else {
                    windowsIdentity.dispose();
                }
            }

            return;
        }

        LOGGER.debug("authorization required");
        sendUnauthorized(response, false);
    }

    /**
     * Filter for a previously logged on user.
     * 
     * @param request
     *            HTTP request.
     * @param response
     *            HTTP response.
     * @param chain
     *            Filter chain.
     * @return True if a user already authenticated.
     * @throws ServletException
     * @throws IOException
     */
    private boolean doFilterPrincipal(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain) throws IOException, ServletException {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                principal = (Principal) session.getAttribute(PRINCIPALSESSIONKEY);
            }
        }

        if (principal == null) {
            // no principal in this request
            return false;
        }

        if (this.providers.isPrincipalException(request)) {
            // the providers signal to authenticate despite an existing principal, eg. NTLM post
            return false;
        }

        // user already authenticated

        if (principal instanceof WindowsPrincipal) {
            LOGGER.debug("previously authenticated Windows user: {}", principal.getName());
            final WindowsPrincipal windowsPrincipal = (WindowsPrincipal) principal;

            if (this.impersonate && windowsPrincipal.getIdentity() == null) {
                // This can happen when the session has been serialized then de-serialized
                // and because the IWindowsIdentity field is transient. In this case re-ask an
                // authentication to get a new identity.
                return false;
            }

            final NegotiateRequestWrapper requestWrapper = new NegotiateRequestWrapper(request, windowsPrincipal);

            IWindowsImpersonationContext ctx = null;
            if (this.impersonate) {
                LOGGER.debug("re-impersonating user");
                ctx = windowsPrincipal.getIdentity().impersonate();
            }
            try {
                chain.doFilter(requestWrapper, response);
            } finally {
                if (this.impersonate && ctx != null) {
                    LOGGER.debug("terminating impersonation");
                    ctx.revertToSelf();
                }
            }
        } else {
            LOGGER.debug("previously authenticated user: {}", principal.getName());
            chain.doFilter(request, response);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        Map<String, String> implParameters = new HashMap<String, String>();

        String authProvider = null;
        String[] providerNames = null;
        if (filterConfig != null) {
            Enumeration<String> parameterNames = filterConfig.getInitParameterNames();
            while (parameterNames.hasMoreElements()) {
                String parameterName = parameterNames.nextElement();
                String parameterValue = filterConfig.getInitParameter(parameterName);
                LOGGER.debug("{}={}", parameterName, parameterValue);
                if (parameterName.equals("principalFormat")) {
                    this.principalFormat = PrincipalFormat.valueOf(parameterValue.toUpperCase(Locale.ENGLISH));
                } else if (parameterName.equals("roleFormat")) {
                    this.roleFormat = PrincipalFormat.valueOf(parameterValue.toUpperCase(Locale.ENGLISH));
                } else if (parameterName.equals("allowGuestLogin")) {
                    this.allowGuestLogin = Boolean.parseBoolean(parameterValue);
                } else if (parameterName.equals("impersonate")) {
                    this.impersonate = Boolean.parseBoolean(parameterValue);
                } else if (parameterName.equals("securityFilterProviders")) {
                    providerNames = parameterValue.split("\\s+");
                } else if (parameterName.equals("authProvider")) {
                    authProvider = parameterValue;
                } else {
                    implParameters.put(parameterName, parameterValue);
                }
            }
        }

        if (authProvider != null) {
            try {
                this.auth = (IWindowsAuthProvider) Class.forName(authProvider).getConstructor().newInstance();
            } catch (ClassNotFoundException e) {
                LOGGER.error("error loading '{}': {}", authProvider, e.getMessage());
                LOGGER.trace("{}", e);
                throw new ServletException(e);
            } catch (IllegalArgumentException e) {
                LOGGER.error("error loading '{}': {}", authProvider, e.getMessage());
                LOGGER.trace("{}", e);
                throw new ServletException(e);
            } catch (SecurityException e) {
                LOGGER.error("error loading '{}': {}", authProvider, e.getMessage());
                LOGGER.trace("{}", e);
                throw new ServletException(e);
            } catch (InstantiationException e) {
                LOGGER.error("error loading '{}': {}", authProvider, e.getMessage());
                LOGGER.trace("{}", e);
                throw new ServletException(e);
            } catch (IllegalAccessException e) {
                LOGGER.error("error loading '{}': {}", authProvider, e.getMessage());
                LOGGER.trace("{}", e);
                throw new ServletException(e);
            } catch (InvocationTargetException e) {
                LOGGER.error("error loading '{}': {}", authProvider, e.getMessage());
                LOGGER.trace("{}", e);
                throw new ServletException(e);
            } catch (NoSuchMethodException e) {
                LOGGER.error("error loading '{}': {}", authProvider, e.getMessage());
                LOGGER.trace("{}", e);
                throw new ServletException(e);
            }
        }

        if (this.auth == null) {
            this.auth = new WindowsAuthProviderImpl();
        }

        if (providerNames != null) {
            this.providers = new SecurityFilterProviderCollection(providerNames, this.auth);
        }

        // create default providers if none specified
        if (this.providers == null) {
            LOGGER.debug("initializing default security filter providers");
            this.providers = new SecurityFilterProviderCollection(this.auth);
        }

        // apply provider implementation parameters
        for (Entry<String, String> implParameter : implParameters.entrySet()) {
            String[] classAndParameter = implParameter.getKey().split("/", 2);
            if (classAndParameter.length == 2) {
                try {

                    LOGGER.debug("setting {}, {}={}", classAndParameter[0], classAndParameter[1],
                            implParameter.getValue());

                    SecurityFilterProvider provider = this.providers.getByClassName(classAndParameter[0]);
                    provider.initParameter(classAndParameter[1], implParameter.getValue());

                } catch (ClassNotFoundException e) {
                    LOGGER.error("invalid class: {} in {}", classAndParameter[0], implParameter.getKey());
                    throw new ServletException(e);
                } catch (Exception e) {
                    LOGGER.error("{}: error setting '{}': {}", classAndParameter[0], classAndParameter[1],
                            e.getMessage());
                    LOGGER.trace("{}", e);
                    throw new ServletException(e);
                }
            } else {
                LOGGER.error("Invalid parameter: {}", implParameter.getKey());
                throw new ServletException("Invalid parameter: " + implParameter.getKey());
            }
        }

        LOGGER.info("[waffle.servlet.NegotiateSecurityFilter] started");
    }

    /**
     * Set the principal format.
     * 
     * @param format
     *            Principal format.
     */
    public void setPrincipalFormat(String format) {
        this.principalFormat = PrincipalFormat.valueOf(format.toUpperCase(Locale.ENGLISH));
        LOGGER.info("principal format: {}", this.principalFormat);
    }

    /**
     * Principal format.
     * 
     * @return Principal format.
     */
    public PrincipalFormat getPrincipalFormat() {
        return this.principalFormat;
    }

    /**
     * Set the principal format.
     * 
     * @param format
     *            Role format.
     */
    public void setRoleFormat(final String format) {
        this.roleFormat = PrincipalFormat.valueOf(format.toUpperCase(Locale.ENGLISH));
        LOGGER.info("role format: {}", this.roleFormat);
    }

    /**
     * Principal format.
     * 
     * @return Role format.
     */
    public PrincipalFormat getRoleFormat() {
        return this.roleFormat;
    }

    /**
     * Send a 401 Unauthorized along with protocol authentication headers.
     * 
     * @param response
     *            HTTP Response
     * @param close
     *            Close connection.
     */
    private void sendUnauthorized(final HttpServletResponse response, final boolean close) {
        try {
            this.providers.sendUnauthorized(response);
            if (close) {
                response.setHeader("Connection", "close");
            } else {
                response.setHeader("Connection", "keep-alive");
            }
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Windows auth provider.
     * 
     * @return IWindowsAuthProvider.
     */
    public IWindowsAuthProvider getAuth() {
        return this.auth;
    }

    /**
     * Set Windows auth provider.
     * 
     * @param provider
     *            Class implements IWindowsAuthProvider.
     */
    public void setAuth(final IWindowsAuthProvider provider) {
        this.auth = provider;
    }

    /**
     * True if guest login is allowed.
     * 
     * @return True if guest login is allowed, false otherwise.
     */
    public boolean isAllowGuestLogin() {
        return this.allowGuestLogin;
    }

    /**
     * Enable/Disable impersonation
     * 
     * @param value
     *            true to enable impersonation, false otherwise
     */
    public void setImpersonate(final boolean value) {
        this.impersonate = value;
    }

    /**
     * @return true if impersonation is enabled, false otherwise
     */
    public boolean isImpersonate() {
        return this.impersonate;
    }

    /**
     * Security filter providers.
     * 
     * @return A collection of security filter providers.
     */
    public SecurityFilterProviderCollection getProviders() {
        return this.providers;
    }
}
