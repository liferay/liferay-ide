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

import com.liferay.ide.core.Event;
import com.liferay.ide.core.EventListener;
import com.liferay.ide.core.IProjectBuilder;
import com.liferay.ide.core.IWorkspaceProjectBuilder;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.workspace.ProjectChangedEvent;
import com.liferay.ide.core.workspace.WorkspaceConstants;
import com.liferay.ide.project.core.LiferayWorkspaceProject;

import java.io.File;
import java.io.FileReader;

import java.util.Objects;
import java.util.Optional;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author Simon Jiang
 * @author Seiphon Wang
 */
public class LiferayMavenWorkspaceProject extends LiferayWorkspaceProject implements EventListener {

	public LiferayMavenWorkspaceProject(IProject project) {
		super(project);

		IPath projectPath = project.getFullPath();

		_importantResources = new IPath[] {projectPath.append("pom.xml")};
	}

	@Override
	public <T> T adapt(Class<T> adapterType) {
		if (IProjectBuilder.class.equals(adapterType) || IWorkspaceProjectBuilder.class.equals(adapterType)) {
			IProjectBuilder projectBuilder = new MavenProjectBuilder(getProject());

			return adapterType.cast(projectBuilder);
		}

		return super.adapt(adapterType);
	}

	@Override
	public String getLiferayHome() {
		_readMavenWorkspaceProperties();

		return getProperty(WorkspaceConstants.LIFERAY_HOME_PROPERTY, WorkspaceConstants.DEFAULT_HOME_DIR);
	}

	@Override
	public String getTargetPlatformVersion() {
		_readMavenWorkspaceProperties();

		String targetPlatformVersion = getProperty(WorkspaceConstants.WORKSPACE_BOM_VERSION, null);

		if (Objects.nonNull(targetPlatformVersion) && targetPlatformVersion.contains("-")) {
			targetPlatformVersion = targetPlatformVersion.substring(0, targetPlatformVersion.indexOf("-"));
		}

		return targetPlatformVersion;
	}

	@Override
	public String[] getWorkspaceModuleDirs() {
		String workspaceBomVersion = getTargetPlatformVersion();

		if (Objects.nonNull(workspaceBomVersion)) {
			return null;
		}

		return new String[] {"modules"};
	}

	@Override
	public String[] getWorkspaceWarDirs() {
		String workspaceBomVersion = getTargetPlatformVersion();

		if (Objects.nonNull(workspaceBomVersion)) {
			return null;
		}

		return new String[] {"wars"};
	}

	@Override
	public boolean isFlexibleLiferayWorkspace() {
		_readMavenWorkspaceProperties();

		return Objects.nonNull(getProperty(WorkspaceConstants.WORKSPACE_BOM_VERSION, null));
	}

	@Override
	public boolean isStale() {
		return _stale;
	}

	@Override
	public void onEvent(Event event) {
		Optional.of(
			event
		).filter(
			e -> !isStale()
		).filter(
			ProjectChangedEvent.class::isInstance
		).map(
			ProjectChangedEvent.class::cast
		).filter(
			projectChangedEvent -> hasResourcesAffected(projectChangedEvent, getProject(), _importantResources)
		).ifPresent(
			e -> _stale = true
		);
	}

	private void _readMavenWorkspaceProperties() {
		IProject project = getProject();

		if (project.exists()) {
			IPath projectLocation = project.getLocation();

			File pomFile = new File(projectLocation.toFile(), "pom.xml");

			if (FileUtil.exists(pomFile)) {
				MavenXpp3Reader mavenReader = new MavenXpp3Reader();

				try (FileReader reader = new FileReader(pomFile)) {
					Model model = mavenReader.read(reader);

					if (model != null) {
						properties.putAll(model.getProperties());
					}
				}
				catch (Exception e) {
				}
			}
		}
	}

	private IPath[] _importantResources;
	private volatile boolean _stale = false;

}