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

package com.liferay.ide.upgrade.plan.core;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.core.util.WorkspaceConstants;

import java.io.File;

import java.nio.file.Path;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;

/**
 * @author Terry Jia
 */
public interface WorkspaceSupport {

	public default String getGradleProperty(Path path, String key, String defaultValue) {
		Path gradlePropertiesPath = path.resolve("gradle.properties");

		File gradleProperties = gradlePropertiesPath.toFile();

		if (FileUtil.notExists(gradleProperties)) {
			return defaultValue;
		}

		Properties properties = PropertiesUtil.loadProperties(gradleProperties);

		return properties.getProperty(key, defaultValue);
	}

	public default String getHomeDirName(Path path) {
		return getGradleProperty(path, WorkspaceConstants.HOME_DIR_PROPERTY, WorkspaceConstants.DEFAULT_HOME_DIR);
	}

	public default IPath getHomeLocation(Path path) {
		String homeDirName = getHomeDirName(path);

		Path homeLocation = path.resolve(homeDirName);

		return new org.eclipse.core.runtime.Path(homeLocation.toString());
	}

	public default String getPluginsSDKDirName(Path path) {
		return getGradleProperty(path, WorkspaceConstants.PLUGINS_SDK_DIR_PROPERTY, "plugins-sdk");
	}

	public default IPath getPluginsSDKLocation(Path path) {
		String pluginsSDKDirName = getPluginsSDKDirName(path);

		Path pluginsSDKLocation = path.resolve(pluginsSDKDirName);

		return new org.eclipse.core.runtime.Path(pluginsSDKLocation.toString());
	}

	public default boolean isValidGradleWorkspace(File workspaceDir) {
		File buildGradle = new File(workspaceDir, "build.gradle");
		File settingsGradle = new File(workspaceDir, "settings.gradle");
		File gradleProperties = new File(workspaceDir, "gradle.properties");

		if (FileUtil.notExists(buildGradle) || FileUtil.notExists(settingsGradle) ||
			FileUtil.notExists(gradleProperties)) {

			return false;
		}

		String settingsContent = FileUtil.readContents(settingsGradle, true);

		if (settingsContent != null) {
			Matcher matcher = workspacePluginPattern.matcher(settingsContent);

			if (matcher.matches()) {
				return true;
			}
		}

		return false;
	}

	public final Pattern workspacePluginPattern = Pattern.compile(
		".*apply.*plugin.*:.*[\'\"]com\\.liferay\\.workspace[\'\"].*", Pattern.MULTILINE | Pattern.DOTALL);

}