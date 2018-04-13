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

package com.liferay.ide.server.ui.util;

import com.liferay.ide.core.util.CoreUtil;

import java.io.File;
import java.io.IOException;

import java.net.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.Util;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

/**
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class ServerUIUtil {

	public static String getSystemExplorerCommand(File file) throws IOException {
		String retval = null;

		IDEWorkbenchPlugin ideWorkbenchPlugin = IDEWorkbenchPlugin.getDefault();

		IPreferenceStore prefStore = ideWorkbenchPlugin.getPreferenceStore();

		String command = prefStore.getString(IDEInternalPreferences.WORKBENCH_SYSTEM_EXPLORER);

		if (!CoreUtil.isNullOrEmpty(command)) {
			command = Util.replaceAll(command, _VARIABLE_RESOURCE, quotePath(file.getCanonicalPath()));

			File canonicalFile = file.getCanonicalFile();

			URI uri = canonicalFile.toURI();

			command = Util.replaceAll(command, _VARIABLE_RESOURCE_URI, uri.toString());

			File parent = file.getParentFile();

			if (parent != null) {
				retval = Util.replaceAll(command, _VARIABLE_FOLDER, quotePath(parent.getCanonicalPath()));
			}
		}

		return retval;
	}

	public static void openFileInSystemExplorer(IPath path) throws IOException {
		String launchCmd = getSystemExplorerCommand(path.toFile());

		if (!CoreUtil.isNullOrEmpty(launchCmd)) {
			File file = path.toFile();

			if (file.isFile()) {
				path = path.removeLastSegments(1);
			}

			openInSystemExplorer(launchCmd, path.toFile());
		}
	}

	public static void openInSystemExplorer(String systemCommand, File file) throws IOException {
		if (Util.isLinux() || Util.isMac()) {
			Runtime runtime = Runtime.getRuntime();

			runtime.exec(new String[] {"/bin/sh", "-c", systemCommand}, null, file);
		}
		else {
			Runtime runtime = Runtime.getRuntime();

			runtime.exec(systemCommand, null, file);
		}
	}

	public static String quotePath(String path) {
		if (CoreUtil.isLinux() || CoreUtil.isMac()) {
			path = path.replaceAll("[\"$`]", "\\\\$0");
		}

		return path;
	}

	private static final String _VARIABLE_FOLDER = "${selected_resource_parent_loc}";

	private static final String _VARIABLE_RESOURCE = "${selected_resource_loc}";

	private static final String _VARIABLE_RESOURCE_URI = "${selected_resource_uri}";

}