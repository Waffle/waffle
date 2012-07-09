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

public class SimpleEngine implements org.apache.catalina.Engine {

	private Pipeline _pipeline;
	
	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public Loader getLoader() {
		return null;
	}

	@Override
	public void setLoader(Loader loader) {

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
	public void setManager(Manager manager) {

	}

	@Override
	public Object getMappingObject() {
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
	public DirContext getResources() {
		return null;
	}

	@Override
	public void setResources(DirContext resources) {
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
		return null;
	}

	@Override
	public ContainerListener[] findContainerListeners() {		
		return null;
	}

	@Override
	public void invoke(Request request, Response response) throws IOException,
			ServletException {		

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
		return null;
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

}
