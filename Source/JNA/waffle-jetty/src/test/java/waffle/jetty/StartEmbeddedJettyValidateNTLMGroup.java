/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.jetty;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

import waffle.servlet.NegotiateSecurityFilter;
import waffle.servlet.WindowsPrincipal;
import waffle.windows.auth.WindowsAccount;

/**
 * A simple embedded server that lets us run directly within Eclipse with added group validation
 * 
 * Browse to http://localhost:8080/ to test.
 */
public class StartEmbeddedJettyValidateNTLMGroup {

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StartEmbeddedJettyValidateNTLMGroup.class);

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(final String args[]) {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

        final Server server = new Server(8080);

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        final ServletHandler handler = new ServletHandler();
        final ServletHolder sh = new ServletHolder(new InfoServlet());
        context.addServlet(sh, "/*");

        final FilterHolder fh = handler.addFilterWithMapping(NegotiateSecurityFilter.class, "/*",
                EnumSet.of(DispatcherType.REQUEST));
        StartEmbeddedJettyValidateNTLMGroup.setFilterParams(fh);
        context.addFilter(fh, "/*", EnumSet.of(DispatcherType.REQUEST));

        context.setHandler(handler);
        server.setHandler(context);

        try {
            server.start();
        } catch (final Exception e) {
            StartEmbeddedJettyValidateNTLMGroup.LOGGER.trace("", e);
        }
    }

    /**
     * Sets the filter params.
     *
     * @param fh
     *            the new filter params
     */
    private static void setFilterParams(final FilterHolder fh) {
        fh.setInitParameter("principalFormat", "fqn");
        fh.setInitParameter("roleFormat", "both");

        fh.setInitParameter("allowGuestLogin", "true");
        fh.setInitParameter("impersonate", "true");

        fh.setInitParameter("securityFilterProviders",
                "waffle.servlet.spi.NegotiateSecurityFilterProvider waffle.servlet.spi.BasicSecurityFilterProvider");
        fh.setInitParameter("waffle.servlet.spi.NegotiateSecurityFilterProvider/protocols", "Negotiate NTLM");

        fh.setInitParameter("waffle.servlet.spi.BasicSecurityFilterProvider/realm", "SecureServiceRunner");
    }

    /**
     * The Class InfoServlet.
     */
    public static class InfoServlet extends HttpServlet {

        /** The Constant serialVersionUID. */
        private static final long         serialVersionUID = 1L;

        /** The authorised groups. */
        private static final List<String> authorisedGroups = Arrays.asList("NTGroup1", "NTGroup2");

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
         * javax.servlet.http.HttpServletResponse)
         */
        @Override
        public void doGet(final HttpServletRequest request, final HttpServletResponse response)
                throws ServletException, IOException {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);

            final boolean isUserAuthorised = this.isUserAuthorised(request, InfoServlet.authorisedGroups);
            if (isUserAuthorised) {
                response.getWriter().println("User is authorised");
            } else {
                response.getWriter().println("User is not authorised");
            }
        }

        /**
         * Checks if is user authorised.
         *
         * @param request
         *            the request
         * @param authorizedGroups
         *            the authorized groups
         * @return true, if is user authorised
         */
        private boolean isUserAuthorised(final HttpServletRequest request, final List<String> authorizedGroups) {
            final List<String> usersGroups = this.getUsersGroups(request);

            final boolean noOverlappingGroups = Collections.disjoint(authorizedGroups, usersGroups);
            return !noOverlappingGroups;
        }

        /**
         * Gets the users groups.
         *
         * @param request
         *            the request
         * @return the users groups
         */
        private List<String> getUsersGroups(final HttpServletRequest request) {
            final List<String> result = new ArrayList<>();
            final Principal principal = request.getUserPrincipal();
            if (principal instanceof WindowsPrincipal) {
                String groupName;
                final WindowsPrincipal windowsPrincipal = (WindowsPrincipal) principal;
                for (final WindowsAccount account : windowsPrincipal.getGroups().values()) {
                    groupName = this.getGroupName(account.getDomain(), account.getFqn());
                    result.add(groupName);
                }
            }
            return result;
        }

        /**
         * Gets the group name.
         *
         * @param domain
         *            the domain
         * @param groupString
         *            the group string
         * @return the group name
         */
        private String getGroupName(final String domain, final String groupString) {
            if (domain == null || groupString == null) {
                return "";
            }
            return groupString.split(domain)[1].substring(1);
        }
    }

}
