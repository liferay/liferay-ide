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

package com.liferay.ide.project.core.modules.ext;

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IProjectBuilder;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Seiphon Wang
 */
public class ModuleExtProjectNameSelectionChangedListener extends ModuleExtProjectNameListener {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		_updateOriginalModules(op(event));
	}

	protected NewModuleExtFilesOp op(PropertyContentEvent event) {
		Property property = event.property();

		Element element = property.element();

		return element.nearest(NewModuleExtFilesOp.class);
	}

	private void _updateOriginalModules(NewModuleExtFilesOp op) {
		IProject project = ProjectUtil.getProject(get(op.getModuleExtProjectName()));

		ILiferayProject extProject = LiferayCore.create(ILiferayProject.class, project);

		IProjectBuilder projectBuilder = extProject.adapt(IProjectBuilder.class);

		if (projectBuilder == null) {
			ProjectCore.logWarning("Please wait for synchronized jobs to finish.");

			return;
		}

		List<Artifact> dependencies = projectBuilder.getDependencies("originalModule");

		if (!dependencies.isEmpty()) {
			Artifact artifact = dependencies.get(0);

			File sourceFile = artifact.getSource();

			if (FileUtil.exists(sourceFile)) {
				op.setSourceFileURI(sourceFile.toURI());
			}
			else {
				op.setSourceFileURI(null);
			}

			op.setOriginalModuleName(artifact.getArtifactId());
			op.setOriginalModuleVersion(artifact.getVersion());
		}

		op.setProjectName(project.getName());

		IPath projectLocation = project.getLocation();

		op.setLocation(PathBridge.create(projectLocation.removeLastSegments(1)));
	}

}