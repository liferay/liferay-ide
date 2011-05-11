package com.liferay.ide.eclipse.server.aws.core;

import com.liferay.ide.eclipse.core.ILiferayConstants;
import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.core.util.ZipUtil;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.server.aws.core.util.BeanstalkUtil;
import com.liferay.ide.eclipse.server.tomcat.core.util.LiferayPublishHelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;


public class BeanstalkServerBehavior extends ServerBehaviourDelegate implements IServerLifecycleListener {

	protected ILaunch currentLaunch;
	protected Job remoteServerUpdateJob;
	protected IBeanstalkAdminService websphereAdminService;
	protected IStatus liferayServerStatus;

	public BeanstalkServerBehavior() {
		super();
	}

	public boolean canConnect() {
		return true;
	}

	@Override
	public IStatus canPublish() {
		if (currentLaunch != null) {
			return Status.OK_STATUS;
		}
		else {
			return Status.CANCEL_STATUS;
		}
	}

	@Override
	public IStatus canRestart(String mode) {
		return AWSCorePlugin.createErrorStatus("The server instance is remote and can not be restarted.");
	}

	@Override
	public boolean canRestartModule(IModule[] module) {
		IStatus status = getServer().canModifyModules(module, null, null);

		return status.isOK();
	}

	@Override
	public IStatus canStart(String launchMode) {
		return AWSCorePlugin.createErrorStatus("The server instance is remote and can not be started.");
	}

	@Override
	public IStatus canStop() {
		return AWSCorePlugin.createErrorStatus("The server instance is remote and can not be stopped.");
	}

	@Override
	public void dispose() {
		super.dispose();

		remoteServerUpdateJob.cancel();
		remoteServerUpdateJob = null;
	}

	public int getWASServerState(int currentServerState, IProgressMonitor monitor) {
		try {
			if (currentServerState == IServer.STATE_STOPPED) {
				monitor.beginTask("Updating server state for " + getServer().getName(), 100);
			}

			Object retval = null;

			IBeanstalkAdminService adminConnection = getAdminConnection();

			try {
				retval = adminConnection.getServerState();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			if (retval == null) {
				setServerStatus(AWSCorePlugin.createErrorStatus("Check connection settings."));
				return IServer.STATE_UNKNOWN;
			}

			String serverState = retval.toString();

			if ("STARTED".equals(serverState)) {
				return IServer.STATE_STARTED;
			}
			else if ("STOPPED".equals(serverState)) {
				return IServer.STATE_STOPPED;
			}
		}
		catch (Exception e) {
			AWSCorePlugin.logError("Could not get server state.", e);
			return IServer.STATE_UNKNOWN;
		}

		return IServer.STATE_UNKNOWN;
	}

	@Override
	public void handleResourceChange() {
		super.handleResourceChange();
	}

	@Override
	public void restart(String launchMode) throws CoreException {
		super.restart(launchMode);
	}

	public void serverAdded(IServer server) {
	}

	public void serverChanged(IServer server) {
	}

	public void serverRemoved(IServer server) {
		if (server.equals(getServer())) {
			if (currentLaunch != null && !currentLaunch.isTerminated()) {
				try {
					currentLaunch.terminate();
				}
				catch (DebugException e) {
				}
			}
		}

		ServerCore.removeServerLifecycleListener(this);
	}

	public void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy workingCopy, IProgressMonitor monitor)
		throws CoreException {

		// reuse the existing admin connection
		workingCopy.setAttribute(BeanstalkLaunchConfigDelegate.SERVER_ID, getServer().getId());
	}

	@Override
	public void startModule(IModule[] module, IProgressMonitor monitor) throws CoreException {

		if (module == null || module.length != 1) {
			throw new CoreException(AWSCorePlugin.createErrorStatus("Cannot start module with length " +
				(module != null ? module.length : 0)));
		}

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		IModule startModule = module[0];

		IProject startModuleProject = startModule.getProject();

		// IWebsphereAdminService adminService = getAdminConnection();
		//
		// String appName = BeanstalkUtil.getAppName(startModuleProject);
		//
		// setModuleStatus(module, AWSCorePlugin.createInfoStatus("Starting..."));
		//
		// Object error = adminService.startApplication(appName);
		//
		// if (error != null) {
		// throw new CoreException(AWSCorePlugin.createErrorStatus(error.toString()));
		// }

		setModuleStatus(module, null);

		setModuleState(module, IServer.STATE_STARTED);
	}

	@Override
	public void stop(boolean force) {
		setServerState(IServer.STATE_STOPPED);
	}

	@Override
	public void stopModule(IModule[] module, IProgressMonitor monitor) throws CoreException {

		if (module == null || module.length != 1) {
			throw new CoreException(AWSCorePlugin.createErrorStatus("Cannot stop module with length " +
				(module != null ? module.length : 0)));
		}

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		IModule stopModule = module[0];

		IProject stopModuleProject = stopModule.getProject();

		IBeanstalkAdminService adminService = getAdminConnection();

		String appName = BeanstalkUtil.getAppName(stopModuleProject);

		setModuleStatus(module, AWSCorePlugin.createInfoStatus("Stopping..."));

		Object error = adminService.stopApplication(appName);

		if (error != null) {
			throw new CoreException(AWSCorePlugin.createErrorStatus(error.toString()));
		}

		setModuleStatus(module, null);

		setModuleState(module, IServer.STATE_STOPPED);
	}

	protected Job checkRemoteServerState(IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		// make sure the server has not been deleted
		if (!(getServer().equals(ServerCore.findServer(getServer().getId())))) {
			remoteServerUpdateJob = null;
			return null;
		}

		final int state = getServer().getServerState();

		Job updateServerJob = null;

		switch (state) {
		case IServer.STATE_UNKNOWN:
			if (canConnect()) {
				updateServerJob = new Job("Updating status for " + getServer().getName()) {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						return updateServerState(state, monitor);
					}

				};
			}

			break;

		case IServer.STATE_STOPPED:
			if (canConnect()) {
				updateServerJob = new Job("Connecting to " + getServer().getName()) {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						int wasState = getWASServerState(state, monitor);

						if (wasState == IServer.STATE_STARTED) {
							setServerState(IServer.STATE_STARTED);
							launchServer(monitor);
						}

						return Status.OK_STATUS;
					}

				};
			}

			break;

		case IServer.STATE_STARTED:
			boolean isAlive = false;

			try {
				isAlive = getAdminConnection().isAlive();
			}
			catch (Exception ex) {
				// no error, this could because server is down
			}

			if (isAlive && (this.currentLaunch == null || this.currentLaunch.isTerminated())) {
				updateServerJob = new Job("Connecting to server: " + getServer().getName()) {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						launchServer(monitor);
						return Status.OK_STATUS;
					}

				};
			}
			else {
				// check on the current launch to make sure it is still valid
				if (!isAlive) {
					terminateLaunch();
					setServerState(IServer.STATE_STOPPED);
				}
			}

			break;

		case IServer.STATE_STOPPING:
		case IServer.STATE_STARTING:
			// do nothing since server state will get updated automatically after start/stop
			break;

		default:
			break;
		}

		return updateServerJob;
	}

	protected IBeanstalkAdminService getAdminConnection() {
		if (websphereAdminService == null) {
			websphereAdminService = AWSCorePlugin.getBeanstalkAdminService(getBeanstalkServer());
		}

		return websphereAdminService;
	}

	protected File getScriptFile(String scriptPath) {
		URL fileUrl = null;

		try {
			fileUrl = FileLocator.toFileURL(AWSCorePlugin.getPluginEntry("scripts/" + scriptPath));

			return new File(fileUrl.getFile());
		}
		catch (IOException e) {
			AWSCorePlugin.logError("Could not find script file.", e);
		}

		return null;
	}

	@Override
	protected IPath getTempDirectory() {
		return super.getTempDirectory();
	}

	@Override
	protected IPath getTempDirectory(boolean recycle) {
		return super.getTempDirectory(recycle);
	}

	protected IBeanstalkServer getBeanstalkServer() {
		return BeanstalkUtil.getBeanstalkServer(getServer());
	}

	@Override
	protected boolean hasBeenPublished(IModule[] module) {
		return super.hasBeenPublished(module);
	}

	@Override
	protected void initialize(IProgressMonitor monitor) {
		super.initialize(monitor);

		if (!(BeanstalkUtil.getBeanstalkServer(getServer()).isLocal())) {
			ServerCore.addServerLifecycleListener(this);

			remoteServerUpdateJob = new Job("WebSpere server update.") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					Job updateServerJob = checkRemoteServerState(monitor);

					if (updateServerJob != null) {
						updateServerJob.schedule();

						try {
							updateServerJob.join();
						}
						catch (InterruptedException e) {
						}
					}

					if (remoteServerUpdateJob != null) {
						remoteServerUpdateJob.schedule(IBeanstalkServer.SERVERS_VIEW_UPDATE_DELAY);
					}

					return Status.OK_STATUS;
				}
			};

			remoteServerUpdateJob.setSystem(true);
			remoteServerUpdateJob.schedule();
		}
	}

	protected boolean isModuleInstalled(IModule[] module) {
		for (IModule m : module) {
			String appName = BeanstalkUtil.getAppName(m.getProject());

			if (getAdminConnection().isAppInstalled(appName)) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings({ "unchecked" })
	protected void launchServer(IProgressMonitor monitor) {
		if (currentLaunch != null && (!currentLaunch.isTerminated())) {
			terminateLaunch();
		}

		ILaunchConfigurationWorkingCopy config = null;
		String launchMode = null;

		try {
			config = getServer().getLaunchConfiguration(true, null).getWorkingCopy();
			IBeanstalkAdminService adminProxy = getAdminConnection();
			Map debugOptions = adminProxy.getDebugOptions();

			Object debugMode = debugOptions.get("debugMode");
			Object debugArgs = debugOptions.get("debugArgs");

			if (debugMode != null && Boolean.parseBoolean(debugMode.toString())) {
				Pattern pattern = Pattern.compile(".*address=([0-9]+).*", Pattern.DOTALL);
				Matcher matcher = pattern.matcher(debugArgs.toString());
				matcher.matches();
				matcher.group(1);

				String debugPort = matcher.group(1);

				Map connectMap = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, (Map) null);

				if (connectMap == null) {
					connectMap = new HashMap();
					config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, connectMap);
				}

				connectMap.put("hostname", getServer().getHost());
				connectMap.put("port", debugPort);

				launchMode = ILaunchManager.DEBUG_MODE;
			}
			else {
				launchMode = ILaunchManager.RUN_MODE;
			}

			try {
				currentLaunch = config.launch(launchMode, null);
			}
			catch (CoreException ce) {
				if (Boolean.parseBoolean(debugMode.toString())) {
					// if debug launch failed just try to launch one in run mode
					BeanstalkUtil.terminateLaunchesForConfig(config);
				}

				try {
					currentLaunch = config.launch(ILaunchManager.RUN_MODE, null);
				}
				catch (CoreException ce1) {
					BeanstalkUtil.terminateLaunchesForConfig(config);
				}
			}

		}
		catch (Exception e) {
			AWSCorePlugin.logError("Could not create new server launch configuration.", e);
		}
	}

	@Override
	protected void publishFinish(IProgressMonitor monitor) throws CoreException {

		super.publishFinish(monitor);

		setServerPublishState(IServer.PUBLISH_STATE_NONE);
	}

	@Override
	protected void publishModule(int kind, int deltaKind, IModule[] module, IProgressMonitor monitor)
		throws CoreException {

		boolean shouldPublishModule =
			LiferayPublishHelper.prePublishModule(
				this, kind, deltaKind, module, getPublishedResourceDelta(module), monitor);

		if (!shouldPublishModule) {
			return;
		}

		int modulePublishState = -1;

		if (kind == IServer.PUBLISH_FULL && (deltaKind == ADDED || deltaKind == CHANGED)) {
			modulePublishState = publishModuleFull(module, deltaKind, monitor);
		}
		else if (kind == IServer.PUBLISH_FULL && deltaKind == NO_CHANGE) {
			// first check to see if this module is even in the list of applications, if it is then we don't need to
			// actually update it
			if (!isModuleInstalled(module)) {
				modulePublishState = publishModuleFull(module, deltaKind, monitor);
			}
		}
		else if (kind == IServer.PUBLISH_FULL && deltaKind == REMOVED) {
			modulePublishState = removeModule(module, monitor);
		}
		else if ((kind == IServer.PUBLISH_AUTO || kind == IServer.PUBLISH_INCREMENTAL) && deltaKind == CHANGED) {
			// modulePublishState = publishModuleDelta(module, monitor);
			modulePublishState = publishModuleFull(module, deltaKind, monitor);
		}


		// by default, assume the module has published successfully.
		// this will update the publish state and delta correctly
		setModulePublishState(module, modulePublishState);
	}

	protected void checkValidLiferayServer() throws CoreException {
		if (liferayServerStatus != null && liferayServerStatus.isOK()) {
			return;
		}

		// ILiferayServer liferayServer = (ILiferayServer) getServer().loadAdapter(ILiferayServer.class, null);
		//
		// URL url = liferayServer.getPortalHomeUrl();
		//
		// try {
		// // check for invalid license
		// URL licenseUrl = new URL(url.toExternalForm() + "/c/portal/ee/license");
		// String licenseContents = CoreUtil.readStreamToString(licenseUrl.openStream());
		//
		// if (licenseContents != null && licenseContents.contains("server is not registered")) {
		// throw new CoreException(
		// AWSCorePlugin.createErrorStatus("Liferay Portal Enterprise Edition is not registered."));
		// }
		// else {
				liferayServerStatus = Status.OK_STATUS;
		// }
		// }
		// catch (IOException ex) {
		// throw new CoreException(AWSCorePlugin.createErrorStatus(ex));
		// }
	}

	protected int publishModuleFull(IModule[] module, int deltaKind, IProgressMonitor monitor) throws CoreException {

		if (module == null || module.length != 1) {
			throw new CoreException(AWSCorePlugin.createErrorStatus("Cannot publish module with length " +
				(module != null ? module.length : 0)));
		}

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		IModule publishModule = module[0];

		// IWebModule webModule = (IWebModule) publishModule.getAdapter(IWebModule.class);

		// if (webModule instanceof J2EEFlexProjDeployable) {
		// J2EEFlexProjDeployable projDeployable = (J2EEFlexProjDeployable) webModule;
		// }

		IProject moduleProject = publishModule.getProject();

		IProgressMonitor submon = CoreUtil.newSubMonitor(monitor, 100);
		submon.subTask("Deploying " + moduleProject.getName() + "  to Liferay...");

		// ModuleDelegate md =
		// (ModuleDelegate) publishModule.loadAdapter(ModuleDelegate.class, ProgressMonitorUtil.submon(monitor, 100));
		// IModuleResource[] members = md.members();
		//
		// for (IModuleResource member : members) {
		// System.out.println(member.getName());
		// }

		SDK sdk = ProjectUtil.getSDK(moduleProject);

		Map<String, String> properties = new HashMap<String, String>();
		properties.put(ISDKConstants.PROPERTY_AUTO_DEPLOY_UNPACK_WAR, "false");

		IPath deployPath = AWSCorePlugin.getTempLocation("direct-deploy", "");

		properties.put(ISDKConstants.PROPERTY_APP_SERVER_DEPLOY_DIR, deployPath.toOSString());

		// File pluginFile = deployPath.append(moduleProject.getName() + ".war").toFile();
		File pluginFile = deployPath.append(moduleProject.getName()).toFile();
		pluginFile.getParentFile().mkdirs();

		submon.worked(10); // 10% complete

		if (monitor.isCanceled()) {
			return IServer.PUBLISH_STATE_FULL;
		}

		submon.subTask("Pre-deploying " + moduleProject.getName() + " contents...");

		IStatus directDeployStatus = sdk.directDeploy(moduleProject, properties, true);

		if (!directDeployStatus.isOK() || (!pluginFile.exists())) {
			throw new CoreException(directDeployStatus);
		}

		submon.worked(15); // 25% complete

		if (monitor.isCanceled()) {
			return IServer.PUBLISH_STATE_FULL;
		}

		submon.subTask("Preparing contents for deploying " + moduleProject.getName() + " to AWS Beanstalk...");

		IPath deployPreparePath = AWSCorePlugin.getTempLocation("deployment", "");

		File warFile = deployPreparePath.append(moduleProject.getName() + ".war").toFile();
		warFile.getParentFile().mkdirs();

		try {
			ZipUtil.zip(pluginFile, warFile);
		}
		catch (IOException e) {
		}

		submon.worked(10); // 35% complete

		if (monitor.isCanceled()) {
			return IServer.PUBLISH_STATE_FULL;
		}

		IBeanstalkAdminService adminService = getAdminConnection();

		setModuleStatus(module, AWSCorePlugin.createInfoStatus("Installing..."));

		submon.worked(15); // 50% complete

		if (monitor.isCanceled()) {
			return IServer.PUBLISH_STATE_FULL;
		}

		submon.subTask("Publishing " + moduleProject.getName() + " to AWS Beanstalk...");

		Object error = null;

		try {
			error =
				adminService.installApplication(
					"liferay-test-01",
					warFile.getAbsolutePath(), BeanstalkUtil.getAppName(moduleProject), submon);
		}
		catch (Exception ex) {
			setModuleStatus(module, null);
			setModuleState(module, IServer.STATE_UNKNOWN);
			throw new CoreException(AWSCorePlugin.createErrorStatus(ex));
		}

		if (error != null) {
			setModuleStatus(module, null);
			setModuleState(module, IServer.STATE_UNKNOWN);
			throw new CoreException(AWSCorePlugin.createErrorStatus(error.toString()));
		}

		submon.worked(40); // 90%

		setModuleStatus(module, AWSCorePlugin.createInfoStatus("Starting..."));

		// scriptFile = getScriptFile("publish/startApplicationScript.groovy");

		if (monitor.isCanceled()) {
			setModuleStatus(module, null);
			return IServer.PUBLISH_STATE_UNKNOWN;
		}

		setModuleStatus(module, null);
		setModuleState(module, IServer.STATE_STARTED);

		submon.worked(10); // 100%
		monitor.done();

		return IServer.PUBLISH_STATE_NONE;
	}

	@Override
	protected void publishServer(int kind, IProgressMonitor monitor) throws CoreException {
		super.publishServer(kind, monitor);
	}

	@Override
	protected void publishStart(IProgressMonitor monitor) throws CoreException {

		int state = getServer().getServerState();

		if (state != IServer.STATE_STARTED) {
			throw new CoreException(
				AWSCorePlugin.createErrorStatus("Cannot publish to remote server that is not started."));
		}

		try {
			if (getAdminConnection().isAlive()) {
				checkValidLiferayServer();
				return;
			}
		}
		catch (RuntimeException e) { // could not communicate with server
			throw new CoreException(
				AWSCorePlugin.createErrorStatus("Not connected to remote server, publishing cannot proceed."));
		}

	}

	protected int removeModule(IModule[] module, IProgressMonitor monitor) throws CoreException {

		if (module == null || module.length != 1) {
			throw new CoreException(AWSCorePlugin.createErrorStatus("Cannot publish module with length " +
				(module != null ? module.length : 0)));
		}

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		IModule publishModule = module[0];

		IProject moduleProject = publishModule.getProject();

		if (moduleProject == null) {
			// just return this is a dead module
			setModuleStatus(module, null);
			return IServer.PUBLISH_STATE_UNKNOWN;
		}

		/*
		 * First look to see if this module (ear plugin) is actually installed on websphere, and if it is then uninstall
		 * it
		 */

		monitor.beginTask("Undeploying " + moduleProject.getName() + " from AWS Beanstalk...", 100);

		String appName = BeanstalkUtil.getAppName(moduleProject);

		setModuleStatus(module, AWSCorePlugin.createInfoStatus("Uninstalling..."));

		// monitor.subTask("Getting WebSphere admin connection...");

		IBeanstalkAdminService adminService = getAdminConnection();

		monitor.worked(25); // 25%

		// File scriptFile = getScriptFile("publish/uninstallApplicationScript.groovy");

		monitor.subTask("Uninstalling " + moduleProject.getName() + " from AWS Beanstalk...");

		// Object error = adminService.uninstallApplication(scriptFile, appName, monitor);

		monitor.worked(75); // 100%

		// if (error != null) {
		// throw new CoreException(AWSCorePlugin.createErrorStatus(error.toString()));
		// }

		setModuleStatus(module, null);

		return IServer.PUBLISH_STATE_NONE;
	}

	protected boolean shouldPublishModuleFull(IModuleResourceDelta[] deltas) {
		boolean retval = false;

		if (!CoreUtil.isNullOrEmpty(deltas)) {
			for (IModuleResourceDelta delta : deltas) {
				if (shouldPublishModuleFull(delta.getAffectedChildren())) {
					retval = true;
					break;
				}
				else {
					IModuleResource resource = delta.getModuleResource();

					if (resource.getName().equals("web.xml") ||
						resource.getName().equals(ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE)) {

						retval = isResourceInDocroot(resource);

						if (retval) {
							break;
						}
					}
					else if (resource.getName().equals("portlet.xml")) {
						// if portlet-custom.xml is used we need to redeploy on this change.
						retval = isResourceInDocroot(resource);

						if (retval) {
							break;
						}
					}
				}
			}
		}

		return retval;
	}

	private boolean isResourceInDocroot(IModuleResource resource) {
		IFile file = (IFile) resource.getAdapter(IFile.class);

		if (file != null) {
			IFolder docroot = ProjectUtil.getDocroot(file.getProject());

			return docroot != null && docroot.exists() && docroot.getFullPath().isPrefixOf(file.getFullPath());
		}

		return false;
	}

	protected void terminateLaunch() {
		if (currentLaunch != null) {
			try {
				currentLaunch.terminate();
			}
			catch (DebugException e) {
			}

			currentLaunch = null;
		}
	}

	protected IStatus updateModuleState(IModule module) {
		String appName = BeanstalkUtil.getAppName(module.getProject());

		boolean appStarted = getAdminConnection().isAppStarted(appName);

		IModule[] module2 = new IModule[] { module };

		setModuleState(module2, appStarted ? IServer.STATE_STARTED : IServer.STATE_STOPPED);

		return Status.OK_STATUS;
	}

	protected IStatus updateServerState(int currentServerState, IProgressMonitor monitor) {
		if (getServer() == null) {
			return Status.OK_STATUS;
		}

		monitor.beginTask("Updating server status...", 100);

		int websphereState = getWASServerState(currentServerState, monitor);

		if (websphereState == IServer.STATE_STARTED) {
			setServerState(IServer.STATE_STARTED);
			launchServer(monitor);
		}
		else if (websphereState == IServer.STATE_STOPPED) {
			terminateLaunch();
			setServerState(IServer.STATE_STOPPED);
		}

		// check modules
		IModule[] modules = getServer().getModules();

		// if (!CoreUtil.isNullOrEmpty(modules)) {
		// Vector apps = getAdminConnection().listApplications();
		//
		// for (IModule module : modules) {
		// if (ProjectUtil.isLiferayProject(module.getProject())) {
		// String appName = BeanstalkUtil.getAppName(module.getProject());
		//
		// if (apps.contains(appName)) {
		// updateModuleState(module);
		// }
		// else {
		// setModuleState(new IModule[] { module }, IServer.STATE_UNKNOWN);
		// }
		// }
		// }
		// }

		return Status.OK_STATUS;
	}
}
