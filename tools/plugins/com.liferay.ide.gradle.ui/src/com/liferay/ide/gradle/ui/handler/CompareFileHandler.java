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

package com.liferay.ide.gradle.ui.handler;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.project.ui.handlers.AbstractCompareFileHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author Lovett Li
 */
public class CompareFileHandler extends AbstractCompareFileHandler {

	protected File getTemplateFile(IFile currentFile) throws Exception {
		IProject currentProject = currentFile.getProject();

		IFile bndfile = currentProject.getFile("bnd.bnd");

		File templateFile = null;

		try (InputStream inputStream = bndfile.getContents();
			InputStreamReader inputReader = new InputStreamReader(inputStream);
			BufferedReader reader = new BufferedReader(inputReader)) {

			String fragment;

			while ((fragment = reader.readLine()) != null) {
				if (fragment.startsWith("Fragment-Host:")) {
					fragment = fragment.substring(fragment.indexOf(":") + 1, fragment.indexOf(";")).trim();

					break;
				}
			}

			String currentLocation = currentFile.getFullPath().toOSString();

			String hookFolder = currentLocation.substring(currentLocation.lastIndexOf("META-INF"));

			IPath stateLocation = GradleCore.getDefault().getStateLocation();

			IPath templateLocation = stateLocation.append(fragment).append(hookFolder);

			templateFile = new File(templateLocation.toOSString());

			if (FileUtil.notExists(templateFile)) {
				throw new FileNotFoundException("Template not found.");
			}
		}

		return templateFile;
	}

}