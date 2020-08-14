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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import java.util.Objects;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class LocationValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		super.dispose();

		NewLiferayPluginProjectOp op = _op();

		if ((_listener != null) && (op != null) && !op.disposed()) {
			SapphireUtil.detachListener(op.getProjectName(), _listener);

			_listener = null;
		}
	}

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		NewLiferayPluginProjectOp op = _op();

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = get(op.getProjectProvider());

		if (Objects.equals("ant", provider.getShortName())) {
			SDK sdk = null;

			try {
				sdk = SDKUtil.getWorkspaceSDK();

				if (sdk != null) {
					IStatus sdkStatus = sdk.validate();

					if (!sdkStatus.isOK()) {
						retval = Status.createErrorStatus(sdkStatus.getChildren()[0].getMessage());
					}
				}
			}
			catch (CoreException ce) {
				retval = Status.createErrorStatus(ce);
			}
		}

		Path currentProjectLocation = get(op.getLocation());
		String currentProjectName = get(op.getProjectName());

		/**
		 * Location won't be validated if the UseDefaultLocation has an error.
		 * Get the validation of the property might not work as excepted,
		 * let's use call the validation service manually.
		 */
		Value<Boolean> useDefalutLocationValue = op.getUseDefaultLocation();

		UseDefaultLocationValidationService service = useDefalutLocationValue.service(
			UseDefaultLocationValidationService.class);

		Status status = service.validation();

		if (!useDefalutLocationValue.content(true) && status.ok() && (currentProjectName != null)) {

			/**
			 * IDE-1150, instead of using annotation "@Required",use this service to
			 * validate the custom project location must be specified, let the wizard
			 * display the error of project name when project name and location are both
			 * null.
			 */
			if (currentProjectLocation == null) {
				return Status.createErrorStatus("Location must be specified.");
			}

			String currentPath = currentProjectLocation.toOSString();

			if (!org.eclipse.core.runtime.Path.EMPTY.isValidPath(currentPath)) {
				return Status.createErrorStatus("\"" + currentPath + "\" is not a valid path.");
			}

			IPath osPath = org.eclipse.core.runtime.Path.fromOSString(currentPath);

			File osFile = osPath.toFile();

			if (!osFile.isAbsolute()) {
				return Status.createErrorStatus("\"" + currentPath + "\" is not an absolute path.");
			}

			if (FileUtil.notExists(osPath) && !_canCreate(osFile)) {
				retval = Status.createErrorStatus("Cannot create project content at \"" + currentPath + "\"");
			}

			IStatus locationStatus = provider.validateProjectLocation(currentProjectName, osPath);

			if (!locationStatus.isOK()) {
				retval = Status.createErrorStatus(locationStatus.getMessage());
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

		NewLiferayPluginProjectOp op = _op();

		SapphireUtil.attachListener(op.getProjectName(), _listener);
		SapphireUtil.attachListener(op.getProjectProvider(), _listener);
	}

	private boolean _canCreate(File file) {
		while (FileUtil.notExists(file)) {
			file = file.getParentFile();

			if (file == null) {
				return false;
			}
		}

		return file.canWrite();
	}

	private NewLiferayPluginProjectOp _op() {
		return context(NewLiferayPluginProjectOp.class);
	}

	private Listener _listener;

}