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
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public class LiferayWorkspaceProject extends BaseLiferayProject implements IWorkspaceProject {

	public LiferayWorkspaceProject(IProject project) {
		super(project);
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
	public List<IProject> getChildProjects() {
		if (FileUtil.notExists(getProject())) {
			return Collections.emptyList();
		}

		if (!getProject().isOpen()) {
			return Collections.emptyList();
		}

		IPath location = getProject().getLocation();

		List<IProject> childProjects = Stream.of(
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
			project -> JavaCore.create(project) != null
		).filter(
			project -> JavaCore.create(
				project
			).isOpen()
		).filter(
			project -> location.isPrefixOf(project.getLocation())
		).collect(
			Collectors.toList()
		);

		return childProjects;
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return null;
	}

	@Override
	public IFolder[] getSourceFolders() {
		return new IFolder[0];
	}

	@Override
	public List<IPath> getTargetPlatformArtifacts() {
		return Collections.emptyList();
	}

	@Override
	public void watch(Set<IProject> childProjects) {
	}

}