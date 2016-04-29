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
package waffle.mock.http;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * Simple Http Session.
 *
 * @author dblock[at]dblock[dot]org
 */
@SuppressWarnings("deprecation")
public class SimpleHttpSession implements HttpSession {

    /** The attributes. */
    private final Map<String, Object> attributes = new HashMap<>();

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getAttribute(java.lang.String)
     */
    @Override
    public Object getAttribute(final String attributeName) {
        return this.attributes.get(attributeName);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getAttributeNames()
     */
    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getCreationTime()
     */
    @Override
    public long getCreationTime() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getId()
     */
    @Override
    public String getId() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getLastAccessedTime()
     */
    @Override
    public long getLastAccessedTime() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
     */
    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getServletContext()
     */
    @Override
    public ServletContext getServletContext() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getSessionContext()
     */
    @Deprecated
    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getValue(java.lang.String)
     */
    @Deprecated
    @Override
    public Object getValue(final String arg0) {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getValueNames()
     */
    @Deprecated
    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#invalidate()
     */
    @Override
    public void invalidate() {
        // Do Nothing
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#isNew()
     */
    @Override
    public boolean isNew() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#putValue(java.lang.String, java.lang.Object)
     */
    @Deprecated
    @Override
    public void putValue(final String arg0, final Object arg1) {
        // Do Nothing
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
     */
    @Override
    public void removeAttribute(final String attributeName) {
        this.attributes.remove(attributeName);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
     */
    @Deprecated
    @Override
    public void removeValue(final String arg0) {
        // Do Nothing
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#setAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    public void setAttribute(final String attributeName, final Object attributeValue) {
        this.attributes.put(attributeName, attributeValue);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSession#setMaxInactiveInterval(int)
     */
    @Override
    public void setMaxInactiveInterval(final int arg0) {
        // Do Nothing
    }
}
