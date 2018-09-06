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

import java.io.File;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Andy Wu
 */
public class BackupLocationValidationService extends ValidationService {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		Value<Path> backupLocation = _op().getBackupLocation();

		Path location = backupLocation.content();

		if (location != null) {
			if (!location.isAbsolute()) {
				return Status.createErrorStatus("\"" + location.toPortableString() + "\" is not an absolute path.");
			}

			File file = location.toFile();

			if (file.isFile()) {
				return Status.createErrorStatus("\"" + location.toPortableString() + "\" is not a folder.");
			}
		}

		return retval;
	}

	private LiferayUpgradeDataModel _op() {
		return context(LiferayUpgradeDataModel.class);
	}

}