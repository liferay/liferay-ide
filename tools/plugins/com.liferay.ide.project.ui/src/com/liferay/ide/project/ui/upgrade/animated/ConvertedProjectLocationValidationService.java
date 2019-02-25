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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.core.util.SapphireContentAccessor;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Andy Wu
 */
public class ConvertedProjectLocationValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		Path currentProjectLocation = get(_op().getConvertedProjectLocation());

		if (currentProjectLocation != null) {
			String currentPath = currentProjectLocation.toPortableString();

			if (!org.eclipse.core.runtime.Path.EMPTY.isValidPath(currentPath)) {
				retval = Status.createErrorStatus("\"" + currentPath + "\" is not a valid path.");
			}
			else {
				IPath osPath = org.eclipse.core.runtime.Path.fromOSString(currentPath);

				File file = osPath.toFile();

				if (!file.isAbsolute()) {
					retval = Status.createErrorStatus("\"" + currentPath + "\" is not an absolute path.");
				}
				else {
					if (!file.exists()) {
						if (!_canCreate(file)) {
							retval = Status.createErrorStatus(
								"Cannot create project content at \"" + currentPath + "\"");
						}
					}
				}
			}
		}
		else {
			retval = Status.createErrorStatus("Converted Project Location must be specified.");
		}

		return retval;
	}

	private boolean _canCreate(File file) {
		while (!file.exists()) {
			file = file.getParentFile();

			if (file == null) {
				return false;
			}
		}

		return file.canWrite();
	}

	private LiferayUpgradeDataModel _op() {
		return context(LiferayUpgradeDataModel.class);
	}

}