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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.upgrade.tasks.core.ProjectImporter;

import java.io.File;

import java.nio.file.Path;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@Component(property = "type=plugins_sdk", service = ProjectImporter.class)
public class PluginsSDKProjectImporter implements ProjectImporter {

	@Override
	public IStatus importProjects(Path rootProjectPath) {
		File file = rootProjectPath.toFile();

		String pathValue = file.getAbsolutePath();

		String fileName = file.getName();

		org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path(pathValue);

		try {
			CoreUtil.openProject(fileName, path, new NullProgressMonitor());
		}
		catch (CoreException ce) {
		}

		return Status.OK_STATUS;
	}

}