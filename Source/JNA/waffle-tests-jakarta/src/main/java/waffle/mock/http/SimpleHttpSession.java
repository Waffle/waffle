/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package waffle.mock.http;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
