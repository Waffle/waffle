/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock.http;

import com.google.errorprone.annotations.InlineMe;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

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

    /**
     * Simply remove this if it is ever actually removed from servlet-api
     *
     * @deprecated Remove this once servlet does.
     */
    @Deprecated
    @InlineMe(replacement = "null")
    @Override
    public final HttpSessionContext getSessionContext() {
        return null;
    }

    /**
     * Simply remove this if it is ever actually removed from servlet-api
     *
     * @deprecated Remove this once servlet does.
     */
    @Deprecated
    @InlineMe(replacement = "null")
    @Override
    public final Object getValue(final String string) {
        return null;
    }

    /**
     * Simply remove this if it is ever actually removed from servlet-api
     *
     * @deprecated Remove this once servlet does.
     */
    @Deprecated
    @InlineMe(replacement = "new String[0]")
    @Override
    public final String[] getValueNames() {
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
    public void putValue(final String string, final Object object) {
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
    public void removeValue(final String string) {
        // Do Nothing
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
