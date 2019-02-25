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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Joye Luo
 */
public class NewModuleFragmentProjectValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		NewModuleFragmentFilesOp op = context(NewModuleFragmentFilesOp.class);

		String projectName = get(op.getProjectName());

		if (projectName == null) {
			return Status.createErrorStatus("No suitable Liferay fragment project.");
		}

		IProject project = CoreUtil.getProject(projectName);

		IFile bndFile = project.getFile("bnd.bnd");

		if (FileUtil.notExists(bndFile)) {
			return Status.createErrorStatus("Can not find bnd.bnd file in the project.");
		}

		return Status.createOkStatus();
	}

}