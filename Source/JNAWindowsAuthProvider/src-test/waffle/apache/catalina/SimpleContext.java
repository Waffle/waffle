/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.apache.catalina;

import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.naming.directory.DirContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.Loader;
import org.apache.catalina.Manager;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.ApplicationParameter;
import org.apache.catalina.deploy.ErrorPage;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.NamingResources;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.util.CharsetMapper;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.http.mapper.Mapper;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleContext implements Context {

	private Realm _realm = null;
	private ServletContext _servletContext = new SimpleServletContext();
	
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

	
	public String getConfigFile() {
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
		return null;
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
	
	public void setPath(String arg0) {

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
		return null;
	}
	
	public String getObjectName() {
		return null;
	}
	
	public Container getParent() {
		return null;
	}
	
	public ClassLoader getParentClassLoader() {
		return null;
	}
	
	public Pipeline getPipeline() {
		return null;
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

	public void setName(String arg0) {

	}
	
	public void setParent(Container arg0) {

	}
	
	public void setParentClassLoader(ClassLoader arg0) {

	}
	
	public void setRealm(Realm realm) {
		_realm = realm;
	}
	
	public void setResources(DirContext arg0) {

	}
}
