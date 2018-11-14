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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.WorkspaceConstants;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.IWorkspaceProjectBuilder;
import com.liferay.ide.project.core.LiferayWorkspaceProject;

import java.io.File;
import java.io.FileReader;

import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author Simon Jiang
 */
public class LiferayMavenWorkspaceProject extends LiferayWorkspaceProject {

	public LiferayMavenWorkspaceProject(IProject project) {
		super(project);
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
	public String getHomeDirName() {
		return getProperty(WorkspaceConstants.MAVEN_HOME_DIR_PROPERTY, WorkspaceConstants.DEFAULT_HOME_DIR);
	}

	@Override
	public boolean isWatchable() {
		return false;
	}

	@Override
	protected Properties loadExtraProperties() {
		IPath projectLocation = getProject().getLocation();

		File pomFile = new File(projectLocation.toFile(), "pom.xml");

		if (FileUtil.exists(pomFile)) {
			MavenXpp3Reader mavenReader = new MavenXpp3Reader();

			try (FileReader reader = new FileReader(pomFile)) {
				Model model = mavenReader.read(reader);

				if (model != null) {
					return model.getProperties();
				}
			}
			catch (Exception e) {
			}
		}

		return new Properties();
	}

}