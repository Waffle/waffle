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
    public void addApplicationListener(String arg0) {
        // Not Implemented
    }

    @Override
    public void addApplicationParameter(ApplicationParameter arg0) {
        // Not Implemented
    }

    @Override
    public void addConstraint(SecurityConstraint arg0) {
        // Not Implemented
    }

    @Override
    public void addErrorPage(ErrorPage arg0) {
        // Not Implemented
    }

    @Override
    public void addFilterDef(FilterDef arg0) {
        // Not Implemented
    }

    @Override
    public void addFilterMap(FilterMap arg0) {
        // Not Implemented
    }

    @Override
    public void addInstanceListener(String arg0) {
        // Not Implemented
    }

    @Override
    public void addLocaleEncodingMappingParameter(String arg0, String arg1) {
        // Not Implemented
    }

    @Override
    public void addMimeMapping(String arg0, String arg1) {
        // Not Implemented
    }

    @Override
    public void addParameter(String arg0, String arg1) {
        // Not Implemented
    }

    @Override
    public void addRoleMapping(String arg0, String arg1) {
        // Not Implemented
    }

    @Override
    public void addSecurityRole(String arg0) {
        // Not Implemented
    }

    @Override
    public void addServletMapping(String arg0, String arg1) {
        // Not Implemented
    }

    @Override
    public void addWatchedResource(String arg0) {
        // Not Implemented
    }

    @Override
    public void addWelcomeFile(String arg0) {
        // Not Implemented
    }

    @Override
    public void addWrapperLifecycle(String arg0) {
        // Not Implemented
    }

    @Override
    public void addWrapperListener(String arg0) {
        // Not Implemented
    }

    @Override
    public Wrapper createWrapper() {
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
    public ErrorPage findErrorPage(int arg0) {
        // Not Implemented
        return null;
    }

    @Override
    public ErrorPage findErrorPage(String arg0) {
        // Not Implemented
        return null;
    }

    @Override
    public ErrorPage[] findErrorPages() {
        // Not Implemented
        return new ErrorPage[0];
    }

    @Override
    public FilterDef findFilterDef(String arg0) {
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
    public String findMimeMapping(String arg0) {
        // Not Implemented
        return null;
    }

    @Override
    public String[] findMimeMappings() {
        // Not Implemented
        return new String[0];
    }

    @Override
    public String findParameter(String arg0) {
        // Not Implemented
        return null;
    }

    @Override
    public String[] findParameters() {
        // Not Implemented
        return new String[0];
    }

    @Override
    public String findRoleMapping(String arg0) {
        // Not Implemented
        return null;
    }

    @Override
    public boolean findSecurityRole(String arg0) {
        // Not Implemented
        return false;
    }

    @Override
    public String[] findSecurityRoles() {
        // Not Implemented
        return new String[0];
    }

    @Override
    public String findServletMapping(String arg0) {
        // Not Implemented
        return null;
    }

    @Override
    public String[] findServletMappings() {
        // Not Implemented
        return new String[0];
    }

    @Override
    public String findStatusPage(int arg0) {
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
    public boolean findWelcomeFile(String arg0) {
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
    public void removeApplicationListener(String arg0) {
        // Not Implemented
    }

    @Override
    public void removeApplicationParameter(String arg0) {
        // Not Implemented
    }

    @Override
    public void removeConstraint(SecurityConstraint arg0) {
        // Not Implemented
    }

    @Override
    public void removeErrorPage(ErrorPage arg0) {
        // Not Implemented
    }

    @Override
    public void removeFilterDef(FilterDef arg0) {
        // Not Implemented
    }

    @Override
    public void removeFilterMap(FilterMap arg0) {
        // Not Implemented
    }

    @Override
    public void removeInstanceListener(String arg0) {
        // Not Implemented
    }

    @Override
    public void removeMimeMapping(String arg0) {
        // Not Implemented
    }

    @Override
    public void removeParameter(String arg0) {
        // Not Implemented
    }

    @Override
    public void removeRoleMapping(String arg0) {
        // Not Implemented
    }

    @Override
    public void removeSecurityRole(String arg0) {
        // Not Implemented
    }

    @Override
    public void removeServletMapping(String arg0) {
        // Not Implemented
    }

    @Override
    public void removeWatchedResource(String arg0) {
        // Not Implemented
    }

    @Override
    public void removeWelcomeFile(String arg0) {
        // Not Implemented
    }

    @Override
    public void removeWrapperLifecycle(String arg0) {
        // Not Implemented
    }

    @Override
    public void removeWrapperListener(String arg0) {
        // Not Implemented
    }

    @Override
    public void setAltDDName(String arg0) {
        // Not Implemented
    }

    @Override
    public void setApplicationEventListeners(Object[] arg0) {
        // Not Implemented
    }

    @Override
    public void setApplicationLifecycleListeners(Object[] arg0) {
        // Not Implemented
    }

    @Override
    public void setCharsetMapper(CharsetMapper arg0) {
        // Not Implemented
    }

    @Override
    public void setConfigured(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setCookies(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setCrossContext(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setDisplayName(String arg0) {
        // Not Implemented
    }

    @Override
    public void setDistributable(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setDocBase(String arg0) {
        // Not Implemented
    }

    @Override
    public void setIgnoreAnnotations(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setLoginConfig(LoginConfig arg0) {
        // Not Implemented
    }

    @Override
    public void setNamingResources(NamingResources arg0) {
        // Not Implemented
    }

    @Override
    public void setOverride(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void setPrivileged(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setPublicId(String arg0) {
        // Not Implemented
    }

    @Override
    public void setReloadable(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setSessionTimeout(int arg0) {
        // Not Implemented
    }

    @Override
    public void setSwallowOutput(boolean arg0) {
        // Not Implemented
    }

    @Deprecated
    @Override
    public void setTldNamespaceAware(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setTldValidation(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setUseHttpOnly(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setWrapperClass(String arg0) {
        // Not Implemented
    }

    @Override
    public void setXmlNamespaceAware(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setXmlValidation(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void addChild(Container arg0) {
        // Not Implemented
    }

    @Override
    public void addContainerListener(ContainerListener arg0) {
        // Not Implemented
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener arg0) {
        // Not Implemented
    }

    @Override
    public void backgroundProcess() {
        // Not Implemented
    }

    @Override
    public Container findChild(String arg0) {
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

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ObjectName getObjectName() {
        // Not Implemented
        return null;
    }

    @Override
    public Container getParent() {
        return this.parent;
    }

    @Override
    public ClassLoader getParentClassLoader() {
        // Not Implemented
        return null;
    }

    @Override
    public Pipeline getPipeline() {
        return this.pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

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
    public void invoke(Request arg0, Response arg1) throws IOException, ServletException {
        // Not Implemented
    }

    @Override
    public void removeChild(Container arg0) {
        // Not Implemented
    }

    @Override
    public void removeContainerListener(ContainerListener arg0) {
        // Not Implemented
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener arg0) {
        // Not Implemented
    }

    @Override
    public void setBackgroundProcessorDelay(int arg0) {
        // Not Implemented
    }

    @Override
    public void setCluster(Cluster arg0) {
        // Not Implemented
    }

    @Override
    public void setLoader(Loader arg0) {
        // Not Implemented
    }

    @Override
    public void setManager(Manager arg0) {
        // Not Implemented
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setParent(Container container) {
        this.parent = container;
    }

    @Override
    public void setParentClassLoader(ClassLoader arg0) {
        // Not Implemented
    }

    @Override
    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    @Override
    public void setResources(DirContext arg0) {
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
    public void setSessionCookieDomain(String arg0) {
        // Not Implemented
    }

    @Override
    public void setSessionCookieName(String arg0) {
        // Not Implemented
    }

    @Override
    public void setSessionCookiePath(String arg0) {
        // Not Implemented
    }

    @Override
    public AccessLog getAccessLog() {
        // Not Implemented
        return null;
    }

    @Override
    public void logAccess(Request arg0, Response arg1, long arg2, boolean arg3) {
        // Not Implemented
    }

    @Override
    public void fireContainerEvent(String arg0, Object arg1) {
        // Not Implemented
    }

    @Override
    public void addLifecycleListener(LifecycleListener arg0) {
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
    public void removeLifecycleListener(LifecycleListener arg0) {
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
    public void addFilterMapBefore(FilterMap arg0) {
        // Not Implemented
    }

    @Override
    public void addResourceJarUrl(URL arg0) {
        // Not Implemented
    }

    @Override
    public void addServletContainerInitializer(ServletContainerInitializer arg0, Set<Class<?>> arg1) {
        // Not Implemented
    }

    @Override
    public void addServletMapping(String arg0, String arg1, boolean arg2) {
        // Not Implemented
    }

    @Override
    public Set<String> addServletSecurity(ApplicationServletRegistration arg0, ServletSecurityElement arg1) {
        // Not Implemented
        return null;
    }

    @Override
    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
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
    public String getRealPath(String arg0) {
        // Not Implemented
        return null;
    }

    @Override
    public boolean isServlet22() {
        // Not Implemented
        return false;
    }

    @Override
    public void setConfigFile(URL arg0) {
        // Not Implemented
    }

    @Override
    public void setEffectiveMajorVersion(int arg0) {
        // Not Implemented
    }

    @Override
    public void setEffectiveMinorVersion(int arg0) {
        // Not Implemented
    }

    @Override
    public void setLogEffectiveWebXml(boolean arg0) {
        // Not Implemented
    }

    @Override
    public int getStartStopThreads() {
        // Not Implemented
        return 0;
    }

    @Override
    public void setStartStopThreads(int arg0) {
        // Not Implemented
    }

    @Override
    public boolean fireRequestDestroyEvent(ServletRequest arg0) {
        // Not Implemented
        return false;
    }

    @Override
    public boolean fireRequestInitEvent(ServletRequest arg0) {
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
    public String getCharset(Locale arg0) {
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
    public boolean isResourceOnlyServlet(String arg0) {
        // Not Implemented
        return false;
    }

    @Override
    public void setAllowCasualMultipartParsing(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setFireRequestListenersOnForwards(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setPreemptiveAuthentication(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setResourceOnlyServlets(String arg0) {
        // Not Implemented
    }

    @Override
    public void setSendRedirectBody(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setSessionCookiePathUsesTrailingSlash(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setSwallowAbortedUploads(boolean arg0) {
        // Not Implemented
    }

    @Override
    public void setWebappVersion(String arg0) {
        // Not Implemented
    }

    @Override
    public JarScanner getJarScanner() {
        // Not Implemented
        return null;
    }

    @Override
    public void setJarScanner(JarScanner arg0) {
        // Not Implemented
    }

    @Override
    public void addPostConstructMethod(String arg0, String arg1) {
        // Not Implemented
    }

    @Override
    public void addPreDestroyMethod(String arg0, String arg1) {
        // Not Implemented
    }

    @Override
    public String findPostConstructMethod(String arg0) {
        // Not Implemented
        return null;
    }

    @Override
    public Map<String, String> findPostConstructMethods() {
        // Not Implemented
        return null;
    }

    @Override
    public String findPreDestroyMethod(String arg0) {
        // Not Implemented
        return null;
    }

    @Override
    public Map<String, String> findPreDestroyMethods() {
        // Not Implemented
        return null;
    }

    @Override
    public void removePostConstructMethod(String arg0) {
        // Not Implemented
    }

    @Override
    public void removePreDestroyMethod(String arg0) {
        // Not Implemented
    }

    @Deprecated
    @Override
    public void addApplicationListener(ApplicationListener arg0) {
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
    public void setContainerSciFilter(String arg0) {
        // Not Implemented
    }

    @Override
    public void setInstanceManager(InstanceManager arg0) {
        // Not Implemented
    }

    @Override
    public void setXmlBlockExternal(boolean arg0) {
        // Not Implemented
    }

}
