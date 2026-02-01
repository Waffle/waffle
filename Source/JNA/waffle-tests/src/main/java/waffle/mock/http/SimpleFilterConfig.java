/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
