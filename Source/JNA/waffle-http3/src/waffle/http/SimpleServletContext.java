/*******************************************************************************
 * Waffle (https://github.com/dblock/waffle)
 * 
 * Copyright (c) 2010 Application Security, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 *******************************************************************************/
package waffle.http;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleServletContext implements ServletContext {

	@Override
	public Object getAttribute(String arg0) {
		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return null;
	}

	@Override
	public ServletContext getContext(String arg0) {
		return null;
	}

	@Override
	public String getContextPath() {
		return null;
	}

	@Override
	public String getInitParameter(String arg0) {
		return null;
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return null;
	}

	@Override
	public int getMajorVersion() {
		return 0;
	}

	@Override
	public String getMimeType(String arg0) {
		return null;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String name) {
		return null;
	}

	@Override
	public String getRealPath(String arg0) {
		return null;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String url) {
		return new SimpleRequestDispatcher(url);
	}

	@Override
	public URL getResource(String arg0) throws MalformedURLException {
		return null;
	}

	@Override
	public InputStream getResourceAsStream(String arg0) {
		return null;
	}

	@Override
	public Set<String> getResourcePaths(String arg0) {
		return null;
	}

	@Override
	public String getServerInfo() {
		return null;
	}

	@Override
	public Servlet getServlet(String arg0) throws ServletException {
		return null;
	}

	@Override
	public String getServletContextName() {
		return null;
	}

	@Override
	public Enumeration<String> getServletNames() {
		return null;
	}

	@Override
	public Enumeration<Servlet> getServlets() {
		return null;
	}

	@Override
	public void log(String arg0) {

	}

	@Override
	public void log(Exception arg0, String arg1) {

	}

	@Override
	public void log(String arg0, Throwable arg1) {

	}

	@Override
	public void removeAttribute(String arg0) {

	}

	@Override
	public void setAttribute(String arg0, Object arg1) {

	}

	@Override
	public Dynamic addFilter(String arg0, String arg1) {
		return null;
	}

	@Override
	public Dynamic addFilter(String arg0, Filter arg1) {
		return null;
	}

	@Override
	public Dynamic addFilter(String arg0, Class<? extends Filter> arg1) {
		return null;
	}

	@Override
	public void addListener(String arg0) {

	}

	@Override
	public <T extends EventListener> void addListener(T arg0) {

	}

	@Override
	public void addListener(Class<? extends EventListener> arg0) {

	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			String arg1) {
		return null;
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Servlet arg1) {
		return null;
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Class<? extends Servlet> arg1) {
		return null;
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> arg0)
			throws ServletException {
		return null;
	}

	@Override
	public <T extends EventListener> T createListener(Class<T> arg0)
			throws ServletException {
		return null;
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> arg0)
			throws ServletException {
		return null;
	}

	@Override
	public void declareRoles(String... arg0) {

	}

	@Override
	public ClassLoader getClassLoader() {
		return null;
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		return null;
	}

	@Override
	public int getEffectiveMajorVersion() {
		return 0;
	}

	@Override
	public int getEffectiveMinorVersion() {
		return 0;
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		return null;
	}

	@Override
	public FilterRegistration getFilterRegistration(String arg0) {
		return null;
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		return null;
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		return null;
	}

	@Override
	public ServletRegistration getServletRegistration(String arg0) {
		return null;
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		return null;
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		return null;
	}

	@Override
	public boolean setInitParameter(String arg0, String arg1) {
		return false;
	}

	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> arg0) {

	}
}
