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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.ui.LiferayUIPlugin;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.internal.preferences.WorkbenchSettingsTransfer;

/**
 * @author Andy Wu
 */
@SuppressWarnings("restriction")
public class WorkbenchPreferencesTransfer extends WorkbenchSettingsTransfer {

	public WorkbenchPreferencesTransfer() {
	}

	@Override
	public String getName() {
		return "Preferences";
	}

	@Override
	public IStatus transferSettings(IPath newWorkspaceRoot) {
		IPath currentWorkspace = Platform.getLocation();

		File srcDir = new File(currentWorkspace.toFile(), ".metadata/.plugins/org.eclipse.core.runtime/.settings");

		File destDir = new File(newWorkspaceRoot.toFile(), ".metadata/.plugins/org.eclipse.core.runtime/.settings");

		File[] srcSettings = srcDir.listFiles();

		if (!destDir.exists()) {
			if (!destDir.mkdirs()) {
				return new Status(IStatus.ERROR, LiferayUIPlugin.PLUGIN_ID, "can not create dirs");
			}
		}

		for (File src : srcSettings) {
			File destSetting = new File(destDir.getAbsolutePath(), src.getName());

			if (destSetting.exists()) {
				if (!destSetting.delete()) {
					return new Status(IStatus.ERROR, LiferayUIPlugin.PLUGIN_ID, "can not delete settings file");
				}
			}

			FileUtil.copyFile(src, destSetting);
		}

		return Status.OK_STATUS;
	}

}