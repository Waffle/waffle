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
 * Browse to http://localhost:8080/ to test
 * 
 */
public class StartEmbeddedJettyValidateNTLMGroup {

    private static Logger LOGGER = LoggerFactory.getLogger(StartEmbeddedJettyValidateNTLMGroup.class);

    public static void main(String args[]) {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ServletHandler handler = new ServletHandler();
        ServletHolder sh = new ServletHolder(new InfoServlet());
        context.addServlet(sh, "/*");

        FilterHolder fh = handler.addFilterWithMapping(NegotiateSecurityFilter.class, "/*",
                EnumSet.of(DispatcherType.REQUEST));
        setFilterParams(fh);
        context.addFilter(fh, "/*", EnumSet.of(DispatcherType.REQUEST));

        context.setHandler(handler);
        server.setHandler(context);

        try {
            server.start();
        } catch (Exception e) {
            StartEmbeddedJettyValidateNTLMGroup.LOGGER.trace("{}", e);
        }
    }

    private static void setFilterParams(FilterHolder fh) {
        fh.setInitParameter("principalFormat", "fqn");
        fh.setInitParameter("roleFormat", "both");

        fh.setInitParameter("allowGuestLogin", "true");
        fh.setInitParameter("impersonate", "true");

        fh.setInitParameter("securityFilterProviders",
                "waffle.servlet.spi.NegotiateSecurityFilterProvider waffle.servlet.spi.BasicSecurityFilterProvider");
        fh.setInitParameter("waffle.servlet.spi.NegotiateSecurityFilterProvider/protocols", "Negotiate NTLM");

        fh.setInitParameter("waffle.servlet.spi.BasicSecurityFilterProvider/realm", "SecureServiceRunner");
    }

    public static class InfoServlet extends HttpServlet {

        private static final long   serialVersionUID = 1L;

        private static List<String> authorisedGroups = Arrays.asList("NTGroup1", "NTGroup2");

        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                IOException {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);

            boolean isUserAuthorised = isUserAuthorised(request, authorisedGroups);
            if (isUserAuthorised) {
                response.getWriter().println("User is authorised");
            } else {
                response.getWriter().println("User is not authorised");
            }
        }

        private boolean isUserAuthorised(HttpServletRequest request, List<String> authorizedGroups) {
            List<String> usersGroups = getUsersGroups(request);

            boolean noOverlappingGroups = Collections.disjoint(authorizedGroups, usersGroups);
            if (!noOverlappingGroups) {
                return true;
            }
            return false;
        }

        private List<String> getUsersGroups(HttpServletRequest request) {
            List<String> result = new ArrayList<String>();
            Principal principal = request.getUserPrincipal();
            if (principal instanceof WindowsPrincipal) {
                WindowsPrincipal windowsPrincipal = (WindowsPrincipal) principal;
                for (WindowsAccount account : windowsPrincipal.getGroups().values()) {
                    String groupName = getGroupName(account.getDomain(), account.getFqn());
                    result.add(groupName);
                }
            }
            return result;
        }

        private String getGroupName(String domain, String groupString) {
            if (domain == null || groupString == null) {
                return "";
            }
            String group = groupString.split(domain)[1];
            return group.substring(1);
        }
    }

}
