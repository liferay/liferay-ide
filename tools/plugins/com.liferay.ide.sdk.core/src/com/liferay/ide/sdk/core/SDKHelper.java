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

package com.liferay.ide.sdk.core;

import com.liferay.ide.core.LiferayRuntimeClasspathEntry;
import com.liferay.ide.core.util.LaunchHelper;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.RuntimeClasspathModel;
import com.liferay.ide.core.util.StringPool;

import java.io.File;

import java.util.Map;

import org.eclipse.ant.launching.IAntLaunchConstants;
import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class SDKHelper extends LaunchHelper {

	public static final String ANT_CLASSPATH_PROVIDER = "org.eclipse.ant.ui.AntClasspathProvider";

	public static final String ANT_LAUNCH_CONFIG_TYPE_ID = IAntLaunchConstants.ID_ANT_LAUNCH_CONFIGURATION_TYPE;

	public SDKHelper(SDK sdk) {
		super(ANT_LAUNCH_CONFIG_TYPE_ID);

		this.sdk = sdk;

		setLaunchSync(true);
		setLaunchInBackground(true);
		setLaunchCaptureInConsole(true);
		setLaunchIsPrivate(true);
	}

	public SDKHelper(SDK sdk, IProgressMonitor monitor) {
		this(sdk);

		_monitor = monitor;
	}

	public ILaunchConfiguration createLaunchConfiguration(
			IPath buildFile, String targets, Map<String, String> properties, boolean separateJRE, String workingDir)
		throws CoreException {

		ILaunchConfigurationWorkingCopy launchConfig = super.createLaunchConfiguration();

		launchConfig.setAttribute(IExternalToolConstants.ATTR_LOCATION, buildFile.toOSString());

		launchConfig.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, workingDir);

		launchConfig.setAttribute(IAntLaunchConstants.ATTR_ANT_TARGETS, targets);

		launchConfig.setAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT, Boolean.TRUE);

		IPath sdkPluginLocation = SDKCorePlugin.getDefault().getStateLocation();

		launchConfig.setAttribute(
			"org.eclipse.debug.ui.ATTR_CAPTURE_IN_FILE", sdkPluginLocation.append("sdk.log").toOSString());

		launchConfig.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID, "org.eclipse.ant.ui.remoteAntProcessFactory");

		launchConfig.setAttribute(IAntLaunchConstants.ATTR_ANT_PROPERTIES, properties);
		launchConfig.setAttribute(IAntLaunchConstants.ATTR_ANT_PROPERTY_FILES, (String)null);

		if (separateJRE) {
			launchConfig.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, IAntLaunchConstants.MAIN_TYPE_NAME);
			launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, _getVMArgumentsAttr());
			launchConfig.setAttribute(IAntLaunchConstants.ATTR_DEFAULT_VM_INSTALL, Boolean.TRUE);
		}

		return launchConfig;
	}

	@Override
	public String getClasspathProviderAttributeValue() {
		return SDKClasspathProvider.ID;
	}

	@Override
	public String getNewLaunchConfigurationName() {
		StringBuffer buffer = new StringBuffer();

		if (this.sdk.getName() != null) {
			buffer.append(this.sdk.getName());
			buffer.append(' ');
		}

		if (currentBuildFile != null) {
			buffer.append(currentBuildFile.lastSegment());
		}

		if (currentTargets != null) {
			buffer.append(" [");
			buffer.append(currentTargets);
			buffer.append("]");
		}

		return buffer.toString();
	}

	public void runTarget(
			IPath buildFile, String targets, Map<String, String> properties, boolean separateJRE, String workingDir)
		throws CoreException {

		if (isLaunchRunning()) {
			throw new IllegalStateException("Existing launch in progress");
		}

		currentBuildFile = buildFile;

		currentTargets = targets;

		ILaunchConfiguration launchConfig = createLaunchConfiguration(
			buildFile, targets, properties, separateJRE, workingDir);

		launch(launchConfig, ILaunchManager.RUN_MODE, _monitor);

		currentBuildFile = null;

		currentTargets = null;
	}

	public void setVMArgs(String[] vmargs) {
		_additionalVMArgs = vmargs;
	}

	@Override
	protected void addUserEntries(RuntimeClasspathModel model) throws CoreException {
		IPath[] antLibs = sdk.getAntLibraries();

		for (IPath antLib : antLibs) {
			if (antLib.toFile().exists()) {
				model.addEntry(
					RuntimeClasspathModel.USER,
					new LiferayRuntimeClasspathEntry(JavaCore.newLibraryEntry(antLib.makeAbsolute(), null, null)));
			}
		}

		// IDE-862 need to add Eclipse's own jdt.core that contains the necessary classes.

		try {
			File bundleFile = FileLocator.getBundleFile(JavaCore.getPlugin().getBundle());

			if (bundleFile.exists()) {
				model.addEntry(
					RuntimeClasspathModel.USER,
					new LiferayRuntimeClasspathEntry(
						JavaCore.newLibraryEntry(new Path(bundleFile.getAbsolutePath()), null, null)));
			}
		}
		catch (Exception e) {
		}
	}

	protected IPath currentBuildFile;
	protected String currentTargets;
	protected SDK sdk;

	private String _getVMArgumentsAttr() {
		StringBuffer args = new StringBuffer("-Xmx768m");

		if (ListUtil.isNotEmpty(_additionalVMArgs)) {
			for (String vmArg : _additionalVMArgs) {
				args.append(StringPool.SPACE + vmArg);
			}
		}

		return args.toString();
	}

	private String[] _additionalVMArgs;
	private IProgressMonitor _monitor;

}