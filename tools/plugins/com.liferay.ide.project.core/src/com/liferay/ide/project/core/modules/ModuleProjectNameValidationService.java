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
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.util.ValidationUtil;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class ModuleProjectNameValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		super.dispose();

		op().detach(_listener, "*");
	}

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		BaseModuleOp op = op();

		String currentProjectName = get(op.getProjectName());

		if (!CoreUtil.empty(currentProjectName)) {
			IStatus nameStatus = CoreUtil.validateName(currentProjectName, IResource.PROJECT);

			if (!nameStatus.isOK()) {
				return StatusBridge.create(nameStatus);
			}

			if (ValidationUtil.isExistingProjectName(currentProjectName)) {
				return Status.createErrorStatus("A project with that name(ignore case) already exists.");
			}

			if (!_validProjectName(currentProjectName)) {
				return Status.createErrorStatus("The project name is invalid.");
			}

			Path currentProjectLocation = get(op.getLocation());

			// double check to make sure this project wont overlap with existing dir

			if (currentProjectLocation != null) {
				String currentPath = currentProjectLocation.toOSString();

				IPath osPath = org.eclipse.core.runtime.Path.fromOSString(currentPath);

				NewLiferayProjectProvider<BaseModuleOp> provider = get(op.getProjectProvider());

				IStatus projectStatus = provider.validateProjectLocation(currentProjectName, osPath);

				if (!projectStatus.isOK()) {
					return StatusBridge.create(projectStatus);
				}

				File projectFodler = FileUtil.getFile(osPath.append(currentProjectName));

				if (FileUtil.hasChildren(projectFodler)) {
					return StatusBridge.create(ProjectCore.createErrorStatus("Target project folder is not empty."));
				}
			}
		}

		return retval;
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				PropertyDef def = SapphireUtil.getPropertyDef(event);

				if (!def.equals(BaseModuleOp.PROP_FINAL_PROJECT_NAME) && !def.equals(BaseModuleOp.PROP_PROJECT_NAMES) &&
					!"ProjectName".equals(def.name()) && !def.equals(ProjectName.PROP_PROJECT_NAME)) {

					refresh();
				}
			}

		};

		op().attach(_listener, "*");
	}

	protected BaseModuleOp op() {
		return context(BaseModuleOp.class);
	}

	private boolean _validProjectName(String currentProjectName) {
		return currentProjectName.matches(_PROJECT_NAME_REGEX);
	}

	private static final String _PROJECT_NAME_REGEX = "([A-Za-z0-9_\\-.]+[A-Za-z0-9]$)|([A-Za-z0-9])";

	private FilteredListener<PropertyContentEvent> _listener;

}