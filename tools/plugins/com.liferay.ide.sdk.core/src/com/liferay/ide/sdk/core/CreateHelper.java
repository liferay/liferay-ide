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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.LaunchHelper;
import com.liferay.ide.core.util.StringUtil;

import java.io.File;

import java.util.ArrayList;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

/**
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class CreateHelper extends LaunchHelper {

	public static final String PROGRAM_CONFIG_TYPE_ID = IExternalToolConstants.ID_PROGRAM_LAUNCH_CONFIGURATION_TYPE;

	public CreateHelper(SDK sdk) {
		super(PROGRAM_CONFIG_TYPE_ID);

		this.sdk = sdk;

		setLaunchSync(true);
		setLaunchInBackground(true);
		setLaunchCaptureInConsole(true);
		setLaunchIsPrivate(true);
	}

	public CreateHelper(SDK sdk, IProgressMonitor monitor) {
		this(sdk);

		_monitor = monitor;
	}

	public ILaunchConfiguration createLaunchConfiguration(
			IPath buildFile, ArrayList<String> arguments, String workingDir)
		throws CoreException {

		StringBuffer sb = new StringBuffer();

		for (String argument : arguments) {
			sb.append("\"");
			sb.append(argument);
			sb.append("\" ");
			sb.append("");
		}

		ILaunchConfigurationWorkingCopy launchConfig = super.createLaunchConfiguration();

		SDKCorePlugin sdkCorePlugin = SDKCorePlugin.getDefault();

		IPath sdkPluginLocation = sdkCorePlugin.getStateLocation();

		launchConfig.setAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT, Boolean.TRUE);
		launchConfig.setAttribute(IExternalToolConstants.ATTR_LOCATION, buildFile.toOSString());
		launchConfig.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, StringUtil.trim(sb.toString()));
		launchConfig.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, workingDir);
		launchConfig.setAttribute(
			"org.eclipse.debug.ui.ATTR_CAPTURE_IN_FILE", FileUtil.toOSString(sdkPluginLocation.append("sdk.log")));

		return launchConfig;
	}

	public void runTarget(IPath createFile, ArrayList<String> arguments, String workingDir) throws CoreException {
		if (isLaunchRunning()) {
			throw new IllegalStateException("Existing launch in progress");
		}

		currentCreateFile = createFile;

		File file = currentCreateFile.toFile();

		file.setExecutable(true);

		ILaunchConfiguration launchConfig = createLaunchConfiguration(createFile, arguments, workingDir);

		int exitValue = launch(launchConfig, ILaunchManager.RUN_MODE, _monitor);

		if (exitValue > 0) {
			throw new CoreException(
				SDKCorePlugin.createErrorStatus("create script failed with error code " + exitValue));
		}

		currentCreateFile = null;
	}

	protected IPath currentCreateFile;
	protected SDK sdk;

	private IProgressMonitor _monitor;

}