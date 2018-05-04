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

package com.liferay.ide.ui.workspace;

import com.liferay.ide.ui.dialog.ChooseWorkspaceWithPreferenceDialog;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.ide.ChooseWorkspaceData;
import org.eclipse.ui.internal.ide.ChooseWorkspaceDialog;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class LaunchWorkspaceHandler extends AbstractHandler {

	public static final String COMMAND_ID = "com.liferay.ide.ui.workspace.launchWorkspace";

	public static final String PARAM_LIFERAY_7_SDK_DIR = "liferay7SDKDir";

	public static final String PARAM_WORKSPACE_LOCATION = "workspaceLocation";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String liferay7SDKDir = event.getParameter(PARAM_LIFERAY_7_SDK_DIR);

		String workspaceLocation = event.getParameter(PARAM_WORKSPACE_LOCATION);

		Location installLocation = Platform.getInstallLocation();

		if (workspaceLocation == null) {
			ChooseWorkspaceData chooseWorkspaceData = new ChooseWorkspaceData(installLocation.getURL());

			ChooseWorkspaceDialog chooseWorkspaceDialog = new ChooseWorkspaceWithPreferenceDialog(
				Display.getDefault().getActiveShell(), chooseWorkspaceData, true, true);

			if (chooseWorkspaceDialog.open() == IDialogConstants.OK_ID) {
				workspaceLocation = chooseWorkspaceData.getSelection();
			}
			else {
				return null;
			}
		}

		List<String> commands = new ArrayList<>();

		File launchDir = new File(installLocation.getURL().getFile());

		File launcher;

		if (launchDir.exists()) {
			switch (Platform.getOS()) {
				case Platform.OS_MACOSX:
					launcher = launchDir.getParentFile().getParentFile();

					break;
				default:
					launcher = new File(System.getProperty("eclipse.launcher"));
			}
		}
		else {
			launcher = null;
		}

		if ((launcher != null) && launcher.exists()) {
			switch (Platform.getOS()) {
				case Platform.OS_MACOSX:
					commands.add("open");
					commands.add("-n");
					commands.add(launcher.getAbsolutePath());
					commands.add("--args");
					commands.add("-data");
					commands.add(workspaceLocation);

					break;

				case Platform.OS_LINUX:
					commands.add("/bin/bash");
					commands.add("-c");
					commands.add("''./" + launcher.getName() + " -data \"" + workspaceLocation + "\"''");

					break;

				case Platform.OS_WIN32:
					commands.add("cmd");
					commands.add("/c");
					commands.add(launcher.getName());
					commands.add("-data");
					commands.add(workspaceLocation);

					break;
			}

			if (liferay7SDKDir != null) {
				commands.add("-vmargs");
				commands.add("-Dliferay7.sdk.dir=\"" + liferay7SDKDir + "\"");
			}
		}
		else {
			throw new ExecutionException("Unable to find Eclipse launcher.");
		}

		if (launchDir.isDirectory()) {
			ProcessBuilder processBuilder = new ProcessBuilder(commands);

			processBuilder.directory(launchDir);

			Map<String, String> environment = processBuilder.environment();

			environment.putAll(System.getenv());

			try {
				processBuilder.start();
			}
			catch (IOException ioe) {
				throw new ExecutionException("Unable to start Eclipse process.", ioe);
			}
		}

		return null;
	}

}