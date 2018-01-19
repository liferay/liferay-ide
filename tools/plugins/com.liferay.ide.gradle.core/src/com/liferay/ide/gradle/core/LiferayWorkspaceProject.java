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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.BaseLiferayProject;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.IWorkspaceProjectBuilder;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

/**
 * @author Andy Wu
 */
public class LiferayWorkspaceProject extends BaseLiferayProject {

	public LiferayWorkspaceProject(IProject project) {
		super(project);
	}

	@Override
	public <T> T adapt(Class<T> adapterType) {
		if (ILiferayPortal.class.equals(adapterType)) {

			// check for bundles/ directory

			IFolder bundlesFolder = getProject().getFolder("bundles");

			if (FileUtil.exists(bundlesFolder)) {
				PortalBundle portalBundle = LiferayServerCore.newPortalBundle(bundlesFolder.getLocation());

				if (portalBundle != null) {
					return adapterType.cast(portalBundle);
				}
			}
		}

		if (IProjectBuilder.class.equals(adapterType) || IWorkspaceProjectBuilder.class.equals(adapterType)) {
			IProjectBuilder projectBuilder = new GradleProjectBuilder(getProject());

			return adapterType.cast(projectBuilder);
		}

		return super.adapt(adapterType);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return null;
	}

	@Override
	public IFolder[] getSourceFolders() {
		return null;
	}

	@Override
	public IFolder[] getSourceFolders() {
		return null;
	}

}