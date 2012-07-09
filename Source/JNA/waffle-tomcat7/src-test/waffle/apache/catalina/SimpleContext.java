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
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import javax.management.ObjectName;
import javax.naming.directory.DirContext;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
import org.apache.catalina.deploy.ApplicationParameter;
import org.apache.catalina.deploy.ErrorPage;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.NamingResources;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.util.CharsetMapper;
import org.apache.juli.logging.Log;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.http.mapper.Mapper;

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
	
	public SimpleContext() {
		
	}
	
	public void addApplicationListener(String arg0) {

	}

	public void addApplicationParameter(ApplicationParameter arg0) {

	}

	public void addConstraint(SecurityConstraint arg0) {

	}

	public void addErrorPage(ErrorPage arg0) {

	}

	public void addFilterDef(FilterDef arg0) {

	}

	public void addFilterMap(FilterMap arg0) {

	}

	public void addInstanceListener(String arg0) {

	}

	public void addJspMapping(String arg0) {

	}

	public void addLocaleEncodingMappingParameter(String arg0, String arg1) {

	}

	public void addMimeMapping(String arg0, String arg1) {

	}

	public void addParameter(String arg0, String arg1) {

	}

	public void addRoleMapping(String arg0, String arg1) {

	}

	public void addSecurityRole(String arg0) {

	}

	public void addServletMapping(String arg0, String arg1) {

	}

	public void addTaglib(String arg0, String arg1) {

	}

	public void addWatchedResource(String arg0) {

	}

	public void addWelcomeFile(String arg0) {

	}

	public void addWrapperLifecycle(String arg0) {

	}

	public void addWrapperListener(String arg0) {

	}

	public Wrapper createWrapper() {
		return null;
	}

	public String[] findApplicationListeners() {
		return null;
	}

	public ApplicationParameter[] findApplicationParameters() {
		return null;
	}

	
	public SecurityConstraint[] findConstraints() {
		return null;
	}

	
	public ErrorPage findErrorPage(int arg0) {
		return null;
	}

	
	public ErrorPage findErrorPage(String arg0) {
		return null;
	}

	public ErrorPage[] findErrorPages() {
		return null;
	}

	public FilterDef findFilterDef(String arg0) {
		return null;
	}
	
	public FilterDef[] findFilterDefs() {
		return null;
	}
	
	public FilterMap[] findFilterMaps() {
		return null;
	}

	
	public String[] findInstanceListeners() {
		return null;
	}

	public String findMimeMapping(String arg0) {
		return null;
	}
	
	public String[] findMimeMappings() {
		return null;
	}
	
	public String findParameter(String arg0) {
		return null;
	}
	
	public String[] findParameters() {
		return null;
	}

	public String findRoleMapping(String arg0) {
		return null;
	}

	public boolean findSecurityRole(String arg0) {
		return false;
	}
	
	public String[] findSecurityRoles() {
		return null;
	}

	public String findServletMapping(String arg0) {
		return null;
	}
	
	public String[] findServletMappings() {
		return null;
	}
	
	public String findStatusPage(int arg0) {
		return null;
	}
	
	public int[] findStatusPages() {
		return null;
	}
	
	public String findTaglib(String arg0) {
		return null;
	}
	
	public String[] findTaglibs() {
		return null;
	}
	
	public String[] findWatchedResources() {
		return null;
	}
	
	public boolean findWelcomeFile(String arg0) {
		return false;
	}
	
	public String[] findWelcomeFiles() {
		return null;
	}
	
	public String[] findWrapperLifecycles() {
		return null;
	}
	
	public String[] findWrapperListeners() {
		return null;
	}
	
	public String getAltDDName() {
		return null;
	}
	
	public Object[] getApplicationEventListeners() {
		return null;
	}
	
	public Object[] getApplicationLifecycleListeners() {
		return null;
	}
	
	public boolean getAvailable() {
		return false;
	}
	
	public CharsetMapper getCharsetMapper() {
		return null;
	}

	
	public URL getConfigFile() {
		return null;
	}
	
	public boolean getConfigured() {
		return false;
	}
	
	public boolean getCookies() {
		return false;
	}
	
	public boolean getCrossContext() {
		return false;
	}
	
	public String getDisplayName() {
		return null;
	}
	
	public boolean getDistributable() {
		return false;
	}
	
	public String getDocBase() {
		return null;
	}
	
	public String getEncodedPath() {
		return null;
	}

	public boolean getIgnoreAnnotations() {
		return false;
	}
	
	public LoginConfig getLoginConfig() {
		return null;
	}
	
	public Mapper getMapper() {
		return null;
	}
	
	public NamingResources getNamingResources() {
		return null;
	}

	public boolean getOverride() {
		return false;
	}
	
	public String getPath() {
		return _path;
	}
	
	public boolean getPrivileged() {
		return false;
	}
	
	public String getPublicId() {
		return null;
	}
	
	public boolean getReloadable() {
		return false;
	}
	
	public ServletContext getServletContext() {
		return _servletContext;
	}
	
	public int getSessionTimeout() {
		return 0;
	}
	
	public boolean getSwallowOutput() {
		return false;
	}
	
	public boolean getTldNamespaceAware() {
		return false;
	}
	
	public boolean getTldValidation() {
		return false;
	}
	
	public boolean getUseHttpOnly() {
		return false;
	}
	
	public String getWrapperClass() {
		return null;
	}
	
	public boolean getXmlNamespaceAware() {
		return false;
	}
	
	public boolean getXmlValidation() {
		return false;
	}
	
	public void reload() {

	}
	
	public void removeApplicationListener(String arg0) {

	}
	
	public void removeApplicationParameter(String arg0) {

	}
	
	public void removeConstraint(SecurityConstraint arg0) {

	}
	
	public void removeErrorPage(ErrorPage arg0) {

	}
	
	public void removeFilterDef(FilterDef arg0) {

	}
	
	public void removeFilterMap(FilterMap arg0) {

	}
	
	public void removeInstanceListener(String arg0) {

	}
	
	public void removeMimeMapping(String arg0) {

	}
	
	public void removeParameter(String arg0) {

	}

	public void removeRoleMapping(String arg0) {

	}
	
	public void removeSecurityRole(String arg0) {

	}
	
	public void removeServletMapping(String arg0) {

	}
	
	public void removeTaglib(String arg0) {

	}
	
	public void removeWatchedResource(String arg0) {

	}
	
	public void removeWelcomeFile(String arg0) {

	}
	
	public void removeWrapperLifecycle(String arg0) {

	}

	public void removeWrapperListener(String arg0) {

	}

	public void setAltDDName(String arg0) {

	}

	public void setApplicationEventListeners(Object[] arg0) {

	}
	
	public void setApplicationLifecycleListeners(Object[] arg0) {

	}
	
	public void setAvailable(boolean arg0) {

	}
	
	public void setCharsetMapper(CharsetMapper arg0) {

	}
	
	public void setConfigFile(String arg0) {

	}

	public void setConfigured(boolean arg0) {

	}
	
	public void setCookies(boolean arg0) {

	}
	
	public void setCrossContext(boolean arg0) {

	}
	
	public void setDisplayName(String arg0) {

	}

	public void setDistributable(boolean arg0) {

	}
	
	public void setDocBase(String arg0) {

	}
	
	public void setIgnoreAnnotations(boolean arg0) {

	}
	
	public void setLoginConfig(LoginConfig arg0) {

	}
	
	public void setNamingResources(NamingResources arg0) {

	}
	
	public void setOverride(boolean arg0) {

	}
	
	public void setPath(String path) {
		_path = path;
	}

	public void setPrivileged(boolean arg0) {

	}
	
	public void setPublicId(String arg0) {

	}
	
	public void setReloadable(boolean arg0) {

	}

	public void setSessionTimeout(int arg0) {

	}
	
	public void setSwallowOutput(boolean arg0) {

	}

	public void setTldNamespaceAware(boolean arg0) {

	}

	public void setTldValidation(boolean arg0) {

	}
	
	public void setUseHttpOnly(boolean arg0) {

	}
	
	public void setWrapperClass(String arg0) {

	}
	
	public void setXmlNamespaceAware(boolean arg0) {

	}

	public void setXmlValidation(boolean arg0) {

	}

	public void addChild(Container arg0) {

	}

	public void addContainerListener(ContainerListener arg0) {

	}
	
	public void addPropertyChangeListener(PropertyChangeListener arg0) {

	}
	
	public void backgroundProcess() {

	}

	public Container findChild(String arg0) {
		return null;
	}

	public Container[] findChildren() {
		return null;
	}
	
	public ContainerListener[] findContainerListeners() {
		return null;
	}
	
	public int getBackgroundProcessorDelay() {
		return 0;
	}
	
	public Cluster getCluster() {
		return null;
	}

	public String getInfo() {
		return null;
	}
	
	public Loader getLoader() {
		return null;
	}
	
	public Log getLogger() {
		return null;
	}
	
	public Manager getManager() {
		return null;
	}
	
	public Object getMappingObject() {
		return null;
	}
	
	public String getName() {
		return _name;
	}
	
	public ObjectName getObjectName() {
		return null;
	}
	
	public Container getParent() {
		return _parent;
	}
	
	public ClassLoader getParentClassLoader() {
		return null;
	}
	
	public Pipeline getPipeline() {
		return _pipeline;
	}
	
	public void setPipeline(Pipeline pipeline) {
		_pipeline = pipeline;
	}

	public Realm getRealm() {
		return _realm;
	}

	public DirContext getResources() {
		return null;
	}
	
	public void invoke(Request arg0, Response arg1) throws IOException,
			ServletException {

	}
	
	public void removeChild(Container arg0) {

	}
	
	public void removeContainerListener(ContainerListener arg0) {

	}
	
	public void removePropertyChangeListener(PropertyChangeListener arg0) {

	}

	public void setBackgroundProcessorDelay(int arg0) {

	}
	
	public void setCluster(Cluster arg0) {

	}
	
	public void setLoader(Loader arg0) {

	}
	
	public void setManager(Manager arg0) {

	}

	public void setName(String name) {
		_name = name;
	}
	
	public void setParent(Container container) {
		_parent = container;
	}
	
	public void setParentClassLoader(ClassLoader arg0) {

	}
	
	public void setRealm(Realm realm) {
		_realm = realm;
	}
	
	public void setResources(DirContext arg0) {

	}

	public String getSessionCookieDomain() {
		return null;
	}

	public String getSessionCookieName() {
		return null;
	}

	public String getSessionCookiePath() {
		return null;
	}

	public void setSessionCookieDomain(String arg0) {
		
	}

	public void setSessionCookieName(String arg0) {
		
	}

	public void setSessionCookiePath(String arg0) {
		
	}

	@Override
	public void fireContainerEvent(String arg0, Object arg1) {
		
	}

	@Override
	public AccessLog getAccessLog() {
		return null;
	}

	@Override
	public void logAccess(Request arg0, Response arg1, long arg2, boolean arg3) {
		
	}

	@Override
	public void addLifecycleListener(LifecycleListener arg0) {
		
	}

	@Override
	public void destroy() throws LifecycleException {
		
	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		return null;
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
	public void addResourceJarUrl(URL arg0) {
		
	}

	@Override
	public void addServletContainerInitializer(
			ServletContainerInitializer arg0, Set<Class<?>> arg1) {
		
	}

	@Override
	public void addServletMapping(String arg0, String arg1, boolean arg2) {
		
	}

	@Override
	public Set<String> addServletSecurity(ApplicationServletRegistration arg0,
			ServletSecurityElement arg1) {
		return null;
	}

	@Override
	public Authenticator getAuthenticator() {
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
	public JarScanner getJarScanner() {
		return null;
	}

	@Override
	public void setJarScanner(JarScanner arg0) {
		// TODO Auto-generated method stub
		
	}
}
