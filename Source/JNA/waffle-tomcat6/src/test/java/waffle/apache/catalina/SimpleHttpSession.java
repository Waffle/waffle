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

import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * Simple HTTP Session.
 * 
 * @author dblock[at]dblock[dot]org
 */
public abstract class SimpleHttpSession implements HttpSession {

    /** The attributes. */
    private Map<String, Object> attributes;

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getAttribute(java.lang.String)
     */
    @Override
    public Object getAttribute(final String attributeName) {
        return this.attributes.get(attributeName);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getId()
     */
    @Override
    public String getId() {
        return "WaffleId";
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
     */
    @Override
    public void removeAttribute(final String attributeName) {
        this.attributes.remove(attributeName);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#setAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    public void setAttribute(final String attributeName, final Object attributeValue) {
        this.attributes.put(attributeName, attributeValue);
    }

    /**
     * Sets the attributes.
     *
     * @param value the value
     */
    public void setAttributes(final Map<String, Object> value) {
        this.attributes = value;
    }

}
