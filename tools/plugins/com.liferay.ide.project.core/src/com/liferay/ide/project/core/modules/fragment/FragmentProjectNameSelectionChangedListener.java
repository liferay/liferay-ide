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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IProjectBuilder;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Seiphon Wang
 * @author Ethan Sun
 */
public class FragmentProjectNameSelectionChangedListener extends FragmentProjectNameListener {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		_updateProject(op(event));
	}

	@Override
	protected NewModuleFragmentFilesOp op(PropertyContentEvent event) {
		Property property = event.property();

		Element element = property.element();

		return element.nearest(NewModuleFragmentFilesOp.class);
	}

	private void _updateProject(NewModuleFragmentFilesOp op) {
		IProject project = ProjectUtil.getProject(get(op.getProjectName()));

		ILiferayProject fragmentProject = LiferayCore.create(ILiferayProject.class, project);

		IProjectBuilder projectBuilder = fragmentProject.adapt(IProjectBuilder.class);

		if (projectBuilder == null) {
			ProjectCore.logWarning("Please wait for synchronized jobs to finish.");

			return;
		}

		Map<String, String> fragmentProjectInfo = ProjectUtil.getFragmentProjectInfo(project);

		op.setHostOsgiBundle(fragmentProjectInfo.get("HostOSGiBundleName"));
		op.setLiferayRuntimeName(fragmentProjectInfo.get("LiferayRuntimeName"));

		IPath projectLocation = project.getLocation();

		op.setLocation(PathBridge.create(projectLocation.removeLastSegments(1)));

		ElementList<OverrideFilePath> overrideFiles = op.getOverrideFiles();

		overrideFiles.clear();
	}

}