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

import javax.management.ObjectName;
import javax.naming.directory.DirContext;
import javax.servlet.ServletException;

import org.apache.catalina.AccessLog;
import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Engine;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Loader;
import org.apache.catalina.Manager;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;

public class SimpleEngine implements Engine {

    private Pipeline pipeline;

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
    public void setLoader(Loader loader) {
        // Not Implemented
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

    @Override
    public void setManager(Manager manager) {
        // Not Implemented
    }

    @Override
    public Object getMappingObject() {
        // Not Implemented
        return null;
    }

    @Override
    public ObjectName getObjectName() {
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

    @Override
    public Cluster getCluster() {
        // Not Implemented
        return null;
    }

    @Override
    public void setCluster(Cluster cluster) {
        // Not Implemented
    }

    @Override
    public int getBackgroundProcessorDelay() {
        // Not Implemented
        return 0;
    }

    @Override
    public void setBackgroundProcessorDelay(int delay) {
        // Not Implemented
    }

    @Override
    public String getName() {
        // Not Implemented
        return null;
    }

    @Override
    public void setName(String name) {
        // Not Implemented
    }

    @Override
    public Container getParent() {
        // Not Implemented
        return null;
    }

    @Override
    public void setParent(Container container) {
        // Not Implemented
    }

    @Override
    public ClassLoader getParentClassLoader() {
        // Not Implemented
        return null;
    }

    @Override
    public void setParentClassLoader(ClassLoader parent) {
        // Not Implemented
    }

    @Override
    public Realm getRealm() {
        // Not Implemented
        return null;
    }

    @Override
    public void setRealm(Realm realm) {
        // Not Implemented
    }

    @Override
    public DirContext getResources() {
        // Not Implemented
        return null;
    }

    @Override
    public void setResources(DirContext resources) {
        // Not Implemented
    }

    @Override
    public void backgroundProcess() {
        // Not Implemented
    }

    @Override
    public void addChild(Container child) {
        // Not Implemented
    }

    @Override
    public void addContainerListener(ContainerListener listener) {
        // Not Implemented
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        // Not Implemented
    }

    @Override
    public Container findChild(String name) {
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

    @Deprecated
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        // Not Implemented
    }

    @Override
    public void removeChild(Container child) {
        // Not Implemented
    }

    @Override
    public void removeContainerListener(ContainerListener listener) {
        // Not Implemented
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        // Not Implemented
    }

    @Override
    public void fireContainerEvent(String type, Object data) {
        // Not Implemented
    }

    @Override
    public void logAccess(Request request, Response response, long time, boolean useDefault) {
        // Not Implemented
    }

    @Override
    public AccessLog getAccessLog() {
        // Not Implemented
        return null;
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        // Not Implemented
    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        // Not Implemented
        return new LifecycleListener[0];
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        // Not Implemented
    }

    @Override
    public void init() throws LifecycleException {
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
    public void destroy() throws LifecycleException {
        // Not Implemented
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
    public String getDefaultHost() {
        // Not Implemented
        return null;
    }

    @Override
    public void setDefaultHost(String defaultHost) {
        // Not Implemented
    }

    @Override
    public String getJvmRoute() {
        // Not Implemented
        return null;
    }

    @Override
    public void setJvmRoute(String jvmRouteId) {
        // Not Implemented
    }

    @Override
    public Service getService() {
        // Not Implemented
        return null;
    }

    @Override
    public void setService(Service service) {
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

}
