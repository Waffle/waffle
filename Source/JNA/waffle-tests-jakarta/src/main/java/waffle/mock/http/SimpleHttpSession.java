/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock.http;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple Http Session.
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

    @Override
    public void invalidate() {
        // Do Nothing
    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    public void removeAttribute(final String attributeName) {
        this.attributes.remove(attributeName);
    }

    @Override
    public void setAttribute(final String attributeName, final Object attributeValue) {
        this.attributes.put(attributeName, attributeValue);
    }

    @Override
    public void setMaxInactiveInterval(final int primativeInt) {
        // Do Nothing
    }
}
