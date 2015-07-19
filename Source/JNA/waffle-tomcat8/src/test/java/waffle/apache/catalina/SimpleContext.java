/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2015 Application Security, Inc.
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

    /** The path. */
    private String         path;
    
    /** The name. */
    private String         name;
    
    /** The realm. */
    private Realm          realm;
    
    /** The parent. */
    private Container      parent;
    
    /** The servlet context. */
    private ServletContext servletContext;
    
    /** The pipeline. */
    private Pipeline       pipeline;
    
    /** The authenticator. */
    private Authenticator  authenticator;

    /**
     * Get Authenticator Used By Waffle.
     *
     * @return the authenticator
     */
    @Override
    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    /**
     * Get domain for Waffle.
     *
     * @return the domain
     */
    @Override
    public String getDomain() {
        return "Waffle-Domain";
    }

    /**
     * Get Logger for Waffle.
     *
     * @return the logger
     */
    @Override
    public Log getLogger() {
        return LogFactory.getLog(SimpleContext.class);
    }

    /**
     * Get null Manager for Waffle.
     *
     * @return the manager
     */
    @Override
    public Manager getManager() {
        return null;
    }

    /**
     * Get MBean String for Waffle.
     *
     * @return the m bean key properties
     */
    @Override
    public String getMBeanKeyProperties() {
        return "Waffle-MBeans";
    }

    /**
     * Get Name Used By Waffle.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get Parent Used By Waffle.
     *
     * @return the parent
     */
    @Override
    public Container getParent() {
        return this.parent;
    }

    /**
     * Get Path Used By Waffle.
     *
     * @return the path
     */
    @Override
    public String getPath() {
        return this.path;
    }

    /**
     * Get Pipeline Used By Waffle.
     *
     * @return the pipeline
     */
    @Override
    public Pipeline getPipeline() {
        return this.pipeline;
    }

    /**
     * Get Realm Used By Waffle.
     *
     * @return the realm
     */
    @Override
    public Realm getRealm() {
        return this.realm;
    }

    /**
     * Get Servlet Context Used By Waffle.
     *
     * @return the servlet context
     */
    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    /**
     * Set Authenticator Used By Waffle.
     *
     * @param value the new authenticator
     */
    public void setAuthenticator(final Authenticator value) {
        this.authenticator = value;
    }

    /**
     * Set Name Used By Waffle.
     *
     * @param value the new name
     */
    @Override
    public void setName(final String value) {
        this.name = value;
    }

    /**
     * Set Parent Used By Waffle.
     *
     * @param container the new parent
     */
    @Override
    public void setParent(final Container container) {
        this.parent = container;
    }

    /**
     * Set Path Used By Waffle.
     *
     * @param value the new path
     */
    @Override
    public void setPath(final String value) {
        this.path = value;
    }

    /**
     * Set Pipeline Used By Waffle.
     *
     * @param value the new pipeline
     */
    public void setPipeline(final Pipeline value) {
        this.pipeline = value;
    }

    /**
     * Set Realm Used By Waffle.
     *
     * @param value the new realm
     */
    @Override
    public void setRealm(final Realm value) {
        this.realm = value;
    }

    /**
     * Set Servlet Context Used By Waffle.
     *
     * @param value the new servlet context
     */
    public void setServletContext(final ServletContext value) {
        this.servletContext = value;
    }

}
