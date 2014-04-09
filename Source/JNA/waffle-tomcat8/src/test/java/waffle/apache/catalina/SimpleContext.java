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
package waffle.apache.catalina;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.management.ObjectName;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration.Dynamic;
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
import org.apache.catalina.ThreadBindingListener;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.juli.logging.Log;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.descriptor.web.ApplicationListener;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleContext implements Context {

	private String _path = "/";
	private String _name = "SimpleContext";
	private Realm _realm = null;
	private Container _parent = null;
	private ServletContext _servletContext = new SimpleServletContext();
	private Pipeline _pipeline = null;
	private Authenticator _authenticator = null;

	@Override
	public void addApplicationParameter(ApplicationParameter arg0) {

	}

	@Override
	public void addConstraint(SecurityConstraint arg0) {

	}

	@Override
	public void addErrorPage(ErrorPage arg0) {

	}

	@Override
	public void addFilterDef(FilterDef arg0) {

	}

	@Override
	public void addFilterMap(FilterMap arg0) {

	}

	@Override
	public void addInstanceListener(String arg0) {

	}

	@Override
	public void addLocaleEncodingMappingParameter(String arg0, String arg1) {

	}

	@Override
	public void addMimeMapping(String arg0, String arg1) {

	}

	@Override
	public void addParameter(String arg0, String arg1) {

	}

	@Override
	public void addRoleMapping(String arg0, String arg1) {

	}

	@Override
	public void addSecurityRole(String arg0) {

	}

	@Override
	public void addServletMapping(String arg0, String arg1) {

	}

	@Override
	public void addWatchedResource(String arg0) {

	}

	@Override
	public void addWelcomeFile(String arg0) {

	}

	@Override
	public void addWrapperLifecycle(String arg0) {

	}

	@Override
	public void addWrapperListener(String arg0) {

	}

	@Override
	public Wrapper createWrapper() {
		return null;
	}

	@Override
	public ApplicationListener[] findApplicationListeners() {
		return new ApplicationListener[0];
	}

	@Override
	public ApplicationParameter[] findApplicationParameters() {
		return new ApplicationParameter[0];
	}

	@Override
	public SecurityConstraint[] findConstraints() {
		return new SecurityConstraint[0];
	}

	@Override
	public ErrorPage findErrorPage(int arg0) {
		return null;
	}

	@Override
	public ErrorPage findErrorPage(String arg0) {
		return null;
	}

	@Override
	public ErrorPage[] findErrorPages() {
		return new ErrorPage[0];
	}

	@Override
	public FilterDef findFilterDef(String arg0) {
		return null;
	}

	@Override
	public FilterDef[] findFilterDefs() {
		return new FilterDef[0];
	}

	@Override
	public FilterMap[] findFilterMaps() {
		return new FilterMap[0];
	}

	@Override
	public String[] findInstanceListeners() {
		return new String[0];
	}

	@Override
	public String findMimeMapping(String arg0) {
		return null;
	}

	@Override
	public String[] findMimeMappings() {
		return new String[0];
	}

	@Override
	public String findParameter(String arg0) {
		return null;
	}

	@Override
	public String[] findParameters() {
		return new String[0];
	}

	@Override
	public String findRoleMapping(String arg0) {
		return null;
	}

	@Override
	public boolean findSecurityRole(String arg0) {
		return false;
	}

	@Override
	public String[] findSecurityRoles() {
		return new String[0];
	}

	@Override
	public String findServletMapping(String arg0) {
		return null;
	}

	@Override
	public String[] findServletMappings() {
		return new String[0];
	}

	@Override
	public String findStatusPage(int arg0) {
		return null;
	}

	@Override
	public int[] findStatusPages() {
		return new int[0];
	}

	@Override
	public String[] findWatchedResources() {
		return new String[0];
	}

	@Override
	public boolean findWelcomeFile(String arg0) {
		return false;
	}

	@Override
	public String[] findWelcomeFiles() {
		return new String[0];
	}

	@Override
	public String[] findWrapperLifecycles() {
		return new String[0];
	}

	@Override
	public String[] findWrapperListeners() {
		return new String[0];
	}

	@Override
	public String getAltDDName() {
		return null;
	}

	@Override
	public Object[] getApplicationEventListeners() {
		return new Object[0];
	}

	@Override
	public Object[] getApplicationLifecycleListeners() {
		return new Object[0];
	}

	@Override
	public URL getConfigFile() {
		return null;
	}

	@Override
	public boolean getConfigured() {
		return false;
	}

	@Override
	public boolean getCookies() {
		return false;
	}

	@Override
	public boolean getCrossContext() {
		return false;
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public boolean getDistributable() {
		return false;
	}

	@Override
	public String getDocBase() {
		return null;
	}

	@Override
	public String getEncodedPath() {
		return null;
	}

	@Override
	public boolean getIgnoreAnnotations() {
		return false;
	}

	@Override
	public LoginConfig getLoginConfig() {
		return null;
	}

	@Override
	public NamingResourcesImpl getNamingResources() {
		return null;
	}

	@Override
	public boolean getOverride() {
		return false;
	}

	@Override
	public String getPath() {
		return _path;
	}

	@Override
	public boolean getPrivileged() {
		return false;
	}

	@Override
	public String getPublicId() {
		return null;
	}

	@Override
	public boolean getReloadable() {
		return false;
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public int getSessionTimeout() {
		return 0;
	}

	@Override
	public boolean getSwallowOutput() {
		return false;
	}

	@Override
	public boolean getTldValidation() {
		return false;
	}

	@Override
	public boolean getUseHttpOnly() {
		return false;
	}

	@Override
	public String getWrapperClass() {
		return null;
	}

	@Override
	public boolean getXmlNamespaceAware() {
		return false;
	}

	@Override
	public boolean getXmlValidation() {
		return false;
	}

	@Override
	public void reload() {

	}

	@Override
	public void removeApplicationListener(String arg0) {

	}

	@Override
	public void removeApplicationParameter(String arg0) {

	}

	@Override
	public void removeConstraint(SecurityConstraint arg0) {

	}

	@Override
	public void removeErrorPage(ErrorPage arg0) {

	}

	@Override
	public void removeFilterDef(FilterDef arg0) {

	}

	@Override
	public void removeFilterMap(FilterMap arg0) {

	}

	@Override
	public void removeInstanceListener(String arg0) {

	}

	@Override
	public void removeMimeMapping(String arg0) {

	}

	@Override
	public void removeParameter(String arg0) {

	}

	@Override
	public void removeRoleMapping(String arg0) {

	}

	@Override
	public void removeSecurityRole(String arg0) {

	}

	@Override
	public void removeServletMapping(String arg0) {

	}

	@Override
	public void removeWatchedResource(String arg0) {

	}

	@Override
	public void removeWelcomeFile(String arg0) {

	}

	@Override
	public void removeWrapperLifecycle(String arg0) {

	}

	@Override
	public void removeWrapperListener(String arg0) {

	}

	@Override
	public void setAltDDName(String arg0) {

	}

	@Override
	public void setApplicationEventListeners(Object[] arg0) {

	}

	@Override
	public void setApplicationLifecycleListeners(Object[] arg0) {

	}

	@Override
	public void setConfigured(boolean arg0) {

	}

	@Override
	public void setCookies(boolean arg0) {

	}

	@Override
	public void setCrossContext(boolean arg0) {

	}

	@Override
	public void setDisplayName(String arg0) {

	}

	@Override
	public void setDistributable(boolean arg0) {

	}

	@Override
	public void setDocBase(String arg0) {

	}

	@Override
	public void setIgnoreAnnotations(boolean arg0) {

	}

	@Override
	public void setLoginConfig(LoginConfig arg0) {

	}

	@Override
	public void setOverride(boolean arg0) {

	}

	@Override
	public void setPath(String path) {
		_path = path;
	}

	@Override
	public void setPrivileged(boolean arg0) {

	}

	@Override
	public void setPublicId(String arg0) {

	}

	@Override
	public void setReloadable(boolean arg0) {

	}

	@Override
	public void setSessionTimeout(int arg0) {

	}

	@Override
	public void setSwallowOutput(boolean arg0) {

	}

	@Override
	public void setTldValidation(boolean arg0) {

	}

	@Override
	public void setUseHttpOnly(boolean arg0) {

	}

	@Override
	public void setWrapperClass(String arg0) {

	}

	@Override
	public void setXmlNamespaceAware(boolean arg0) {

	}

	@Override
	public void setXmlValidation(boolean arg0) {

	}

	@Override
	public void addChild(Container arg0) {

	}

	@Override
	public void addContainerListener(ContainerListener arg0) {

	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener arg0) {

	}

	@Override
	public void backgroundProcess() {

	}

	@Override
	public Container findChild(String arg0) {
		return null;
	}

	@Override
	public Container[] findChildren() {
		return new Container[0];
	}

	@Override
	public ContainerListener[] findContainerListeners() {
		return new ContainerListener[0];
	}

	@Override
	public int getBackgroundProcessorDelay() {
		return 0;
	}

	@Override
	public Cluster getCluster() {
		return null;
	}

	@Override
	public Loader getLoader() {
		return null;
	}

	@Override
	public Log getLogger() {
		return null;
	}

	@Override
	public Manager getManager() {
		return null;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public ObjectName getObjectName() {
		return null;
	}

	@Override
	public Container getParent() {
		return _parent;
	}

	@Override
	public ClassLoader getParentClassLoader() {
		return null;
	}

	@Override
	public Pipeline getPipeline() {
		return _pipeline;
	}

	public void setPipeline(Pipeline pipeline) {
		_pipeline = pipeline;
	}

	@Override
	public Realm getRealm() {
		return _realm;
	}

	@Override
	public WebResourceRoot getResources() {
		return null;
	}

	@Override
	public void removeChild(Container arg0) {

	}

	@Override
	public void removeContainerListener(ContainerListener arg0) {

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener arg0) {

	}

	@Override
	public void setBackgroundProcessorDelay(int arg0) {

	}

	@Override
	public void setCluster(Cluster arg0) {

	}

	@Override
	public void setLoader(Loader arg0) {

	}

	@Override
	public void setManager(Manager arg0) {

	}

	@Override
	public void setName(String name) {
		_name = name;
	}

	@Override
	public void setParent(Container container) {
		_parent = container;
	}

	@Override
	public void setParentClassLoader(ClassLoader arg0) {

	}

	@Override
	public void setRealm(Realm realm) {
		_realm = realm;
	}

	@Override
	public String getSessionCookieDomain() {
		return null;
	}

	@Override
	public String getSessionCookieName() {
		return null;
	}

	@Override
	public String getSessionCookiePath() {
		return null;
	}

	@Override
	public void setSessionCookieDomain(String arg0) {

	}

	@Override
	public void setSessionCookieName(String arg0) {

	}

	@Override
	public void setSessionCookiePath(String arg0) {

	}

	@Override
	public AccessLog getAccessLog() {
		return null;
	}

	@Override
	public void logAccess(Request arg0, Response arg1, long arg2, boolean arg3) {

	}

	@Override
	public void fireContainerEvent(String arg0, Object arg1) {

	}

	@Override
	public void addLifecycleListener(LifecycleListener arg0) {

	}

	@Override
	public void destroy() throws LifecycleException {

	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		return new LifecycleListener[0];
	}

	@Override
	public LifecycleState getState() {
		return null;
	}

	@Override
	public String getStateName() {
		return null;
	}

	@Override
	public void init() throws LifecycleException {

	}

	@Override
	public void removeLifecycleListener(LifecycleListener arg0) {

	}

	@Override
	public void start() throws LifecycleException {

	}

	@Override
	public void stop() throws LifecycleException {

	}

	@Override
	public void addFilterMapBefore(FilterMap arg0) {

	}

	@Override
	public void addServletContainerInitializer(
			ServletContainerInitializer arg0, Set<Class<?>> arg1) {

	}

	@Override
	public void addServletMapping(String arg0, String arg1, boolean arg2) {

	}

	@Override
	public Authenticator getAuthenticator() {
		return _authenticator;
	}
	
	public void setAuthenticator(Authenticator authenticator){
	  _authenticator = authenticator;
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
	public JspConfigDescriptor getJspConfigDescriptor() {
		return null;
	}

	@Override
	public boolean getLogEffectiveWebXml() {
		return false;
	}

	@Override
	public boolean getPaused() {
		return false;
	}

	@Override
	public String getRealPath(String arg0) {
		return null;
	}

	@Override
	public boolean isServlet22() {
		return false;
	}

	@Override
	public void setConfigFile(URL arg0) {

	}

	@Override
	public void setEffectiveMajorVersion(int arg0) {

	}

	@Override
	public void setEffectiveMinorVersion(int arg0) {

	}

	@Override
	public void setLogEffectiveWebXml(boolean arg0) {

	}

	@Override
	public int getStartStopThreads() {
		return 0;
	}

	@Override
	public void setStartStopThreads(int arg0) {

	}

	@Override
	public boolean fireRequestDestroyEvent(ServletRequest arg0) {
		return false;
	}

	@Override
	public boolean fireRequestInitEvent(ServletRequest arg0) {
		return false;
	}

	@Override
	public boolean getAllowCasualMultipartParsing() {
		return false;
	}

	@Override
	public String getBaseName() {
		return null;
	}

	@Override
	public String getCharset(Locale arg0) {
		return null;
	}

	@Override
	public boolean getFireRequestListenersOnForwards() {
		return false;
	}

	@Override
	public boolean getPreemptiveAuthentication() {
		return false;
	}

	@Override
	public String getResourceOnlyServlets() {
		return null;
	}

	@Override
	public boolean getSendRedirectBody() {
		return false;
	}

	@Override
	public boolean getSessionCookiePathUsesTrailingSlash() {
		return false;
	}

	@Override
	public boolean getSwallowAbortedUploads() {
		return false;
	}

	@Override
	public String getWebappVersion() {
		return null;
	}

	@Override
	public boolean isResourceOnlyServlet(String arg0) {
		return false;
	}

	@Override
	public void setAllowCasualMultipartParsing(boolean arg0) {

	}

	@Override
	public void setFireRequestListenersOnForwards(boolean arg0) {

	}

	@Override
	public void setPreemptiveAuthentication(boolean arg0) {

	}

	@Override
	public void setResourceOnlyServlets(String arg0) {

	}

	@Override
	public void setSendRedirectBody(boolean arg0) {

	}

	@Override
	public void setSessionCookiePathUsesTrailingSlash(boolean arg0) {

	}

	@Override
	public void setSwallowAbortedUploads(boolean arg0) {

	}

	@Override
	public void setWebappVersion(String arg0) {

	}

	@Override
	public JarScanner getJarScanner() {
		return null;
	}

	@Override
	public void setJarScanner(JarScanner arg0) {

	}

    @Override
    public String getDomain() {
        return null;
    }

    @Override
    public String getMBeanKeyProperties() {
        return null;
    }

    @Override
    public File getCatalinaBase() {
        return null;
    }

    @Override
    public File getCatalinaHome() {
        return null;
    }

    @Override
    public boolean getDenyUncoveredHttpMethods() {
        return false;
    }

    @Override
    public void setDenyUncoveredHttpMethods(boolean denyUncoveredHttpMethods) {

    }

    @Override
    public void setNamingResources(NamingResourcesImpl namingResources) {

    }

    @Override
    public boolean getXmlBlockExternal() {
        return false;
    }

    @Override
    public void setXmlBlockExternal(boolean xmlBlockExternal) {

    }

    @Override
    public InstanceManager getInstanceManager() {
        return null;
    }

    @Override
    public void setInstanceManager(InstanceManager instanceManager) {

    }

    @Override
    public void setContainerSciFilter(String containerSciFilter) {

    }

    @Override
    public String getContainerSciFilter() {
        return null;
    }

    @Override
    public void addApplicationListener(ApplicationListener listener) {

    }

    @Override
    public ThreadBindingListener getThreadBindingListener() {
        return null;
    }

    @Override
    public void setThreadBindingListener(ThreadBindingListener threadBindingListener) {

    }

    @Override
    public void setJspConfigDescriptor(JspConfigDescriptor descriptor) {

    }

    @Override
    public Set<String> addServletSecurity(Dynamic registration, ServletSecurityElement servletSecurityElement) {
        return null;
    }

    @Override
    public void setResources(WebResourceRoot resources) {

    }

    @Override
    public void setAddWebinfClassesResources(boolean addWebinfClassesResources) {

    }

    @Override
    public boolean getAddWebinfClassesResources() {
        return false;
    }

    @Override
    public void addPostConstructMethod(String clazz, String method) {

    }

    @Override
    public void addPreDestroyMethod(String clazz, String method) {

    }

    @Override
    public void removePostConstructMethod(String clazz) {

    }

    @Override
    public void removePreDestroyMethod(String clazz) {

    }

    @Override
    public String findPostConstructMethod(String clazz) {
        return null;
    }

    @Override
    public String findPreDestroyMethod(String clazz) {
        return null;
    }

    @Override
    public Map<String, String> findPostConstructMethods() {
        return null;
    }

    @Override
    public Map<String, String> findPreDestroyMethods() {
        return null;
    }

    @Override
    public ClassLoader bind(boolean usePrivilegedAction, ClassLoader originalClassLoader) {
        return null;
    }

    @Override
    public void unbind(boolean usePrivilegedAction, ClassLoader originalClassLoader) {

    }
}
