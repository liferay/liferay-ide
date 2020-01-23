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
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.ValidationUtil;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Andy Wu
 */
public class WorkspaceNameValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		super.dispose();

		if (_listener != null) {
			Value<Path> location = _op().getLocation();

			location.detach(_listener);

			_listener = null;
		}
	}

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		if (LiferayWorkspaceUtil.hasWorkspace()) {
			retval = Status.createErrorStatus(LiferayWorkspaceUtil.hasLiferayWorkspaceMsg);

			return retval;
		}

		String currentWorkspaceName = get(_op().getWorkspaceName());

		if (CoreUtil.isNullOrEmpty(currentWorkspaceName)) {
			return Status.createErrorStatus("Liferay Workspace project name could not be empty.");
		}

		IStatus nameStatus = CoreUtil.validateName(currentWorkspaceName, IResource.PROJECT);

		if (!nameStatus.isOK()) {
			return StatusBridge.create(nameStatus);
		}

		if (!_isValidProjectName(currentWorkspaceName)) {
			return Status.createErrorStatus("The name is invalid for a project.");
		}

		if (ValidationUtil.isExistingProjectName(currentWorkspaceName)) {
			return Status.createErrorStatus("A project with that name(ignore case) already exists.");
		}

		if (_isExistingFolder(currentWorkspaceName)) {
			return Status.createErrorStatus("Target project folder is not empty.");
		}

		return retval;
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		SapphireUtil.attachListener(_op().getLocation(), _listener);
	}

	private boolean _isExistingFolder(String projectName) {
		Path location = get(_op().getLocation());

		if (location != null) {
			Path path = location.append(projectName);

			File targetDir = path.toFile();

			if (FileUtil.hasChildren(targetDir)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isValidProjectName(String currentProjectName) {
		return currentProjectName.matches(_PROJECT_NAME_REGEX);
	}

	private NewLiferayWorkspaceOp _op() {
		return context(NewLiferayWorkspaceOp.class);
	}

	private static final String _PROJECT_NAME_REGEX = "[A-Za-z0-9_\\-.]+";

	private Listener _listener;

}