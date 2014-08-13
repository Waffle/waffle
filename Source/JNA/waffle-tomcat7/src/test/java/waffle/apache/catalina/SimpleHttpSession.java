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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * Simple Http Session
 * 
 * @author dblock[at]dblock[dot]org
 */
@SuppressWarnings("deprecation")
public class SimpleHttpSession implements HttpSession {

    private Map<String, Object> attributes = new HashMap<String, Object>();

    @Override
    public Object getAttribute(String attributeName) {
        return this.attributes.get(attributeName);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        // Not Implemented
        return null;
    }

    @Override
    public long getCreationTime() {
        // Not Implemented
        return 0;
    }

    @Override
    public String getId() {
        // Not Implemented
        return null;
    }

    @Override
    public long getLastAccessedTime() {
        // Not Implemented
        return 0;
    }

    @Override
    public int getMaxInactiveInterval() {
        // Not Implemented
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        // Not Implemented
        return null;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        // Not Implemented
        return null;
    }

    @Override
    public Object getValue(String arg0) {
        // Not Implemented
        return null;
    }

    @Override
    public String[] getValueNames() {
        // Not Implemented
        return new String[0];
    }

    @Override
    public void invalidate() {
        // Not Implemented
    }

    @Override
    public boolean isNew() {
        // Not Implemented
        return false;
    }

    @Override
    public void putValue(String arg0, Object arg1) {
        // Not Implemented
    }

    @Override
    public void removeAttribute(String attributeName) {
        this.attributes.remove(attributeName);
    }

    @Override
    public void removeValue(String arg0) {
        // Not Implemented
    }

    @Override
    public void setAttribute(String attributeName, Object attributeValue) {
        this.attributes.put(attributeName, attributeValue);
    }

    @Override
    public void setMaxInactiveInterval(int arg0) {
        // Not Implemented
    }
}
