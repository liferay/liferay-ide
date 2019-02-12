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

package com.liferay.ide.upgrade.tasks.core.internal.sdk;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepStatus;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"id=remove_legacy_projects", "requirement=recommended", "order=1", "taskId=migrate_plugins_sdk",
		"title=Remove Legacy Projects"
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStep.class
)
public class RemoveLegacyProjectsTaskStep extends BaseUpgradeTaskStep {

	public IStatus execute(IProject project, IProgressMonitor progressMonitor) {
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

	@Override
	public UpgradeTaskStepStatus getStatus() {
		return UpgradeTaskStepStatus.INCOMPLETE;
	}

	private final String[] _needToDeletePaths = {"shared/portal-http-service", "webs/resources-importer-web"};

}