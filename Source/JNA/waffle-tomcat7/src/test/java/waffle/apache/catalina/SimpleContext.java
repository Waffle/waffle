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
package waffle.apache.catalina;

import javax.servlet.ServletContext;

import org.apache.catalina.Authenticator;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Manager;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/**
 * Simple Context.
 * 
 * @author dblock[at]dblock[dot]org
 */
public abstract class SimpleContext implements Context {

    private String         path;
    private String         name;
    private Realm          realm;
    private Container      parent;
    private ServletContext servletContext;
    private Pipeline       pipeline;
    private Authenticator  authenticator;

    /**
     * Get Authenticator Used By Waffle.
     */
    @Override
    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    /**
     * Get Logger for Waffle.
     */
    @Override
    public Log getLogger() {
        return LogFactory.getLog(SimpleContext.class);
    }

    /**
     * Get null Manager for Waffle.
     */
    @Override
    public Manager getManager() {
        return null;
    }

    /**
     * Get Name Used By Waffle.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get Parent Used By Waffle.
     */
    @Override
    public Container getParent() {
        return this.parent;
    }

    /**
     * Get Path Used By Waffle.
     */
    @Override
    public String getPath() {
        return this.path;
    }

    /**
     * Get Pipeline Used By Waffle.
     */
    @Override
    public Pipeline getPipeline() {
        return this.pipeline;
    }

    /**
     * Get Realm Used By Waffle.
     */
    @Override
    public Realm getRealm() {
        return this.realm;
    }

    /**
     * Get Servlet Context Used By Waffle.
     */
    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    /**
     * Set Authenticator Used By Waffle.
     * 
     * @param value
     */
    public void setAuthenticator(final Authenticator value) {
        this.authenticator = value;
    }

    /**
     * Set Name Used By Waffle.
     */
    @Override
    public void setName(final String value) {
        this.name = value;
    }

    /**
     * Set Parent Used By Waffle.
     */
    @Override
    public void setParent(final Container container) {
        this.parent = container;
    }

    /**
     * Set Path Used By Waffle.
     */
    @Override
    public void setPath(final String value) {
        this.path = value;
    }

    /**
     * Set Pipeline Used By Waffle.
     * 
     * @param value
     */
    public void setPipeline(final Pipeline value) {
        this.pipeline = value;
    }

    /**
     * Set Realm Used By Waffle.
     */
    @Override
    public void setRealm(final Realm value) {
        this.realm = value;
    }

    /**
     * Set Servlet Context Used By Waffle.
     */
    public void setServletContext(final ServletContext value) {
        this.servletContext = value;
    }

}
