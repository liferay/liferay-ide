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

package com.liferay.ide.server.ui.portal;

import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.ILaunchable;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.ModuleArtifactAdapterDelegate;
import org.eclipse.wst.server.core.util.WebResource;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class BundleModuleArtifactAdapterDelegate extends ModuleArtifactAdapterDelegate implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class<?>[] {ILaunchable.class};
	}

	@Override
	public IModuleArtifact getModuleArtifact(Object obj) {
		IProject project = null;

		if (obj instanceof IProject) {
			project = (IProject)obj;
		}
		else if (obj instanceof IAdaptable) {
			project = (IProject)((IAdaptable)obj).getAdapter(IProject.class);
		}

		if (project != null) {
			if (ProjectUtil.is7xServerDeployableProject(project)) {
				return new WebResource(_getModule(project), project.getProjectRelativePath());
			}
		}

		return null;
	}

	private IModule _getModule(IProject project) {
		IModule[] modules = ServerUtil.getModules("liferay.bundle");

		for (IModule module : modules) {
			if ((project == null) || project.equals(module.getProject())) {
				return module;
			}
		}

		return null;
	}

}