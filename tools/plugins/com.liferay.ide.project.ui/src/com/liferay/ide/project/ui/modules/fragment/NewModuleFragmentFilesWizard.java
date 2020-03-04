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

package com.liferay.ide.project.ui.modules.fragment;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentFilesOp;

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

/**
 * @author Terry Jia
 */
public class NewModuleFragmentFilesWizard
	extends SapphireWizard<NewModuleFragmentFilesOp> implements INewWizard, IWorkbenchWizard {

	public NewModuleFragmentFilesWizard() {
		super(_createDefaultOp(), DefinitionLoader.sdef(NewModuleFragmentFilesWizard.class).wizard());
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
				final IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, _initialProject);

				if ((bundleProject != null) && bundleProject.isFragmentBundle()) {
					IPath projectLocation = _initialProject.getLocation();

					element().setLocation(PathBridge.create(projectLocation.removeLastSegments(1)));
				}
			}
		}
	}

	private static NewModuleFragmentFilesOp _createDefaultOp() {
		return NewModuleFragmentFilesOp.TYPE.instantiate();
	}

	private IProject _initialProject;

}