/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.LiferayRuntimeClasspathEntry;
import com.liferay.ide.core.properties.PortalPropertiesConfiguration;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.ILiferayServerBehavior;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.gogo.GogoBundleDeployer;
import com.liferay.ide.server.util.PingThread;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
@SuppressWarnings({"restriction", "rawtypes"})
public class PortalServerBehavior
	extends ServerBehaviourDelegate
	implements ILiferayServerBehavior, IJavaLaunchConfigurationConstants {

	public static final String ATTR_STOP = "stop-server";

	public PortalServerBehavior() {
		_watchedProjects = new LinkedHashSet<IProject>();
	}

	public void addProcessListener(IProcess newProcess) {
		if ((_processListener != null) || (newProcess == null)) {
			return;
		}

		_processListener = new IDebugEventSetListener() {

			@Override
			public void handleDebugEvents(DebugEvent[] events) {
				if (events != null) {
					for (DebugEvent event : events) {
						if ((newProcess != null) && newProcess.equals(event.getSource()) &&
							(event.getKind() == DebugEvent.TERMINATE)) {

							cleanup();
						}
					}
				}
			}

		};

		DebugPlugin debugPlugin = DebugPlugin.getDefault();

		debugPlugin.addDebugEventListener(_processListener);
	}

	public void addWatchProject(IProject project) {
		try {
			if (IServer.STATE_STARTED == getServer().getServerState()) {
				_watchedProjects.add(project);
				_refreshSourceLookup();
			}
		}
		catch (CoreException ce) {
			LiferayServerCore.logError("Could not reinitialize source lookup director", ce);
		}
	}

	@Override
	public boolean canRestartModule(IModule[] modules) {
		for (IModule module : modules) {
			IProject project = module.getProject();

			if (project == null) {
				return false;
			}

			IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

			if ((bundleProject != null) && !bundleProject.isFragmentBundle()) {
				return true;
			}
		}

		return false;
	}

	public void cleanup() {
		if (_ping != null) {
			_ping.stop();

			_ping = null;
		}

		if (_processListener != null) {
			DebugPlugin debugPlugin = DebugPlugin.getDefault();

			debugPlugin.removeDebugEventListener(_processListener);

			_processListener = null;
		}

		setServerState(IServer.STATE_STOPPED);

		_watchedProjects.clear();
	}

	public GogoBundleDeployer createBundleDeployer() throws Exception {
		return ServerUtil.createBundleDeployer(_getPortalRuntime(), getServer());
	}

	public String getClassToLaunch() {
		PortalBundle portalBundle = _getPortalRuntime().getPortalBundle();

		return portalBundle.getMainClass();
	}

	@Override
	public IPath getDeployedPath(IModule[] module) {
		return null;
	}

	public IAdaptable getInfo() {
		return _info;
	}

	public Set<IProject> getWatchProject() {
		return _watchedProjects;
	}

	public void launchServer(ILaunch launch, String mode, IProgressMonitor monitor) throws CoreException {
		ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();

		if ("true".equals(launchConfiguration.getAttribute(ATTR_STOP, "false"))) {
			return;
		}

		IStatus status = _getPortalRuntime().validate();

		if ((status != null) && (status.getSeverity() == IStatus.ERROR)) {
			throw new CoreException(status);
		}

		setServerRestartState(false);
		setServerState(IServer.STATE_STARTING);
		setMode(mode);

		try {
			String url = "http://" + getServer().getHost();
			PortalBundle portalBundle = _getPortalRuntime().getPortalBundle();

			int port = Integer.parseInt(portalBundle.getHttpPort());

			if (port != 80) {
				url += ":" + port;
			}

			_ping = new PingThread(getServer(), url, -1, this);
		}
		catch (Exception e) {
			LiferayServerCore.logError("Can't ping for portal startup.");
		}
	}

	@Override
	public synchronized void publish(int kind, List<IModule[]> modules, IProgressMonitor monitor, IAdaptable info)
		throws CoreException {

		// save info

		_info = info;

		super.publish(kind, modules, monitor, info);

		_info = null;
	}

	@Override
	public void redeployModule(IModule[] module) throws CoreException {
		setModulePublishState(module, IServer.PUBLISH_STATE_FULL);

		IAdaptable info = new IAdaptable() {

			@Override
			@SuppressWarnings("unchecked")
			public Object getAdapter(Class adapter) {
				if (String.class.equals(adapter)) {
					return "user";
				}
				else if (IModule.class.equals(adapter)) {
					return module[0];
				}

				return null;
			}

		};

		List<IModule[]> modules = new ArrayList<>();

		modules.add(module);

		publish(IServer.PUBLISH_FULL, modules, null, info);
	}

	public void removeWatchProject(IProject project) {
		try {
			if (IServer.STATE_STARTED == getServer().getServerState()) {
				_watchedProjects.remove(project);
				_refreshSourceLookup();
			}
		}
		catch (CoreException ce) {
			LiferayServerCore.logError("Could not reinitialize source lookup director", ce);
		}
	}

	public void setModulePublishState2(IModule[] module, int state) {
		super.setModulePublishState(module, state);
	}

	public void setModuleState2(IModule[] modules, int state) {
		super.setModuleState(modules, state);
	}

	public void setServerStarted() {
		setServerState(IServer.STATE_STARTED);
	}

	@Override
	public void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy launch, IProgressMonitor monitor)
		throws CoreException {

		String existingProgArgs = launch.getAttribute(ATTR_PROGRAM_ARGUMENTS, (String)null);

		launch.setAttribute(
			ATTR_PROGRAM_ARGUMENTS, _mergeArguments(existingProgArgs, _getRuntimeStartProgArgs(), null));

		String existingVMArgs = launch.getAttribute(ATTR_VM_ARGUMENTS, (String)null);

		String[] configVMArgs = _getRuntimeStartVMArguments();

		launch.setAttribute(ATTR_VM_ARGUMENTS, _mergeArguments(existingVMArgs, configVMArgs, null));

		PortalRuntime portalRuntime = _getPortalRuntime();

		IVMInstall vmInstall = portalRuntime.getVMInstall();

		if (vmInstall != null) {
			IPath jreContainerPath = JavaRuntime.newJREContainerPath(vmInstall);

			launch.setAttribute(ATTR_JRE_CONTAINER_PATH, jreContainerPath.toPortableString());
		}

		IRuntimeClasspathEntry[] orgClasspath = JavaRuntime.computeUnresolvedRuntimeClasspath(launch);

		int orgClasspathSize = orgClasspath.length;

		List<IRuntimeClasspathEntry> oldCp = new ArrayList<>(orgClasspathSize);

		Collections.addAll(oldCp, orgClasspath);

		List<IRuntimeClasspathEntry> runCpEntries = portalRuntime.getRuntimeClasspathEntries();

		for (IRuntimeClasspathEntry cpEntry : runCpEntries) {
			_mergeClasspath(oldCp, cpEntry);
		}

		if (vmInstall != null) {
			try {
				IVMInstallType vmInstallType = vmInstall.getVMInstallType();

				String typeId = vmInstallType.getId();

				IPath typeIdPath = new Path(JavaRuntime.JRE_CONTAINER).append(typeId);

				IRuntimeClasspathEntry newJRECp = JavaRuntime.newRuntimeContainerClasspathEntry(
					typeIdPath.append(vmInstall.getName()), IRuntimeClasspathEntry.BOOTSTRAP_CLASSES);

				_replaceJREConatiner(oldCp, newJRECp);
			}
			catch (Exception e) {

				// ignore

			}

			File vmInstallLocation = vmInstall.getInstallLocation();

			IPath jrePath = new Path(vmInstallLocation.getAbsolutePath());

			if (jrePath != null) {
				IPath toolsPath = jrePath.append("lib/tools.jar");

				if (FileUtil.exists(toolsPath)) {
					IRuntimeClasspathEntry toolsJar = JavaRuntime.newArchiveRuntimeClasspathEntry(toolsPath);

					// Search for index to any existing tools.jar entry

					int toolsIndex;

					for (toolsIndex = 0; toolsIndex < oldCp.size(); toolsIndex++) {
						IRuntimeClasspathEntry entry = oldCp.get(toolsIndex);

						IPath entryPath = entry.getPath();

						String entryPathLastSegment = entryPath.lastSegment();

						if ((entry.getType() == IRuntimeClasspathEntry.ARCHIVE) &&
							entryPathLastSegment.equals("tools.jar")) {

							break;
						}
					}

					// If existing tools.jar found, replace in case it's different. Otherwise add.

					if (toolsIndex < oldCp.size()) {
						oldCp.set(toolsIndex, toolsJar);
					}
					else {
						_mergeClasspath(oldCp, toolsJar);
					}
				}
			}
		}

		List<String> cp = new ArrayList<>();

		for (IRuntimeClasspathEntry entry : oldCp) {
			try {
				IClasspathEntry classpathEntry = entry.getClasspathEntry();

				if (classpathEntry.getEntryKind() != IClasspathEntry.CPE_CONTAINER) {
					entry = new LiferayRuntimeClasspathEntry(classpathEntry);
				}

				cp.add(entry.getMemento());
			}
			catch (Exception e) {
				LiferayServerCore.logError("Could not resolve cp entry " + entry, e);
			}
		}

		launch.setAttribute(ATTR_CLASSPATH, cp);
		launch.setAttribute(ATTR_DEFAULT_CLASSPATH, Boolean.FALSE);
	}

	@Override
	public void startModule(IModule[] modules, IProgressMonitor monitor) throws CoreException {
		_startOrStopModules(modules, "start");
	}

	@Override
	public void stop(boolean force) {
		if (force) {
			terminate();

			return;
		}

		int state = getServer().getServerState();

		// If stopped or stopping, no need to run stop command again

		if ((state == IServer.STATE_STOPPED) || (state == IServer.STATE_STOPPING)) {
			return;
		}
		else if (state == IServer.STATE_STARTING) {
			terminate();

			return;
		}

		try {
			if (state != IServer.STATE_STOPPED) {
				setServerState(IServer.STATE_STOPPING);
			}

			ILaunchConfiguration launchConfig = ((Server)getServer()).getLaunchConfiguration(false, null);

			ILaunchConfigurationWorkingCopy wc = launchConfig.getWorkingCopy();

			String args = renderCommandLine(_getRuntimeStopProgArgs(), " ");

			// Remove JMX arguments if present

			String existingVMArgs = wc.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, (String)null);

			if (existingVMArgs.indexOf(_JMX_EXCLUDE_ARGS[0]) >= 0) {
				wc.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
					_mergeArguments(existingVMArgs, _getRuntimeStopVMArguments(), _JMX_EXCLUDE_ARGS));
			}
			else {
				wc.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
					_mergeArguments(existingVMArgs, _getRuntimeStopVMArguments(), null));
			}

			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, args);
			wc.setAttribute("org.eclipse.debug.ui.private", Boolean.TRUE);
			wc.setAttribute(ATTR_STOP, "true");

			wc.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
		}
		catch (Exception e) {
			LiferayServerCore.logError("Error stopping portal", e);
		}
	}

	@Override
	public void stopModule(IModule[] modules, IProgressMonitor monitor) throws CoreException {
		_startOrStopModules(modules, "stop");
	}

	protected static String renderCommandLine(String[] commandLine, String separator) {
		if ((commandLine == null) || (commandLine.length < 1)) {
			return "";
		}

		StringBuffer buf = new StringBuffer(commandLine[0]);

		for (int i = 1; i < commandLine.length; i++) {
			buf.append(separator);
			buf.append(commandLine[i]);
		}

		return buf.toString();
	}

	@Override
	protected void publishModule(int kind, int deltaKind, IModule[] modules, IProgressMonitor monitor)
		throws CoreException {

		// publishing is done by PortalPublishTask

		return;
	}

	@Override
	protected void publishServer(int kind, IProgressMonitor monitor) throws CoreException {
		setServerPublishState(IServer.PUBLISH_STATE_UNKNOWN);
	}

	protected void terminate() {
		if (getServer().getServerState() == IServer.STATE_STOPPED) {
			return;
		}

		try {
			setServerState(IServer.STATE_STOPPING);

			ILaunch launch = getServer().getLaunch();

			if (launch != null) {
				launch.terminate();
				cleanup();
			}
		}
		catch (Exception e) {
			LiferayServerCore.logError("Error killing the process", e);
		}
	}

	private int _getNextToken(String s, int start) {
		int i = start;
		int length = s.length();
		char lookFor = ' ';

		while (i < length) {
			char c = s.charAt(i);

			if (lookFor == c) {
				if (lookFor == '"') {
					return i + 1;
				}

				return i;
			}

			if (c == '"') {
				lookFor = '"';
			}

			i++;
		}

		return -1;
	}

	private PortalRuntime _getPortalRuntime() {
		PortalRuntime retval = null;

		IServer s = getServer();

		IRuntime runtime = s.getRuntime();

		if (runtime != null) {
			retval = (PortalRuntime)runtime.loadAdapter(PortalRuntime.class, null);
		}

		return retval;
	}

	private PortalServer _getPortalServer() {
		PortalServer retval = null;

		IServer s = getServer();

		if (s != null) {
			retval = (PortalServer)s.loadAdapter(PortalServer.class, null);
		}

		return retval;
	}

	private String[] _getRuntimeStartProgArgs() {
		PortalBundle portalBundle = _getPortalRuntime().getPortalBundle();

		return portalBundle.getRuntimeStartProgArgs();
	}

	private String[] _getRuntimeStartVMArguments() {
		boolean launchSetting = _getPortalServer().getLaunchSettings();
		IPath liferayHome = _getPortalRuntime().getLiferayHome();

		IPath portalExtPath = liferayHome.append("portal-ext.properties");

		if (!launchSetting) {
			File portalext = portalExtPath.toFile();

			if (_getPortalServer().getDeveloperMode()) {
				try {
					if (FileUtil.notExists(portalext)) {
						portalext.createNewFile();
					}

					PortalPropertiesConfiguration config = new PortalPropertiesConfiguration();

					try (InputStream in = Files.newInputStream(portalext.toPath())) {
						config.load(in);
					}

					String[] p = config.getStringArray("include-and-override");

					boolean existing = false;

					for (String prop : p) {
						if (prop.equals("portal-developer.properties")) {
							existing = true;

							break;
						}
					}

					if (!existing) {
						config.addProperty("include-and-override", "portal-developer.properties");
					}

					config.save(portalext);
				}
				catch (Exception e) {
					LiferayServerCore.logError(e);
				}
			}
			else if (FileUtil.exists(portalext)) {
				String contents = FileUtil.readContents(portalext, true);

				contents = contents.replace("include-and-override=portal-developer.properties", "");

				try {
					FileUtils.write(portalext, contents);
				}
				catch (IOException ioe) {
					LiferayServerCore.logError(ioe);
				}
			}
		}

		List<String> retval = new ArrayList<>();

		Collections.addAll(retval, _getPortalServer().getMemoryArgs());

		PortalBundle portalBundle = _getPortalRuntime().getPortalBundle();

		Collections.addAll(retval, portalBundle.getRuntimeStartVMArgs());

		return retval.toArray(new String[0]);
	}

	private String[] _getRuntimeStopProgArgs() {
		PortalBundle portalBundle = _getPortalRuntime().getPortalBundle();

		return portalBundle.getRuntimeStopProgArgs();
	}

	private String[] _getRuntimeStopVMArguments() {
		List<String> retval = new ArrayList<>();

		String[] memoryArgs = _getPortalServer().getMemoryArgs();

		if (memoryArgs != null) {
			Collections.addAll(retval, memoryArgs);
		}

		PortalBundle portalBundle = _getPortalRuntime().getPortalBundle();

		Collections.addAll(retval, portalBundle.getRuntimeStopVMArgs());

		return retval.toArray(new String[0]);
	}

	private String _mergeArguments(String orgArgsString, String[] newArgs, String[] excludeArgs) {
		String retval = null;

		if (ListUtil.isEmpty(newArgs) && ListUtil.isEmpty(excludeArgs)) {
			retval = orgArgsString;
		}
		else {
			retval = orgArgsString == null ? "" : orgArgsString;

			String xbootClasspath = "";

			// replace and null out all newArgs that already exist

			int size = newArgs.length;

			for (int i = 0; i < size; i++) {
				if (newArgs[i].startsWith("-Xbootclasspath")) {
					xbootClasspath = xbootClasspath + newArgs[i] + " ";

					newArgs[i] = null;

					continue;
				}

				int ind = newArgs[i].indexOf(" ");
				int ind2 = newArgs[i].indexOf("=");

				if ((ind >= 0) && ((ind2 == -1) || (ind < ind2))) {

					// -a bc style

					int index = retval.indexOf(newArgs[i].substring(0, ind + 1));

					if ((index == 0) || ((index > 0) && Character.isWhitespace(retval.charAt(index - 1)))) {
						newArgs[i] = null;
					}
				}
				else if (ind2 >= 0) {

					// a =b style

					int index = retval.indexOf(newArgs[i].substring(0, ind2 + 1));

					if ((index == 0) || ((index > 0) && Character.isWhitespace(retval.charAt(index - 1)))) {
						newArgs[i] = null;
					}
				}
				else {

					// abc style

					int index = retval.indexOf(newArgs[i]);

					if ((index == 0) || ((index > 0) && Character.isWhitespace(retval.charAt(index - 1)))) {
						newArgs[i] = null;
					}
				}
			}

			// remove excluded arguments

			if (ListUtil.isNotEmpty(excludeArgs)) {
				for (int i = 0; i < excludeArgs.length; i++) {
					int ind = excludeArgs[i].indexOf(" ");
					int ind2 = excludeArgs[i].indexOf("=");

					if ((ind >= 0) && ((ind2 == -1) || (ind < ind2))) {

						// -a bc style

						int index = retval.indexOf(excludeArgs[i].substring(0, ind + 1));

						if ((index == 0) || ((index > 0) && Character.isWhitespace(retval.charAt(index - 1)))) {

							// remove

							String s = retval.substring(0, index);
							int index2 = _getNextToken(retval, index + ind + 1);

							if (index2 >= 0) {

								// If remainder will become the first argument, remove leading blanks

								while ((index2 < retval.length()) &&
									   Character.isWhitespace(retval.charAt(index2)))index2 += 1;
								retval = s + retval.substring(index2);
							}
							else retval = s;
						}
					}
					else if (ind2 >= 0) {

						// a =b style

						int index = retval.indexOf(excludeArgs[i].substring(0, ind2 + 1));

						if ((index == 0) || ((index > 0) && Character.isWhitespace(retval.charAt(index - 1)))) {

							// remove

							String s = retval.substring(0, index);
							int index2 = _getNextToken(retval, index);

							if (index2 >= 0) {

								// If remainder will become the first argument, remove leading blanks

								while ((index2 < retval.length()) &&
									   Character.isWhitespace(retval.charAt(index2)))index2 += 1;
								retval = s + retval.substring(index2);
							}
							else retval = s;
						}
					}
					else {

						// abc style

						int index = retval.indexOf(excludeArgs[i]);

						if ((index == 0) || ((index > 0) && Character.isWhitespace(retval.charAt(index - 1)))) {

							// remove

							String s = retval.substring(0, index);
							int index2 = _getNextToken(retval, index);

							if (index2 >= 0) {

								// Remove leading blanks

								while ((index2 < retval.length()) &&
									   Character.isWhitespace(retval.charAt(index2)))index2 += 1;
								retval = s + retval.substring(index2);
							}
							else {
								retval = s;
							}
						}
					}
				}
			}

			// add remaining vmargs to the end

			for (int i = 0; i < size; i++) {
				if (newArgs[i] != null) {
					if ((retval.length() > 0) && !retval.endsWith(" ")) {
						retval += " ";
					}

					retval += newArgs[i];
				}
			}

			if (!CoreUtil.isNullOrEmpty(xbootClasspath)) {

				// delete xbootclasspath

				int xbootIndex = retval.lastIndexOf("-Xbootclasspath");

				while (xbootIndex != -1) {
					String head = retval.substring(0, xbootIndex);

					int tailIndex = _getNextToken(retval, xbootIndex);

					String tail = retval.substring(tailIndex == retval.length() ? retval.length() : tailIndex + 1);

					retval = head + tail;

					xbootIndex = retval.lastIndexOf("-Xbootclasspath");
				}

				retval = retval + " " + xbootClasspath;
			}
		}

		return retval;
	}

	private void _mergeClasspath(List<IRuntimeClasspathEntry> oldCpEntries, IRuntimeClasspathEntry cpEntry) {
		for (IRuntimeClasspathEntry oldCpEntry : oldCpEntries) {
			IPath oldCpEntryPath = oldCpEntry.getPath();

			if (oldCpEntryPath.equals(cpEntry.getPath())) {
				return;
			}
		}

		oldCpEntries.add(cpEntry);
	}

	private void _refreshSourceLookup() throws CoreException {
		ILaunch launch = getServer().getLaunch();
		ILaunchConfiguration launchConfiguration = getServer().getLaunchConfiguration(false, new NullProgressMonitor());

		if (launchConfiguration != null) {
			AbstractSourceLookupDirector sourceLocator = (AbstractSourceLookupDirector)launch.getSourceLocator();

			String memento = launchConfiguration.getAttribute(
				ILaunchConfiguration.ATTR_SOURCE_LOCATOR_MEMENTO, (String)null);

			if (memento != null) {
				sourceLocator.initializeFromMemento(memento);
			}
			else {
				sourceLocator.initializeDefaults(launchConfiguration);
			}
		}
	}

	private void _replaceJREConatiner(List<IRuntimeClasspathEntry> oldCp, IRuntimeClasspathEntry newJRECp) {
		int size = oldCp.size();

		for (int i = 0; i < size; i++) {
			IRuntimeClasspathEntry entry2 = oldCp.get(i);

			IPath entry2Path = entry2.getPath();

			IPath entry2PathSeg2 = entry2Path.uptoSegment(2);

			if (entry2PathSeg2.isPrefixOf(newJRECp.getPath())) {
				oldCp.set(i, newJRECp);

				return;
			}
		}

		oldCp.add(0, newJRECp);
	}

	private void _startOrStopModules(IModule[] modules, String action) {
		for (IModule module : modules) {
			IProject project = module.getProject();

			if (project == null) {
				continue;
			}

			IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

			if (bundleProject != null) {
				try {
					String symbolicName = bundleProject.getSymbolicName();

					GogoBundleDeployer helper = new GogoBundleDeployer();

					long bundleId = helper.getBundleId(symbolicName);

					if (bundleId > 0) {
						if (action.equals("start")) {
							String error = helper.start(bundleId);

							if (error == null) {
								setModuleState(new IModule[] {module}, IServer.STATE_STARTED);
							}
							else {
								LiferayServerCore.logError("Unable to start this bundle");
							}
						}
						else if (action.equals("stop")) {
							String error = helper.stop(bundleId);

							if (error == null) {
								setModuleState(new IModule[] {module}, IServer.STATE_STOPPED);
							}
							else {
								LiferayServerCore.logError("Unable to stop this bundle");
							}
						}
					}
				}
				catch (Exception e) {
					LiferayServerCore.logError("Unable to " + action + " module", e);
				}
			}
		}
	}

	private static final String[] _JMX_EXCLUDE_ARGS = {
		"-Dcom.sun.management.jmxremote", "-Dcom.sun.management.jmxremote.port=", "-Dcom.sun.management.jmxremote.ssl=",
		"-Dcom.sun.management.jmxremote.authenticate="
	};

	private IAdaptable _info;
	private transient PingThread _ping = null;
	private transient IDebugEventSetListener _processListener;
	private Set<IProject> _watchedProjects;

}