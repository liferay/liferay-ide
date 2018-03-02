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

package com.liferay.ide.core.util;

import com.liferay.ide.core.LiferayRuntimeClasspathEntry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class LaunchHelper implements IDebugEventSetListener {

	public LaunchHelper() {
	}

	public LaunchHelper(String launchConfigTypeId) {
		this.launchConfigTypeId = launchConfigTypeId;
	}

	public ILaunchConfigurationWorkingCopy createLaunchConfiguration() throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();

		ILaunchConfigurationType type = manager.getLaunchConfigurationType(launchConfigTypeId);

		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

		String name = launchManager.generateLaunchConfigurationName(getNewLaunchConfigurationName());

		ILaunchConfigurationWorkingCopy launchConfig = type.newInstance(null, name);

		launchConfig.setAttribute("org.eclipse.debug.ui.ATTR_CAPTURE_IN_CONSOLE", isLaunchCaptureInConsole());
		launchConfig.setAttribute("org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND", isLaunchInBackground());
		launchConfig.setAttribute("org.eclipse.debug.ui.ATTR_PRIVATE", isLaunchIsPrivate());

		IRuntimeClasspathEntry[] classpath = getClasspath(launchConfig);

		List<String> mementos = new ArrayList<>(classpath.length);

		for (int i = 0; i < classpath.length; i++) {
			IRuntimeClasspathEntry entry = classpath[i];

			mementos.add(entry.getMemento());
		}

		launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, Boolean.FALSE);
		launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, mementos);

		if (getMainClass() != null) {
			launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, getMainClass());
		}

		if (ListUtil.isNotEmpty(launchArgs)) {
			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < launchArgs.length; i++) {
				sb.append("\"");
				sb.append(launchArgs[i]);
				sb.append("\" ");
			}

			launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, sb.toString());
		}

		return launchConfig;
	}

	public String[] getLaunchArgs() {
		return launchArgs;
	}

	public String getMainClass() {
		return mainClass;
	}

	public String getMode() {
		return mode;
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			Object source = event.getSource();

			if (source instanceof IProcess) {
				if (((IProcess)source).getLaunch().equals(runningLaunch) && (event.getKind() == DebugEvent.TERMINATE)) {
					synchronized (this) {
						DebugPlugin.getDefault().removeDebugEventListener(this);

						// launchRunning = false;

					}
				}
			}
		}
	}

	public boolean isLaunchCaptureInConsole() {
		return launchCaptureInConsole;
	}

	public boolean isLaunchInBackground() {
		return launchInBackground;
	}

	public boolean isLaunchIsPrivate() {
		return launchIsPrivate;
	}

	public boolean isLaunchRunning() {
		if (runningLaunch == null) {
			return false;
		}

		if (runningLaunch.isTerminated()) {
			return false;
		}

		IProcess[] processes = runningLaunch.getProcesses();

		if (ListUtil.isEmpty(processes)) {
			return false;
		}

		IProcess process = processes[0];

		if ((process == null) || process.isTerminated()) {
			return false;
		}

		return true;
	}

	public boolean isLaunchSync() {
		return launchSync;
	}

	public int launch(ILaunchConfiguration config, String mode, IProgressMonitor monitor) throws CoreException {
		if (config == null) {
			throw new IllegalArgumentException("Launch config cannot be null");
		}

		if (isLaunchSync()) {
			DebugPlugin.getDefault().addDebugEventListener(this);
		}

		ILaunch launch = config.launch(mode, new NullProgressMonitor());

		IProcess process = launch.getProcesses().length > 0 ? launch.getProcesses()[0] : null;

		if (isLaunchSync()) {
			runningLaunch = launch;

			try {
				while (isLaunchRunning()) {
					if ((monitor != null) && monitor.isCanceled() && !launch.isTerminated()) {
						if (process != null) {
							process.terminate();
						}

						launch.terminate();
					}
					else {
						Thread.sleep(100);
					}
				}
			}
			catch (InterruptedException ie) {
				runningLaunch.terminate();
			}
			finally {
				runningLaunch = null;
			}
		}

		if (isLaunchSync() && (process != null)) {
			return process.getExitValue();
		}

		return 0;
	}

	public int launch(IProgressMonitor monitor) throws CoreException {
		ILaunchConfigurationWorkingCopy config = createLaunchConfiguration();

		return launch(config, mode, monitor);
	}

	public void setLaunchArgs(String[] launchArgs) {
		this.launchArgs = launchArgs;
	}

	public void setLaunchCaptureInConsole(boolean launchCaptureInConsole) {
		this.launchCaptureInConsole = launchCaptureInConsole;
	}

	public void setLaunchInBackground(boolean launchInBackground) {
		this.launchInBackground = launchInBackground;
	}

	public void setLaunchIsPrivate(boolean launchIsPrivate) {
		this.launchIsPrivate = launchIsPrivate;
	}

	public void setLaunchSync(boolean sync) {
		launchSync = sync;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	protected void addUserEntries(RuntimeClasspathModel model) throws CoreException {
	}

	protected IRuntimeClasspathEntry[] getClasspath(ILaunchConfigurationWorkingCopy config) throws CoreException {
		RuntimeClasspathModel model = new RuntimeClasspathModel(config);

		config.setAttribute(
			IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, getClasspathProviderAttributeValue());

		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, Boolean.TRUE);

		IRuntimeClasspathEntry[] defaultEntries = JavaRuntime.computeUnresolvedRuntimeClasspath(config);

		IRuntimeClasspathEntry entry;

		for (int i = 0; i < defaultEntries.length; i++) {
			entry = defaultEntries[i];

			switch (entry.getClasspathProperty()) {
				case IRuntimeClasspathEntry.USER_CLASSES:
					model.addEntry(RuntimeClasspathModel.USER, entry);
					break;

				default:
					break;
			}
		}

		addUserEntries(model);

		return getClasspathEntries(model);
	}

	protected IRuntimeClasspathEntry[] getClasspathEntries(RuntimeClasspathModel model) {
		IClasspathEntry[] users = model.getEntries(RuntimeClasspathModel.USER, model.getConfig());

		List<IRuntimeClasspathEntry> entries = new ArrayList<>();

		IRuntimeClasspathEntry entry;

		IClasspathEntry userEntry;

		for (int i = 0; i < users.length; i++) {
			userEntry = users[i];

			entry = null;

			if (userEntry instanceof IRuntimeClasspathEntry) {
				entry = (IRuntimeClasspathEntry)users[i];
			}
			else if (userEntry instanceof IClasspathEntry) {
				entry = new LiferayRuntimeClasspathEntry(userEntry);
			}

			if (entry != null) {
				entry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

				entries.add(entry);
			}
		}

		return entries.toArray(new IRuntimeClasspathEntry[entries.size()]);
	}

	protected String getClasspathProviderAttributeValue() {
		return null;
	}

	protected IVMInstall getDefaultVMInstall(ILaunchConfiguration config) {
		IVMInstall defaultVMInstall;

		try {
			defaultVMInstall = JavaRuntime.computeVMInstall(config);
		}
		catch (CoreException ce) {

			// core exception thrown for non-Java project

			defaultVMInstall = JavaRuntime.getDefaultVMInstall();
		}

		return defaultVMInstall;
	}

	protected String getNewLaunchConfigurationName() {
		Class<?> clazz = getClass();

		return clazz.getName();
	}

	protected String[] launchArgs = new String[0];
	protected boolean launchCaptureInConsole = true;
	protected String launchConfigTypeId;
	protected boolean launchInBackground = true;
	protected boolean launchIsPrivate = true;
	protected boolean launchSync = true;
	protected String mainClass;
	protected String mode = ILaunchManager.RUN_MODE;
	protected ILaunch runningLaunch;

}