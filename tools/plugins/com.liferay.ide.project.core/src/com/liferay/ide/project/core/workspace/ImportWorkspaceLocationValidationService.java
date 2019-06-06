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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.ProjectImportUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Andy Wu
 */
public class ImportWorkspaceLocationValidationService extends ValidationService {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		if (LiferayWorkspaceUtil.hasWorkspace()) {
			return Status.createErrorStatus(LiferayWorkspaceUtil.hasLiferayWorkspaceMsg);
		}

		Value<Path> workspaceLocation = _op().getWorkspaceLocation();

		Path currentProjectLocation = workspaceLocation.content(true);

		if ((currentProjectLocation != null) && !currentProjectLocation.isEmpty()) {
			String currentPath = currentProjectLocation.toOSString();

			IStatus validPathStatus = ProjectImportUtil.validatePath(currentPath);

			if (!validPathStatus.isOK()) {
				return Status.createErrorStatus(validPathStatus.getMessage());
			}

			if (LiferayWorkspaceUtil.getWorkspaceType(currentPath) == null) {
				return Status.createErrorStatus("Invalid Liferay Workspace");
			}

			String projectName = currentProjectLocation.lastSegment();

			if (FileUtil.exists(CoreUtil.getProject(projectName))) {
				return Status.createErrorStatus("A project with that name already exists.");
			}
		}

		return retval;
	}

	private ImportLiferayWorkspaceOp _op() {
		return context(ImportLiferayWorkspaceOp.class);
	}

}