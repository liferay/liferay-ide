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

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.util.ProjectModule;

/**
 * @author Gregory Amerson
 */
public class BundleModulelDelegate extends ProjectModule {

	public BundleModulelDelegate(IProject project) {
		super(project);
	}

	@Override
	public IModuleResource[] members() throws CoreException {
		List<IModuleResource> retval = new ArrayList<>();

		IModuleResource[] members = super.members();

		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, getProject());

		for (IModuleResource moduleResource : members) {
			IPath relativePath = moduleResource.getModuleRelativePath();

			IPath path = relativePath.append(moduleResource.getName());

			if ((bundleProject != null) && bundleProject.filterResource(path)) {
				continue;
			}

			retval.add(moduleResource);
		}

		return retval.toArray(new IModuleResource[0]);
	}

}