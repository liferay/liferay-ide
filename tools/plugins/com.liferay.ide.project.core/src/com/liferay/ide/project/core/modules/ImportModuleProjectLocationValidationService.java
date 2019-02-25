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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.ProjectImportUtil;

import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Andy Wu
 */
public class ImportModuleProjectLocationValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		Path path = get(_op().getLocation());

		if ((path == null) || path.isEmpty()) {
			return Status.createOkStatus();
		}

		String location = path.toOSString();

		Status retval = StatusBridge.create(ProjectImportUtil.validatePath(location));

		if (!retval.ok()) {
			return retval;
		}

		if (LiferayWorkspaceUtil.isValidWorkspaceLocation(location)) {
			return Status.createErrorStatus(
				"Can not import Liferay Workspace, please use Import Liferay Workspace Project wizard.");
		}

		retval = StatusBridge.create(ImportLiferayModuleProjectOpMethods.getBuildType(location));

		if (retval.severity() == Status.Severity.ERROR) {
			return retval;
		}

		String projectName = path.lastSegment();

		if (FileUtil.exists(CoreUtil.getProject(projectName))) {
			return Status.createErrorStatus("A project with that name already exists");
		}

		return retval;
	}

	private ImportLiferayModuleProjectOp _op() {
		return context(ImportLiferayModuleProjectOp.class);
	}

}