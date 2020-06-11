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

package com.liferay.ide.project.core;

import com.liferay.ide.core.BaseLiferayProject;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.adapter.NoopLiferayProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public abstract class LiferayWorkspaceProject extends BaseLiferayProject implements IWorkspaceProject {

	public LiferayWorkspaceProject(IProject project) {
		super(project);

		_initializeWorkspaceProperties(project);
	}

	@Override
	public <T> T adapt(Class<T> adapterType) {
		if (ILiferayPortal.class.equals(adapterType)) {
			IFolder bundlesFolder = getProject().getFolder("bundles");

			if (FileUtil.exists(bundlesFolder)) {
				PortalBundle portalBundle = LiferayServerCore.newPortalBundle(bundlesFolder.getLocation());

				if (portalBundle != null) {
					return adapterType.cast(portalBundle);
				}
			}
		}

		return super.adapt(adapterType);
	}

	@Override
	public Set<IProject> getChildProjects() {
		if (FileUtil.notExists(getProject())) {
			return Collections.emptySet();
		}

		if (!getProject().isOpen()) {
			return Collections.emptySet();
		}

		IPath location = getProject().getLocation();

		Set<IProject> childProjects = Stream.of(
			CoreUtil.getAllProjects()
		).filter(
			project -> FileUtil.exists(project)
		).filter(
			project -> project.isOpen()
		).filter(
			project -> !project.equals(getProject())
		).filter(
			project -> LiferayCore.create(ILiferayProject.class, project) != null
		).filter(
			project -> !(LiferayCore.create(ILiferayProject.class, project) instanceof NoopLiferayProject)
		).filter(
			project -> location.isPrefixOf(project.getLocation())
		).collect(
			Collectors.toSet()
		);

		return childProjects;
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	@Override
	public IFolder[] getSourceFolders() {
		return new IFolder[0];
	}

	@Override
	public void watch(Set<IProject> childProjects) {
	}

	@Override
	public Set<IProject> watching() {
		return Collections.emptySet();
	}

	protected Properties properties = new Properties();

	private void _initializeWorkspaceProperties(IProject project) {
		try {
			if (project.exists()) {
				IPath projectLocation = project.getLocation();

				IPath bladeSettingsPath = projectLocation.append(".blade.properties");

				properties.putAll(PropertiesUtil.loadProperties(bladeSettingsPath));
			}
		}
		catch (Exception e) {
			ProjectCore.logError(e);
		}
	}

}