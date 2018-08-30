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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public abstract class AbstractProjectLocationValidationService<T extends ExecutableElement> extends ValidationService {

	@Override
	public void dispose() {
		super.dispose();

		BaseModuleOp op = op();

		if (_listener != null) {
			op.detach(_listener);

			_listener = null;
		}
	}

	@Override
	protected Status compute() {
		BaseModuleOp op = op();

		Status retval = Status.createOkStatus();

		String currentProjectName = SapphireUtil.getContent(op.getProjectName());
		Path currentProjectLocation = SapphireUtil.getContent(op.getLocation());
		Boolean userDefaultLocation = SapphireUtil.getContent(op.getUseDefaultLocation());

		/*
		 * Location won't be validated if the UseDefaultLocation has an error.
		 * Get the validation of the property might not work as excepted,
		 * let's use call the validation service manually.
		 */
		if (userDefaultLocation) {
			return retval;
		}

		/*
		 * IDE-1150, instead of using annotation "@Required",use this service to
		 * validate the custom project location must be specified, let the wizard
		 * display the error of project name when project name and location are both
		 * null.
		 */
		if (currentProjectName == null) {
			return retval;
		}

		if (currentProjectLocation == null) {
			return Status.createErrorStatus("Location must be specified.");
		}

		String currentPath = currentProjectLocation.toOSString();

		if (!org.eclipse.core.runtime.Path.EMPTY.isValidPath(currentPath)) {
			return Status.createErrorStatus("\"" + currentPath + "\" is not a valid path.");
		}
		else {
			IPath osPath = org.eclipse.core.runtime.Path.fromOSString(currentPath);

			File file = osPath.toFile();

			if (!file.isAbsolute()) {
				retval = Status.createErrorStatus("\"" + currentPath + "\" is not an absolute path.");
			}
			else {
				if (FileUtil.notExists(osPath)) {

					// check non-existing external location

					if (!_canCreate(osPath.toFile())) {
						retval = Status.createErrorStatus("Cannot create project content at \"" + currentPath + "\"");
					}
				}

				NewLiferayProjectProvider<BaseModuleOp> provider = SapphireUtil.getContent(op.getProjectProvider());

				IStatus locationStatus = provider.validateProjectLocation(currentProjectName, osPath);

				if (!locationStatus.isOK()) {
					retval = Status.createErrorStatus(locationStatus.getMessage());
				}
			}
		}

		return retval;
	}

	@Override
	protected void initValidationService() {
		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		BaseModuleOp op = op();

		SapphireUtil.attachListener(op.getProjectName(), _listener);
		SapphireUtil.attachListener(op.getProjectProvider(), _listener);
	}

	protected abstract BaseModuleOp op();

	private boolean _canCreate(File file) {
		while (FileUtil.notExists(file)) {
			file = file.getParentFile();

			if (file == null) {
				return false;
			}
		}

		return file.canWrite();
	}

	private Listener _listener;

}