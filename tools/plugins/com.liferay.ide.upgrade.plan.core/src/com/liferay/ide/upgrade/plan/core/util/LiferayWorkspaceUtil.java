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

package com.liferay.ide.upgrade.plan.core.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.File;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author Terry Jia
 */
public class LiferayWorkspaceUtil {

	public static IProject getWorkspaceProject() {
		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			if (isValidWorkspace(project)) {
				return project;
			}
		}

		return null;
	}

	public static boolean isValidGradleWorkspaceLocation(String location) {
		File workspaceDir = new File(location);

		File buildGradle = new File(workspaceDir, _BUILD_GRADLE_FILE_NAME);
		File settingsGradle = new File(workspaceDir, _SETTINGS_GRADLE_FILE_NAME);
		File gradleProperties = new File(workspaceDir, _GRADLE_PROPERTIES_FILE_NAME);

		if (FileUtil.notExists(buildGradle) || FileUtil.notExists(settingsGradle) ||
			FileUtil.notExists(gradleProperties)) {

			return false;
		}

		String settingsContent = FileUtil.readContents(settingsGradle, true);

		if (settingsContent != null) {
			Matcher matcher = _workspacePluginPattern.matcher(settingsContent);

			if (matcher.matches()) {
				return true;
			}
		}

		return false;
	}

	public static boolean isValidWorkspace(IProject project) {
		if ((project != null) && (project.getLocation() != null) && isValidWorkspaceLocation(project.getLocation())) {
			return true;
		}

		return false;
	}

	public static boolean isValidWorkspaceLocation(IPath path) {
		return isValidGradleWorkspaceLocation(path.toOSString());
	}

	private static final String _BUILD_GRADLE_FILE_NAME = "build.gradle";

	private static final String _GRADLE_PROPERTIES_FILE_NAME = "gradle.properties";

	private static final String _SETTINGS_GRADLE_FILE_NAME = "settings.gradle";

	private static final Pattern _workspacePluginPattern = Pattern.compile(
		".*apply.*plugin.*:.*[\'\"]com\\.liferay\\.workspace[\'\"].*", Pattern.MULTILINE | Pattern.DOTALL);

}