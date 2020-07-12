/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
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
public class SimpleHttpSession implements HttpSession {

    /** The attributes. */
    private final Map<String, Object> attributes = new HashMap<>();

    @Override
    public Object getAttribute(final String attributeName) {
        return this.attributes.get(attributeName);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public long getCreationTime() {
        return 0;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public long getLastAccessedTime() {
        return 0;
    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    /**
     * Simply remove this if it is ever actually removed from servlet-api
     *
     * @deprecated Remove this once servlet does.
     */
    @Deprecated
    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    /**
     * Simply remove this if it is ever actually removed from servlet-api
     *
     * @deprecated Remove this once servlet does.
     */
    @Deprecated
    @Override
    public Object getValue(final String arg0) {
        return null;
    }

    /**
     * Simply remove this if it is ever actually removed from servlet-api
     *
     * @deprecated Remove this once servlet does.
     */
    @Deprecated
    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void invalidate() {
        // Do Nothing
    }

    @Override
    public boolean isNew() {
        return false;
    }

    /**
     * Simply remove this if it is ever actually removed from servlet-api
     *
     * @deprecated Remove this once servlet does.
     */
    @Deprecated
    @Override
    public void putValue(final String arg0, final Object arg1) {
        // Do Nothing
    }

    @Override
    public void removeAttribute(final String attributeName) {
        this.attributes.remove(attributeName);
    }

    /**
     * Simply remove this if it is ever actually removed from servlet-api
     *
     * @deprecated Remove this once servlet does.
     */
    @Deprecated
    @Override
    public void removeValue(final String arg0) {
        // Do Nothing
    }

    @Override
    public void setAttribute(final String attributeName, final Object attributeValue) {
        this.attributes.put(attributeName, attributeValue);
    }

    @Override
    public void setMaxInactiveInterval(final int arg0) {
        // Do Nothing
    }
}
