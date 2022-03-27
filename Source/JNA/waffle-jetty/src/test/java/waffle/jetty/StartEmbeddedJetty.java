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