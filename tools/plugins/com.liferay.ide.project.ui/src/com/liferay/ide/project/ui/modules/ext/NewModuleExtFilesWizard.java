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

package com.liferay.ide.project.ui.modules.ext;

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IProjectBuilder;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.modules.ext.NewModuleExtFilesOp;

import java.io.File;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import org.osgi.framework.Version;

/**
 * @author Terry Jia
 */
public class NewModuleExtFilesWizard
	extends SapphireWizard<NewModuleExtFilesOp> implements INewWizard, IWorkbenchWizard {

	public NewModuleExtFilesWizard() {
		super(_createDefaultOp(), DefinitionLoader.sdef(NewModuleExtFilesWizard.class).wizard());
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		if ((selection != null) && !selection.isEmpty()) {
			final Object element = selection.getFirstElement();

			if (element instanceof IResource) {
				_initialProject = ((IResource)element).getProject();
			}
			else if (element instanceof IJavaProject) {
				_initialProject = ((IJavaProject)element).getProject();
			}
			else if (element instanceof IPackageFragment) {
				IResource resource = ((IPackageFragment)element).getResource();

				_initialProject = resource.getProject();
			}
			else if (element instanceof IJavaElement) {
				IResource resource = ((IJavaElement)element).getResource();

				_initialProject = resource.getProject();
			}

			if (_initialProject != null) {
				ILiferayProject extProject = LiferayCore.create(ILiferayProject.class, _initialProject);

				IProjectBuilder builder = extProject.adapt(IProjectBuilder.class);

				List<Artifact> dependencies = builder.getDependencies("originalModule");

				if (!dependencies.isEmpty()) {
					Artifact artifact = dependencies.get(0);

					File sourceFile = artifact.getSource();

					if (FileUtil.exists(sourceFile)) {
						NewModuleExtFilesOp moduleExtFilesOp = element();

						moduleExtFilesOp.setSourceFileURI(sourceFile.toURI());
						moduleExtFilesOp.setOriginalModuleName(artifact.getArtifactId());
						moduleExtFilesOp.setOriginalModuleVersion(new Version(artifact.getVersion()));
						moduleExtFilesOp.setProjectName(_initialProject.getName());

						IPath projectLocation = _initialProject.getLocation();

						moduleExtFilesOp.setLocation(PathBridge.create(projectLocation.removeLastSegments(1)));
					}
				}
			}
		}
	}

	private static NewModuleExtFilesOp _createDefaultOp() {
		return NewModuleExtFilesOp.TYPE.instantiate();
	}

	private IProject _initialProject;

}