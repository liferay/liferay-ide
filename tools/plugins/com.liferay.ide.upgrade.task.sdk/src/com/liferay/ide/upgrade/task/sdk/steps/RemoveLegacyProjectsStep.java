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

package com.liferay.ide.upgrade.task.sdk.steps;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.api.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.base.WorkspaceUpgradeTaskStep;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(properties = "OSGI-INF/RemoveLegacyProjectsStep.properties", service = UpgradeTaskStep.class)
public class RemoveLegacyProjectsStep extends WorkspaceUpgradeTaskStep {

	@Override
	protected IStatus execute(IProject project, IProgressMonitor progressMonitor) {
		IPath projectLocation = project.getLocation();

		IPath sdkLoaction = projectLocation.append("plugins-sdk");

		for (String path : _needToDeletePaths) {
			IPath needToDeletePath = sdkLoaction.append(path);

			File file = needToDeletePath.toFile();

			if (file.exists()) {
				FileUtil.deleteDir(file, true);
			}
		}

		return Status.OK_STATUS;
	}

	private final String[] _needToDeletePaths = {"shared/portal-http-service", "webs/resources-importer-web"};

}