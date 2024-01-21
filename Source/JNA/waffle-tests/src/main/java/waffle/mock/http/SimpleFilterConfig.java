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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

/**
 * The Class SimpleFilterConfig.
 */
public class SimpleFilterConfig implements FilterConfig {

    /** The filter name. */
    private String filterName = "Simple Filter";

    /** The parameters. */
    private final Map<String, String> parameters = new TreeMap<>();

    @Override
    public String getFilterName() {
        return this.filterName;
    }

    /**
     * Sets the filter name.
     *
     * @param value
     *            the new filter name
     */
    public void setFilterName(final String value) {
        this.filterName = value;
    }

    @Override
    public String getInitParameter(final String s) {
        return this.parameters.get(s);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        final List<String> keys = new ArrayList<>();
        keys.addAll(this.parameters.keySet());
        return Collections.enumeration(keys);
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    /**
     * Sets the parameter.
     *
     * @param parameterName
     *            the parameter name
     * @param parameterValue
     *            the parameter value
     */
    public void setParameter(final String parameterName, final String parameterValue) {
        this.parameters.put(parameterName, parameterValue);
    }
}
