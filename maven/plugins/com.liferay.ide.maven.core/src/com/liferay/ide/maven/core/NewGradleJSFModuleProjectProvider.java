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

package com.liferay.ide.maven.core;

import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;

import java.io.File;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.Value;

/**
 * @author Simon Jiang
 */
public class NewGradleJSFModuleProjectProvider extends NewMavenJSFModuleProjectProvider {

	@Override
	public IStatus createNewProject(NewLiferayJSFModuleProjectOp op, IProgressMonitor monitor) throws CoreException {
		IStatus retval = Status.OK_STATUS;

		try {
			IPath projectLocation = createArchetypeProject(op, monitor);

			IPath buildGradlePath = projectLocation.append("build.gradle");

			File buildGradleFile = buildGradlePath.toFile();

			if (FileUtil.exists(buildGradlePath)) {
				try {
					File workspaceDir = LiferayWorkspaceUtil.getWorkspaceDir(buildGradleFile);

					if (FileUtil.exists(workspaceDir)) {
						boolean hasLiferayWorkspace = LiferayWorkspaceUtil.isValidWorkspaceLocation(
							workspaceDir.getAbsolutePath());

						if (hasLiferayWorkspace) {
							List<String> buildGradleContents = Files.readAllLines(
								Paths.get(buildGradleFile.toURI()), StandardCharsets.UTF_8);

							List<String> modifyContents = new ArrayList<>();

							for (String line : buildGradleContents) {
								Matcher matcher = _liferayWarPluginPattern.matcher(line);

								if (matcher.matches()) {
									continue;
								}

								modifyContents.add(line);
							}

							Files.write(buildGradleFile.toPath(), modifyContents, StandardCharsets.UTF_8);
						}
					}
				}
				catch (Exception e) {
					ProjectCore.logError("Failed to check LiferayWorkspace project. ");
				}
			}

			FileUtil.delete(projectLocation.append("pom.xml"));

			Value<String> projectNameValue = op.getProjectName();

			String projectName = projectNameValue.content();

			CoreUtil.openProject(projectName, projectLocation, monitor);

			ILiferayProjectImporter importer = LiferayCore.getImporter("gradle");

			String location = projectLocation.toOSString();

			IStatus canImport = importer.canImport(location);

			if (canImport.getCode() != Status.ERROR) {
				importer.importProjects(location, monitor);
			}
		}
		catch (Exception e) {
			throw new CoreException(LiferayCore.createErrorStatus(e));
		}

		return retval;
	}

	private static final Pattern _liferayWarPluginPattern = Pattern.compile(
		".*apply.*plugin.*:.*[\'\"]((com\\.liferay)|(war))[\'\"].*", Pattern.MULTILINE | Pattern.DOTALL);

}