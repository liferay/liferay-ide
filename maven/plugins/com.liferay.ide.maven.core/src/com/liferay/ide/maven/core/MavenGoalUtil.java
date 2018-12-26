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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringUtil;

import org.apache.maven.model.Plugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.osgi.framework.Version;

/**
 * @author Joye Luo
 */
public class MavenGoalUtil {

	public static String getMavenBuildServiceGoal(IProject project) {
		try {
			String pluginKey =
				ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_GROUP_ID + ":" +
					ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID;

			Plugin plugin = MavenUtil.getPlugin(
				MavenUtil.getProjectFacade(project), pluginKey, new NullProgressMonitor());

			if (plugin == null) {
				pluginKey =
					ILiferayMavenConstants.NEW_LIFERAY_MAVEN_PLUGINS_GROUP_ID + ":" +
						ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_SERVICE_BUILDER_KEY;

				plugin = MavenUtil.getPlugin(MavenUtil.getProjectFacade(project), pluginKey, new NullProgressMonitor());
			}

			return getMavenBuildServiceGoal(plugin);
		}
		catch (CoreException ce) {
			LiferayMavenCore.logError(ce);
		}

		return ILiferayMavenConstants.PLUGIN_GOAL_BUILD_SERVICE;
	}

	public static String getMavenBuildServiceGoal(Plugin plugin) {
		if (plugin == null) {
			return "build-service";
		}

		if ((CoreUtil.compareVersions(Version.parseVersion(plugin.getVersion()), new Version("1.0.145")) >= 0) &&
			StringUtil.equals(
				plugin.getArtifactId(), ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_SERVICE_BUILDER_KEY)) {

			return "service-builder:build";
		}

		return ILiferayMavenConstants.PLUGIN_GOAL_BUILD_SERVICE;
	}

	public static String getMavenBuildWSDDGoal(IProject project) {
		try {
			String pluginKey =
				ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_GROUP_ID + ":" +
					ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID;

			Plugin plugin = MavenUtil.getPlugin(
				MavenUtil.getProjectFacade(project), pluginKey, new NullProgressMonitor());

			if (plugin == null) {
				pluginKey =
					ILiferayMavenConstants.NEW_LIFERAY_MAVEN_PLUGINS_GROUP_ID + ":" +
						ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_WSDD_BUILDER_KEY;

				plugin = MavenUtil.getPlugin(MavenUtil.getProjectFacade(project), pluginKey, new NullProgressMonitor());
			}

			return getMavenBuildWSDDGoal(plugin);
		}
		catch (CoreException ce) {
			LiferayMavenCore.logError(ce);
		}

		return ILiferayMavenConstants.PLUGIN_GOAL_BUILD_WSDD;
	}

	public static String getMavenBuildWSDDGoal(Plugin plugin) {
		if (plugin == null) {
			return "build-wsdd";
		}

		if ((CoreUtil.compareVersions(Version.parseVersion(plugin.getVersion()), new Version("1.0.7")) >= 0) &&
			StringUtil.equals(plugin.getArtifactId(), ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_WSDD_BUILDER_KEY)) {

			return "wsdd-builder:build";
		}

		return ILiferayMavenConstants.PLUGIN_GOAL_BUILD_WSDD;
	}

	public static String getMavenInitBundleGoal(IProject project) {
		try {
			String pluginKey =
				ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_GROUP_ID + ":" +
					ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID;

			Plugin plugin = MavenUtil.getPlugin(
				MavenUtil.getProjectFacade(project), pluginKey, new NullProgressMonitor());

			if (plugin == null) {
				pluginKey =
					ILiferayMavenConstants.NEW_LIFERAY_MAVEN_PLUGINS_GROUP_ID + ":" +
						ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_BUNDLE_SUPPORT_KEY;

				plugin = MavenUtil.getPlugin(MavenUtil.getProjectFacade(project), pluginKey, new NullProgressMonitor());
			}

			return getMavenInitBundleGoal(plugin);
		}
		catch (CoreException ce) {
			LiferayMavenCore.logError(ce);
		}

		return ILiferayMavenConstants.PLUGIN_GOAL_INIT_BUNDLE;
	}

	public static String getMavenInitBundleGoal(Plugin plugin) {
		if (plugin == null) {
			return "init-bundle";
		}

		if (CoreUtil.compareVersions(Version.parseVersion(plugin.getVersion()), new Version("2.0.2")) >= 0) {
			return "bundle-support:init";
		}

		return ILiferayMavenConstants.PLUGIN_GOAL_INIT_BUNDLE;
	}

}