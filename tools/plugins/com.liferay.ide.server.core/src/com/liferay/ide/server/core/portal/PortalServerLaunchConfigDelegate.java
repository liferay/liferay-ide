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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * @author Gregory Amerson
 * @author Charles Wu
 * @author Simon Jiang
 */
public class PortalServerLaunchConfigDelegate extends AbstractJavaLaunchConfigurationDelegate {

	public static final String ID = "com.liferay.ide.server.portal.launch";

	public PortalServerLaunchConfigDelegate() {
		Method allowAdvancedSourcelookupMethod = _getSuperClassMethod("allowAdvancedSourcelookup", new Class<?>[0]);

		if (allowAdvancedSourcelookupMethod != null) {
			try {
				allowAdvancedSourcelookupMethod.invoke(this);

				_allowAdvancedSourceLookup = true;
			}
			catch (Exception e) {
			}
		}
	}

	@Override
	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor)
		throws CoreException {

		IServer server = ServerUtil.getServer(config);

		if (server != null) {
			IRuntime runtime = server.getRuntime();

			if (runtime == null) {
				throw new CoreException(LiferayServerCore.createErrorStatus("Server runtime is invalid."));
			}

			PortalRuntime portalRuntime = (PortalRuntime)runtime.loadAdapter(PortalRuntime.class, monitor);

			if (portalRuntime == null) {
				throw new CoreException(LiferayServerCore.createErrorStatus("Server portal runtime is invalid."));
			}

			IStatus status = portalRuntime.validate();

			if (status.getSeverity() == IStatus.ERROR) {
				throw new CoreException(status);
			}

			_launchServer(server, config, mode, launch, monitor);
		}
	}

	private Method _getSuperClassMethod(String methodName, Class<?>... parameterTypes) {
		Method superClassMethod = null;

		try {
			Class<?> superclass = getClass().getSuperclass();

			superClassMethod = superclass.getDeclaredMethod(methodName, parameterTypes);
		}
		catch (Exception e) {
		}

		return superClassMethod;
	}

	private void _launchServer(
			IServer server, ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor)
		throws CoreException {

		IVMInstall vm = verifyVMInstall(config);

		IVMRunner runner;

		if (vm.getVMRunner(mode) != null) {
			runner = vm.getVMRunner(mode);
		}
		else {
			runner = vm.getVMRunner(ILaunchManager.RUN_MODE);
		}

		File workingDir = verifyWorkingDirectory(config);

		String workingDirPath = (workingDir != null) ? workingDir.getAbsolutePath() : null;

		String progArgs = getProgramArguments(config);
		String vmArgs = getVMArguments(config);
		String[] envp = getEnvironment(config);

		ExecutionArguments execArgs = new ExecutionArguments(vmArgs, progArgs);

		Map<String, Object> vmAttributesMap = getVMSpecificAttributesMap(config);

		PortalServerBehavior portalServer = (PortalServerBehavior)server.loadAdapter(
			PortalServerBehavior.class, monitor);

		String classToLaunch = portalServer.getClassToLaunch();

		String[][] classpathAndModulepath = getClasspathAndModulepath(config);

		VMRunnerConfiguration runConfig = new VMRunnerConfiguration(classToLaunch, classpathAndModulepath[0]);

		runConfig.setProgramArguments(execArgs.getProgramArgumentsArray());

		if (_allowAdvancedSourceLookup) {
			try {
				Method getVMArgumentsMethod = _getSuperClassMethod(
					"getVMArguments", new Class<?>[] {ILaunchConfiguration.class, String.class});

				if (getVMArgumentsMethod != null) {
					List<String> vmArguments = new ArrayList<>();
					String vmArgumentResult = (String)getVMArgumentsMethod.invoke(this, new Object[] {config, mode});

					Collections.addAll(vmArguments, DebugPlugin.parseArguments(vmArgumentResult));

					Collections.addAll(vmArguments, execArgs.getVMArgumentsArray());

					runConfig.setVMArguments(vmArguments.toArray(new String[vmArguments.size()]));
				}
			}
			catch (Exception e) {
			}
		}
		else {
			runConfig.setVMArguments(execArgs.getVMArgumentsArray());
		}

		runConfig.setWorkingDirectory(workingDirPath);
		runConfig.setEnvironment(envp);
		runConfig.setVMSpecificAttributesMap(vmAttributesMap);

		String[] bootpath = getBootpath(config);

		if (ListUtil.isNotEmpty(bootpath)) {
			runConfig.setBootClassPath(bootpath);
		}

		portalServer.launchServer(launch, mode, monitor);

		server.addServerListener(
			new IServerListener() {

				@Override
				public void serverChanged(ServerEvent event) {
					if (!config.exists()) {
						LiferayServerCore.logError("Launch configuration " + config.getName() + " does not exist.");

						return;
					}

					if ((event.getKind() & ServerEvent.MODULE_CHANGE) > 0) {
						AbstractSourceLookupDirector sourceLocator =
							(AbstractSourceLookupDirector)launch.getSourceLocator();

						try {
							String memento = config.getAttribute(
								ILaunchConfiguration.ATTR_SOURCE_LOCATOR_MEMENTO, (String)null);

							if (memento != null) {
								sourceLocator.initializeFromMemento(memento);
							}
							else {
								sourceLocator.initializeDefaults(config);
							}
						}
						catch (CoreException ce) {
							LiferayServerCore.logError("Could not reinitialize source lookup director", ce);
						}
					}
					else if (((event.getKind() & ServerEvent.SERVER_CHANGE) > 0) &&
							 (event.getState() == IServer.STATE_STOPPED)) {

						server.removeServerListener(this);
					}
					else if (((event.getKind() & ServerEvent.SERVER_CHANGE) > 0) &&
							 (event.getState() == IServer.STATE_STOPPING)) {

						IJobManager jobManager = Job.getJobManager();

						Job[] jobs = jobManager.find(null);

						for (Job job : jobs) {
							if (job.getProperty(ILiferayServer.LIFERAY_SERVER_JOB) != null) {
								job.cancel();
							}
						}
					}
				}

			});

		try {
			runner.run(runConfig, launch, monitor);
			portalServer.addProcessListener(launch.getProcesses()[0]);
		}
		catch (Exception e) {
			portalServer.cleanup();
		}
	}

	private boolean _allowAdvancedSourceLookup = false;

}