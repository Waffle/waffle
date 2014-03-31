/*******************************************************************************
 * Waffle (https://github.com/dblock/waffle)
 * 
 * Copyright (c) 2010 Application Security, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 *******************************************************************************/
package waffle.jetty;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;


/**
 * A simple embedded server that lets us run directly within Eclipse
 */
public class StartEmbeddedJetty
{
  public static void main(String[] args) throws Exception
  {
    String path = "../demo/waffle-filter";
    
    File dir = new File( path );
    if(!dir.exists()) {
      throw new FileNotFoundException("Can not find webapp: "+dir.getAbsolutePath());
    }
    
    Server server = new Server(8080);
    //SocketConnector connector = new SocketConnector();

    // Set some timeout options to make debugging easier.
//    connector.setMaxIdleTime(1000 * 60 * 60);
//    connector.setSoLingerTime(-1);
//    connector.setPort(8080);
//    connector.setRequestHeaderSize(20*1044); // 20K for big request headers
//    server.setConnectors(new Connector[] { connector });

    WebAppContext context = new WebAppContext();
    context.setServer(server);
    context.setContextPath("/");
    context.setWar(path);
    
    // Try adding JSP
    try {
      ServletHolder jsp = context.addServlet(JspServlet.class, "*.jsp");
      jsp.setInitParameter("classpath", context.getClassPath());
    }
    catch( Exception ex) {
      System.err.println("Error adding JSP Support: "+ex.toString());
    }

    server.setHandler(context);

    try {
      System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
      server.start();
      System.in.read();
      System.out.println(">>> STOPPING EMBEDDED JETTY SERVER");
      server.stop();
      server.join();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(100);
    }
  }
}