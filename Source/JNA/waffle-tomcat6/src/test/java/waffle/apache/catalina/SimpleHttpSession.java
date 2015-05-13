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

    private Map<String, Object> attributes;

    @Override
    public Object getAttribute(final String attributeName) {
        return this.attributes.get(attributeName);
    }

    @Override
    public String getId() {
        return "WaffleId";
    }

    @Override
    public void removeAttribute(final String attributeName) {
        this.attributes.remove(attributeName);
    }

    @Override
    public void setAttribute(final String attributeName, final Object attributeValue) {
        this.attributes.put(attributeName, attributeValue);
    }

    public void setAttributes(final Map<String, Object> value) {
        this.attributes = value;
    }

}
