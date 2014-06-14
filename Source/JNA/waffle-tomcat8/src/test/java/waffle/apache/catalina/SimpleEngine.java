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
import javax.management.ObjectName;
import org.apache.catalina.AccessLog;
import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;

public class SimpleEngine implements org.apache.catalina.Engine {

	private Pipeline _pipeline;

	@Override
	public Log getLogger() {
		return null;
	}

	@Override
	public ObjectName getObjectName() {
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
	public Cluster getCluster() {
		return null;
	}

	@Override
	public void setCluster(Cluster cluster) {

	}

	@Override
	public int getBackgroundProcessorDelay() {
		return 0;
	}

	@Override
	public void setBackgroundProcessorDelay(int delay) {

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setName(String name) {

	}

	@Override
	public Container getParent() {
		return null;
	}

	@Override
	public void setParent(Container container) {

	}

	@Override
	public ClassLoader getParentClassLoader() {
		return null;
	}

	@Override
	public void setParentClassLoader(ClassLoader parent) {

	}

	@Override
	public Realm getRealm() {
		return null;
	}

	@Override
	public void setRealm(Realm realm) {

	}

	@Override
	public void backgroundProcess() {

	}

	@Override
	public void addChild(Container child) {

	}

	@Override
	public void addContainerListener(ContainerListener listener) {

	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {

	}

	@Override
	public Container findChild(String name) {
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
	public void removeChild(Container child) {

	}

	@Override
	public void removeContainerListener(ContainerListener listener) {

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {

	}

	@Override
	public void fireContainerEvent(String type, Object data) {

	}

	@Override
	public void logAccess(Request request, Response response, long time,
			boolean useDefault) {

	}

	@Override
	public AccessLog getAccessLog() {
		return null;
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {

	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		return new LifecycleListener[0];
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {

	}

	@Override
	public void init() throws LifecycleException {

	}

	@Override
	public void start() throws LifecycleException {

	}

	@Override
	public void stop() throws LifecycleException {

	}

	@Override
	public void destroy() throws LifecycleException {

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
	public String getDefaultHost() {
		return null;
	}

	@Override
	public void setDefaultHost(String defaultHost) {

	}

	@Override
	public String getJvmRoute() {
		return null;
	}

	@Override
	public void setJvmRoute(String jvmRouteId) {

	}

	@Override
	public Service getService() {
		return null;
	}

	@Override
	public void setService(Service service) {

	}

	@Override
	public int getStartStopThreads() {
		return 0;
	}

	@Override
	public void setStartStopThreads(int arg0) {

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

}
