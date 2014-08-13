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

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.management.ObjectName;
import javax.naming.directory.DirContext;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletSecurityElement;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.apache.catalina.AccessLog;
import org.apache.catalina.Authenticator;
import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Loader;
import org.apache.catalina.Manager;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.ApplicationServletRegistration;
import org.apache.catalina.deploy.ApplicationListener;
import org.apache.catalina.deploy.ApplicationParameter;
import org.apache.catalina.deploy.ErrorPage;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.NamingResources;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.util.CharsetMapper;
import org.apache.juli.logging.Log;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.http.mapper.Mapper;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleContext implements Context {

    private String         path           = "/";
    private String         name           = "SimpleContext";
    private Realm          realm;
    private Container      parent;
    private ServletContext servletContext = new SimpleServletContext();
    private Pipeline       pipeline;
    private Authenticator  authenticator;

    @Override
    public void addApplicationListener(String value) {
        // Not Implemented
    }

    @Override
    public void addApplicationParameter(ApplicationParameter value) {
        // Not Implemented
    }

    @Override
    public void addConstraint(SecurityConstraint value) {
        // Not Implemented
    }

    @Override
    public void addErrorPage(ErrorPage value) {
        // Not Implemented
    }

    @Override
    public void addFilterDef(FilterDef value) {
        // Not Implemented
    }

    @Override
    public void addFilterMap(FilterMap value) {
        // Not Implemented
    }

    @Override
    public void addInstanceListener(String value) {
        // Not Implemented
    }

    @Override
    public void addLocaleEncodingMappingParameter(String locale, String encoding) {
        // Not Implemented
    }

    @Override
    public void addMimeMapping(String value, String arg1) {
        // Not Implemented
    }

    @Override
    public void addParameter(String value, String arg1) {
        // Not Implemented
    }

    @Override
    public void addRoleMapping(String value, String arg1) {
        // Not Implemented
    }

    @Override
    public void addSecurityRole(String value) {
        // Not Implemented
    }

    @Override
    public void addServletMapping(String value, String arg1) {
        // Not Implemented
    }

    @Override
    public void addWatchedResource(String value) {
        // Not Implemented
    }

    @Override
    public void addWelcomeFile(String value) {
        // Not Implemented
    }

    @Override
    public void addWrapperLifecycle(String value) {
        // Not Implemented
    }

    @Override
    public void addWrapperListener(String value) {
        // Not Implemented
    }

    @Override
    public Wrapper createWrapper() {
        // Not Implemented
        return null;
    }

    @Override
    public String[] findApplicationListeners() {
        // Not Implemented
        return new String[0];
    }

    @Override
    public ApplicationParameter[] findApplicationParameters() {
        // Not Implemented
        return new ApplicationParameter[0];
    }

    @Override
    public SecurityConstraint[] findConstraints() {
        // Not Implemented
        return new SecurityConstraint[0];
    }

    @Override
    public ErrorPage findErrorPage(int value) {
        // Not Implemented
        return null;
    }

    @Override
    public ErrorPage findErrorPage(String value) {
        // Not Implemented
        return null;
    }

    @Override
    public ErrorPage[] findErrorPages() {
        // Not Implemented
        return new ErrorPage[0];
    }

    @Override
    public FilterDef findFilterDef(String value) {
        // Not Implemented
        return null;
    }

    @Override
    public FilterDef[] findFilterDefs() {
        // Not Implemented
        return new FilterDef[0];
    }

    @Override
    public FilterMap[] findFilterMaps() {
        // Not Implemented
        return new FilterMap[0];
    }

    @Override
    public String[] findInstanceListeners() {
        // Not Implemented
        return new String[0];
    }

    @Override
    public String findMimeMapping(String value) {
        // Not Implemented
        return null;
    }

    @Override
    public String[] findMimeMappings() {
        // Not Implemented
        return new String[0];
    }

    @Override
    public String findParameter(String value) {
        // Not Implemented
        return null;
    }

    @Override
    public String[] findParameters() {
        // Not Implemented
        return new String[0];
    }

    @Override
    public String findRoleMapping(String value) {
        // Not Implemented
        return null;
    }

    @Override
    public boolean findSecurityRole(String value) {
        // Not Implemented
        return false;
    }

    @Override
    public String[] findSecurityRoles() {
        // Not Implemented
        return new String[0];
    }

    @Override
    public String findServletMapping(String value) {
        // Not Implemented
        return null;
    }

    @Override
    public String[] findServletMappings() {
        // Not Implemented
        return new String[0];
    }

    @Override
    public String findStatusPage(int value) {
        // Not Implemented
        return null;
    }

    @Override
    public int[] findStatusPages() {
        // Not Implemented
        return new int[0];
    }

    @Override
    public String[] findWatchedResources() {
        // Not Implemented
        return new String[0];
    }

    @Override
    public boolean findWelcomeFile(String value) {
        // Not Implemented
        return false;
    }

    @Override
    public String[] findWelcomeFiles() {
        // Not Implemented
        return new String[0];
    }

    @Override
    public String[] findWrapperLifecycles() {
        // Not Implemented
        return new String[0];
    }

    @Override
    public String[] findWrapperListeners() {
        // Not Implemented
        return new String[0];
    }

    @Override
    public String getAltDDName() {
        // Not Implemented
        return null;
    }

    @Override
    public Object[] getApplicationEventListeners() {
        // Not Implemented
        return new Object[0];
    }

    @Override
    public Object[] getApplicationLifecycleListeners() {
        // Not Implemented
        return new Object[0];
    }

    @Deprecated
    @Override
    public boolean getAvailable() {
        // Not Implemented
        return false;
    }

    @Deprecated
    @Override
    public CharsetMapper getCharsetMapper() {
        // Not Implemented
        return null;
    }

    @Override
    public URL getConfigFile() {
        // Not Implemented
        return null;
    }

    @Override
    public boolean getConfigured() {
        // Not Implemented
        return false;
    }

    @Override
    public boolean getCookies() {
        // Not Implemented
        return false;
    }

    @Override
    public boolean getCrossContext() {
        // Not Implemented
        return false;
    }

    @Override
    public String getDisplayName() {
        // Not Implemented
        return null;
    }

    @Override
    public boolean getDistributable() {
        // Not Implemented
        return false;
    }

    @Override
    public String getDocBase() {
        // Not Implemented
        return null;
    }

    @Override
    public String getEncodedPath() {
        // Not Implemented
        return null;
    }

    @Override
    public boolean getIgnoreAnnotations() {
        // Not Implemented
        return false;
    }

    @Override
    public LoginConfig getLoginConfig() {
        // Not Implemented
        return null;
    }

    @Override
    public Mapper getMapper() {
        // Not Implemented
        return null;
    }

    @Override
    public NamingResources getNamingResources() {
        // Not Implemented
        return null;
    }

    @Override
    public boolean getOverride() {
        // Not Implemented
        return false;
    }

    /**
     * Get Path Used By Waffle.
     */
    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public boolean getPrivileged() {
        // Not Implemented
        return false;
    }

    @Override
    public String getPublicId() {
        // Not Implemented
        return null;
    }

    @Override
    public boolean getReloadable() {
        // Not Implemented
        return false;
    }

    /**
     * Get Servlet Context Used By Waffle.
     */
    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public int getSessionTimeout() {
        // Not Implemented
        return 0;
    }

    @Override
    public boolean getSwallowOutput() {
        // Not Implemented
        return false;
    }

    @Deprecated
    @Override
    public boolean getTldNamespaceAware() {
        // Not Implemented
        return false;
    }

    @Override
    public boolean getTldValidation() {
        // Not Implemented
        return false;
    }

    @Override
    public boolean getUseHttpOnly() {
        // Not Implemented
        return false;
    }

    @Override
    public String getWrapperClass() {
        // Not Implemented
        return null;
    }

    @Override
    public boolean getXmlNamespaceAware() {
        // Not Implemented
        return false;
    }

    @Override
    public boolean getXmlValidation() {
        // Not Implemented
        return false;
    }

    @Override
    public void reload() {
        // Not Implemented
    }

    @Override
    public void removeApplicationListener(String value) {
        // Not Implemented
    }

    @Override
    public void removeApplicationParameter(String value) {
        // Not Implemented
    }

    @Override
    public void removeConstraint(SecurityConstraint value) {
        // Not Implemented
    }

    @Override
    public void removeErrorPage(ErrorPage value) {
        // Not Implemented
    }

    @Override
    public void removeFilterDef(FilterDef value) {
        // Not Implemented
    }

    @Override
    public void removeFilterMap(FilterMap value) {
        // Not Implemented
    }

    @Override
    public void removeInstanceListener(String value) {
        // Not Implemented
    }

    @Override
    public void removeMimeMapping(String value) {
        // Not Implemented
    }

    @Override
    public void removeParameter(String value) {
        // Not Implemented
    }

    @Override
    public void removeRoleMapping(String value) {
        // Not Implemented
    }

    @Override
    public void removeSecurityRole(String value) {
        // Not Implemented
    }

    @Override
    public void removeServletMapping(String value) {
        // Not Implemented
    }

    @Override
    public void removeWatchedResource(String value) {
        // Not Implemented
    }

    @Override
    public void removeWelcomeFile(String value) {
        // Not Implemented
    }

    @Override
    public void removeWrapperLifecycle(String value) {
        // Not Implemented
    }

    @Override
    public void removeWrapperListener(String value) {
        // Not Implemented
    }

    @Override
    public void setAltDDName(String value) {
        // Not Implemented
    }

    @Override
    public void setApplicationEventListeners(Object[] value) {
        // Not Implemented
    }

    @Override
    public void setApplicationLifecycleListeners(Object[] value) {
        // Not Implemented
    }

    @Deprecated
    @Override
    public void setCharsetMapper(CharsetMapper value) {
        // Not Implemented
    }

    @Override
    public void setConfigured(boolean value) {
        // Not Implemented
    }

    @Override
    public void setCookies(boolean value) {
        // Not Implemented
    }

    @Override
    public void setCrossContext(boolean value) {
        // Not Implemented
    }

    @Override
    public void setDisplayName(String value) {
        // Not Implemented
    }

    @Override
    public void setDistributable(boolean value) {
        // Not Implemented
    }

    @Override
    public void setDocBase(String value) {
        // Not Implemented
    }

    @Override
    public void setIgnoreAnnotations(boolean value) {
        // Not Implemented
    }

    @Override
    public void setLoginConfig(LoginConfig value) {
        // Not Implemented
    }

    @Override
    public void setNamingResources(NamingResources value) {
        // Not Implemented
    }

    @Override
    public void setOverride(boolean value) {
        // Not Implemented
    }

    /**
     * Set Path Used By Waffle
     */
    @Override
    public void setPath(String value) {
        this.path = value;
    }

    @Override
    public void setPrivileged(boolean value) {
        // Not Implemented
    }

    @Override
    public void setPublicId(String value) {
        // Not Implemented
    }

    @Override
    public void setReloadable(boolean value) {
        // Not Implemented
    }

    @Override
    public void setSessionTimeout(int value) {
        // Not Implemented
    }

    @Override
    public void setSwallowOutput(boolean value) {
        // Not Implemented
    }

    @Deprecated
    @Override
    public void setTldNamespaceAware(boolean value) {
        // Not Implemented
    }

    @Override
    public void setTldValidation(boolean value) {
        // Not Implemented
    }

    @Override
    public void setUseHttpOnly(boolean value) {
        // Not Implemented
    }

    @Override
    public void setWrapperClass(String value) {
        // Not Implemented
    }

    @Override
    public void setXmlNamespaceAware(boolean value) {
        // Not Implemented
    }

    @Override
    public void setXmlValidation(boolean value) {
        // Not Implemented
    }

    @Override
    public void addChild(Container value) {
        // Not Implemented
    }

    @Override
    public void addContainerListener(ContainerListener value) {
        // Not Implemented
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener value) {
        // Not Implemented
    }

    @Override
    public void backgroundProcess() {
        // Not Implemented
    }

    @Override
    public Container findChild(String value) {
        // Not Implemented
        return null;
    }

    @Override
    public Container[] findChildren() {
        // Not Implemented
        return new Container[0];
    }

    @Override
    public ContainerListener[] findContainerListeners() {
        // Not Implemented
        return new ContainerListener[0];
    }

    @Override
    public int getBackgroundProcessorDelay() {
        // Not Implemented
        return 0;
    }

    @Override
    public Cluster getCluster() {
        // Not Implemented
        return null;
    }

    @Override
    public String getInfo() {
        // Not Implemented
        return null;
    }

    @Override
    public Loader getLoader() {
        // Not Implemented
        return null;
    }

    @Override
    public Log getLogger() {
        // Not Implemented
        return null;
    }

    @Override
    public Manager getManager() {
        // Not Implemented
        return null;
    }

    @Deprecated
    @Override
    public Object getMappingObject() {
        // Not Implemented
        return null;
    }

    /**
     * Get Name Used By Waffle.
     */
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ObjectName getObjectName() {
        // Not Implemented
        return null;
    }

    /**
     * Get Parent Used By Waffle.
     */
    @Override
    public Container getParent() {
        return this.parent;
    }

    @Override
    public ClassLoader getParentClassLoader() {
        // Not Implemented
        return null;
    }

    /**
     * Get Pipeline Used By Waffle.
     */
    @Override
    public Pipeline getPipeline() {
        return this.pipeline;
    }

    /**
     * Set Pipeline Used By Waffle.
     * 
     * @param value
     */
    public void setPipeline(Pipeline value) {
        this.pipeline = value;
    }

    /**
     * Get Realm Used By Waffle.
     */
    @Override
    public Realm getRealm() {
        return this.realm;
    }

    @Override
    public DirContext getResources() {
        // Not Implemented
        return null;
    }

    @Deprecated
    @Override
    public void invoke(Request value, Response arg1) throws IOException, ServletException {
        // Not Implemented
    }

    @Override
    public void removeChild(Container value) {
        // Not Implemented
    }

    @Override
    public void removeContainerListener(ContainerListener value) {
        // Not Implemented
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener value) {
        // Not Implemented
    }

    @Override
    public void setBackgroundProcessorDelay(int value) {
        // Not Implemented
    }

    @Override
    public void setCluster(Cluster value) {
        // Not Implemented
    }

    @Override
    public void setLoader(Loader value) {
        // Not Implemented
    }

    @Override
    public void setManager(Manager value) {
        // Not Implemented
    }

    /**
     * Set Name Used By Waffle.
     */
    @Override
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Set Parent Used By Waffle.
     */
    @Override
    public void setParent(Container container) {
        this.parent = container;
    }

    @Override
    public void setParentClassLoader(ClassLoader value) {
        // Not Implemented
    }

    /**
     * Set Realm Used By Waffle.
     */
    @Override
    public void setRealm(Realm value) {
        this.realm = value;
    }

    @Override
    public void setResources(DirContext value) {
        // Not Implemented
    }

    @Override
    public String getSessionCookieDomain() {
        // Not Implemented
        return null;
    }

    @Override
    public String getSessionCookieName() {
        // Not Implemented
        return null;
    }

    @Override
    public String getSessionCookiePath() {
        // Not Implemented
        return null;
    }

    @Override
    public void setSessionCookieDomain(String value) {
        // Not Implemented
    }

    @Override
    public void setSessionCookieName(String value) {
        // Not Implemented
    }

    @Override
    public void setSessionCookiePath(String value) {
        // Not Implemented
    }

    @Override
    public AccessLog getAccessLog() {
        // Not Implemented
        return null;
    }

    @Override
    public void logAccess(Request value, Response arg1, long arg2, boolean arg3) {
        // Not Implemented
    }

    @Override
    public void fireContainerEvent(String value, Object arg1) {
        // Not Implemented
    }

    @Override
    public void addLifecycleListener(LifecycleListener value) {
        // Not Implemented
    }

    @Override
    public void destroy() throws LifecycleException {
        // Not Implemented
    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        // Not Implemented
        return new LifecycleListener[0];
    }

    @Override
    public LifecycleState getState() {
        // Not Implemented
        return null;
    }

    @Override
    public String getStateName() {
        // Not Implemented
        return null;
    }

    @Override
    public void init() throws LifecycleException {
        // Not Implemented
    }

    @Override
    public void removeLifecycleListener(LifecycleListener value) {
        // Not Implemented
    }

    @Override
    public void start() throws LifecycleException {
        // Not Implemented
    }

    @Override
    public void stop() throws LifecycleException {
        // Not Implemented
    }

    @Override
    public void addFilterMapBefore(FilterMap value) {
        // Not Implemented
    }

    @Override
    public void addResourceJarUrl(URL value) {
        // Not Implemented
    }

    @Override
    public void addServletContainerInitializer(ServletContainerInitializer value, Set<Class<?>> arg1) {
        // Not Implemented
    }

    @Override
    public void addServletMapping(String value, String arg1, boolean arg2) {
        // Not Implemented
    }

    @Override
    public Set<String> addServletSecurity(ApplicationServletRegistration value, ServletSecurityElement arg1) {
        // Not Implemented
        return null;
    }

    /**
     * Get Authenticator Used By Waffle.
     */
    @Override
    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    /**
     * Set Authenticator Used By Waffle.
     * 
     * @param value
     */
    public void setAuthenticator(Authenticator value) {
        this.authenticator = value;
    }

    @Override
    public int getEffectiveMajorVersion() {
        // Not Implemented
        return 0;
    }

    @Override
    public int getEffectiveMinorVersion() {
        // Not Implemented
        return 0;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        // Not Implemented
        return null;
    }

    @Override
    public boolean getLogEffectiveWebXml() {
        // Not Implemented
        return false;
    }

    @Override
    public boolean getPaused() {
        // Not Implemented
        return false;
    }

    @Override
    public String getRealPath(String value) {
        // Not Implemented
        return null;
    }

    @Override
    public boolean isServlet22() {
        // Not Implemented
        return false;
    }

    @Override
    public void setConfigFile(URL value) {
        // Not Implemented
    }

    @Override
    public void setEffectiveMajorVersion(int value) {
        // Not Implemented
    }

    @Override
    public void setEffectiveMinorVersion(int value) {
        // Not Implemented
    }

    @Override
    public void setLogEffectiveWebXml(boolean value) {
        // Not Implemented
    }

    @Override
    public int getStartStopThreads() {
        // Not Implemented
        return 0;
    }

    @Override
    public void setStartStopThreads(int value) {
        // Not Implemented
    }

    @Override
    public boolean fireRequestDestroyEvent(ServletRequest value) {
        // Not Implemented
        return false;
    }

    @Override
    public boolean fireRequestInitEvent(ServletRequest value) {
        // Not Implemented
        return false;
    }

    @Override
    public boolean getAllowCasualMultipartParsing() {
        // Not Implemented
        return false;
    }

    @Override
    public String getBaseName() {
        // Not Implemented
        return null;
    }

    @Override
    public String getCharset(Locale value) {
        // Not Implemented
        return null;
    }

    @Override
    public boolean getFireRequestListenersOnForwards() {
        // Not Implemented
        return false;
    }

    @Override
    public boolean getPreemptiveAuthentication() {
        // Not Implemented
        return false;
    }

    @Override
    public String getResourceOnlyServlets() {
        // Not Implemented
        return null;
    }

    @Override
    public boolean getSendRedirectBody() {
        // Not Implemented
        return false;
    }

    @Override
    public boolean getSessionCookiePathUsesTrailingSlash() {
        // Not Implemented
        return false;
    }

    @Override
    public boolean getSwallowAbortedUploads() {
        // Not Implemented
        return false;
    }

    @Override
    public String getWebappVersion() {
        // Not Implemented
        return null;
    }

    @Override
    public boolean isResourceOnlyServlet(String value) {
        // Not Implemented
        return false;
    }

    @Override
    public void setAllowCasualMultipartParsing(boolean value) {
        // Not Implemented
    }

    @Override
    public void setFireRequestListenersOnForwards(boolean value) {
        // Not Implemented
    }

    @Override
    public void setPreemptiveAuthentication(boolean value) {
        // Not Implemented
    }

    @Override
    public void setResourceOnlyServlets(String value) {
        // Not Implemented
    }

    @Override
    public void setSendRedirectBody(boolean value) {
        // Not Implemented
    }

    @Override
    public void setSessionCookiePathUsesTrailingSlash(boolean value) {
        // Not Implemented
    }

    @Override
    public void setSwallowAbortedUploads(boolean value) {
        // Not Implemented
    }

    @Override
    public void setWebappVersion(String value) {
        // Not Implemented
    }

    @Override
    public JarScanner getJarScanner() {
        // Not Implemented
        return null;
    }

    @Override
    public void setJarScanner(JarScanner value) {
        // Not Implemented
    }

    @Override
    public void addPostConstructMethod(String value, String arg1) {
        // Not Implemented
    }

    @Override
    public void addPreDestroyMethod(String value, String arg1) {
        // Not Implemented
    }

    @Override
    public String findPostConstructMethod(String value) {
        // Not Implemented
        return null;
    }

    @Override
    public Map<String, String> findPostConstructMethods() {
        // Not Implemented
        return null;
    }

    @Override
    public String findPreDestroyMethod(String value) {
        // Not Implemented
        return null;
    }

    @Override
    public Map<String, String> findPreDestroyMethods() {
        // Not Implemented
        return null;
    }

    @Override
    public void removePostConstructMethod(String value) {
        // Not Implemented
    }

    @Override
    public void removePreDestroyMethod(String value) {
        // Not Implemented
    }

    @Deprecated
    @Override
    public void addApplicationListener(ApplicationListener value) {
        // Not Implemented
    }

    @Override
    public String getContainerSciFilter() {
        // Not Implemented
        return null;
    }

    @Override
    public InstanceManager getInstanceManager() {
        // Not Implemented
        return null;
    }

    @Override
    public boolean getXmlBlockExternal() {
        // Not Implemented
        return false;
    }

    @Override
    public void setContainerSciFilter(String value) {
        // Not Implemented
    }

    @Override
    public void setInstanceManager(InstanceManager value) {
        // Not Implemented
    }

    @Override
    public void setXmlBlockExternal(boolean value) {
        // Not Implemented
    }

}
