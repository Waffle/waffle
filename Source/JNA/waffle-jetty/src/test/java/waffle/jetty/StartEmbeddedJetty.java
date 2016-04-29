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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple embedded server that lets us run directly within Eclipse.
 */
public class StartEmbeddedJetty {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StartEmbeddedJetty.class);

    /**
     * Main method.
     * 
     * @param args
     *            input arguments to main.
     * @throws Exception
     *             Exception thrown.
     */
    public static void main(final String[] args) throws Exception {
        final String path = "../waffle-demo/waffle-filter";

        final File dir = new File(path);
        if (!dir.exists()) {
            throw new FileNotFoundException("Can not find webapp: " + dir.getAbsolutePath());
        }

        final Server server = new Server(8080);
        final WebAppContext context = new WebAppContext();
        context.setServer(server);
        context.setContextPath("/");
        context.setWar(path);

        // Try adding JSP
        final ServletHolder jsp = context.addServlet(JspServlet.class, "*.jsp");
        jsp.setInitParameter("classpath", context.getClassPath());

        server.setHandler(context);

        try {
            StartEmbeddedJetty.LOGGER.info(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
            server.start();
            if (System.in.read() == -1) {
                StartEmbeddedJetty.LOGGER.error("End of Stream reached");
                return;
            }
            StartEmbeddedJetty.LOGGER.info(">>> STOPPING EMBEDDED JETTY SERVER");
            server.stop();
            server.join();
        } catch (final IOException e) {
            StartEmbeddedJetty.LOGGER.error("", e);
            System.exit(100);
        } catch (final Exception e) {
            StartEmbeddedJetty.LOGGER.error("", e);
            System.exit(100);
        }
    }

}