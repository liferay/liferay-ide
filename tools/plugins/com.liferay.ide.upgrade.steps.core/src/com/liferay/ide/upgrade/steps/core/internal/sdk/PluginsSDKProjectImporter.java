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

package com.liferay.ide.upgrade.steps.core.internal.sdk;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.upgrade.steps.core.ProjectImporter;
import com.liferay.ide.upgrade.steps.core.internal.UpgradeStepsCorePlugin;

import java.io.File;

import java.nio.file.Path;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@Component(property = "type=plugins_sdk", service = ProjectImporter.class)
public class PluginsSDKProjectImporter implements ProjectImporter {

	@Override
	public IStatus canImport(Path rootProjectPath) {
		if ((rootProjectPath == null) || FileUtil.notExists(rootProjectPath.toFile())) {
			return UpgradeStepsCorePlugin.createErrorStatus("SDK location does not exists.");
		}

		Path buildProperties = rootProjectPath.resolve(ISDKConstants.BUILD_PROPERTIES);

		Path portletsBuildXml = rootProjectPath.resolve(ISDKConstants.PORTLET_PLUGIN_ANT_BUILD);

		Path hooksBuildXml = rootProjectPath.resolve(ISDKConstants.HOOK_PLUGIN_ANT_BUILD);

		if (FileUtil.notExists(buildProperties.toFile()) || FileUtil.notExists(portletsBuildXml.toFile()) ||
			FileUtil.notExists(hooksBuildXml.toFile())) {

			return UpgradeStepsCorePlugin.createErrorStatus("The folder is not a valid sdk loaction.");
		}

		return Status.OK_STATUS;
	}

	@Override
	public IStatus importProjects(Path rootProjectPath, IProgressMonitor progressMonitor) {
		File file = rootProjectPath.toFile();

		String pathValue = file.getAbsolutePath();

		String fileName = file.getName();

		org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path(pathValue);

		try {
			CoreUtil.openProject(fileName, path, progressMonitor);
		}
		catch (CoreException ce) {
		}

		return Status.OK_STATUS;
	}

}