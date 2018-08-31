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

package com.liferay.ide.project.core.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

/**
 * @author Andy Wu
 */
public class ValidationUtil {

	public static boolean isExistingProjectName(String projectName) {
		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			if (projectName.equalsIgnoreCase(project.getName())) {
				return true;
			}
		}

		return false;
	}

	public static boolean isProjectTargetDirFile(File file) {
		IProject project = CoreUtil.getProject(file);

		IFolder targetFolder = project.getFolder("target");

		boolean inTargetDir = false;

		File targetDir = null;

		if (targetFolder.exists()) {
			targetDir = FileUtil.getFile(targetFolder.getLocation());

			try {
				String path = file.getCanonicalPath();

				inTargetDir = path.startsWith(targetDir.getCanonicalPath());
			}
			catch (IOException ioe) {
			}
		}

		return inTargetDir;
	}

}