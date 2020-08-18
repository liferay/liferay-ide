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

import org.apache.maven.plugin.MojoExecution;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

/**
 * @author Gregory Amerson
 */
public class LiferayProjectLifecycleConfigurator extends AbstractProjectConfigurator {

	public LiferayProjectLifecycleConfigurator() {
	}

	@Override
	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
	}

	@Override
	public AbstractBuildParticipant getBuildParticipant(
		IMavenProjectFacade projectFacade, MojoExecution execution, IPluginExecutionMetadata executionMetadata) {

		if (_isLiferayMavenThemePluginExecution(execution)) {
			String goal = execution.getGoal();

			if (ILiferayMavenConstants.GOAL_THEME_MERGE.equals(goal)) {
				return new ThemeMergeBuildParticipant();
			}
			else if (ILiferayMavenConstants.GOAL_BUILD_CSS.equals(goal)) {
				return new BuildCSSBuildParticipant();
			}
			else if (ILiferayMavenConstants.GOAL_BUILD_THUMBNAIL.equals(goal)) {
				return new BuildThumbnailBuildParticipant();
			}
		}
		else if (_isLiferayMavenExtPluginExecution(execution)) {

			// TODO IDE-936
			// return new ExtPluginBuildParticipant();

		}

		return null;
	}

	private boolean _isLiferayMavenExtPluginExecution(MojoExecution execution) {
		if (ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_GROUP_ID.equals(execution.getGroupId()) &&
			ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID.equals(execution.getArtifactId()) &&
			ILiferayMavenConstants.GOAL_BUILD_EXT.equals(execution.getGoal())) {

			return true;
		}

		return false;
	}

	private boolean _isLiferayMavenThemePluginExecution(MojoExecution execution) {
		String goal = execution.getGoal();

		if (ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_GROUP_ID.equals(execution.getGroupId()) &&
			ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID.equals(execution.getArtifactId()) &&
			(ILiferayMavenConstants.GOAL_BUILD_CSS.equals(goal) ||
			 ILiferayMavenConstants.GOAL_THEME_MERGE.equals(goal) ||
			 ILiferayMavenConstants.GOAL_BUILD_THUMBNAIL.equals(goal))) {

			return true;
		}

		return false;
	}

}