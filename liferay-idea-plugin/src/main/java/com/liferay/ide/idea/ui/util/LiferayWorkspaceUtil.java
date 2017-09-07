/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.idea.ui.util;

import java.io.File;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author Terry Jia
 */
public class LiferayWorkspaceUtil {

    private final static Pattern PATTERN_WORKSPACE_PLUGIN = Pattern.compile(
            ".*apply.*plugin.*:.*[\'\"]com\\.liferay\\.workspace[\'\"].*", Pattern.MULTILINE | Pattern.DOTALL);

    private static final String _GRADLE_PROPERTIES_FILE_NAME = "gradle.properties";
    private static final String _SETTINGS_GRADLE_FILE_NAME = "settings.gradle";
    private static final String _BUILD_GRADLE_FILE_NAME = "build.gradle";

    public static boolean isValidGradleWorkspaceLocation(String location) {
        File workspaceDir = new File(location);

        File buildGradle = new File(workspaceDir, _BUILD_GRADLE_FILE_NAME);
        File settingsGradle = new File(workspaceDir, _SETTINGS_GRADLE_FILE_NAME);
        File gradleProperties = new File(workspaceDir, _GRADLE_PROPERTIES_FILE_NAME);

        if (!(buildGradle.exists() && settingsGradle.exists() && gradleProperties.exists())) {
            return false;
        }

        final String settingsContent = FileUtil.readContents(settingsGradle, true);

        return settingsContent != null && PATTERN_WORKSPACE_PLUGIN.matcher(settingsContent).matches();
    }

    public static String getHomeDir(String location) {
        final String result = getGradleProperty(location, "liferay.workspace.home.dir", "bundles");

        return (result == null || result.equals("")) ? "bundles" : result;
    }

    private static String getGradleProperty(String projectLocation, String key, String defaultValue) {
        final File gradleProperties = new File(projectLocation, "gradle.properties");

        if (gradleProperties.exists()) {
            final Properties properties = PropertiesUtil.loadProperties(gradleProperties);

            return properties.getProperty(key, defaultValue);
        }

        return "";
    }

}
