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

import org.apache.catalina.Context;
import org.apache.catalina.Realm;

/**
 * Simple Context.
 * 
 * @author dblock[at]dblock[dot]org
 */
public abstract class SimpleContext implements Context {

    private Realm          realm;
    private ServletContext servletContext;

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
