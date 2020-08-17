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

package com.liferay.ide.project.ui.modules;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.modules.NewLiferayComponentOp;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.UIUtil;

import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.ide.IDE;

/**
 * @author Simon Jiang
 * @author Charles Wu
 */
public class NewLiferayComponentWizard
	extends SapphireWizard<NewLiferayComponentOp> implements INewWizard, IWorkbenchWizard, SapphireContentAccessor {

	public NewLiferayComponentWizard() {
		super(_createDefaultOp(), DefinitionLoader.sdef(NewLiferayComponentWizard.class).wizard());
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		if ((selection != null) && !selection.isEmpty()) {
			final Object element = selection.getFirstElement();

			if (element instanceof IResource) {
				IResource resourceElement = (IResource)element;

				_initialProject = resourceElement.getProject();
			}
			else if (element instanceof IJavaProject) {
				IJavaProject javaElement = (IJavaProject)element;

				_initialProject = javaElement.getProject();
			}
			else if (element instanceof IPackageFragment) {
				_initialPackage = (IPackageFragment)element;

				IJavaElement javaElement = (IJavaElement)element;

				IResource resource = javaElement.getResource();

				_initialProject = resource.getProject();
			}
			else if (element instanceof IPackageFragmentRoot) {
				_initialPackageRoot = (IPackageFragmentRoot)element;

				IJavaProject javaProject = _initialPackageRoot.getJavaProject();

				_initialProject = javaProject.getProject();
			}
			else if (element instanceof IJavaElement) {
				IJavaElement javaElement = (IJavaElement)element;

				IResource resource = javaElement.getResource();

				_initialProject = resource.getProject();
			}

			if (_initialProject != null) {
				final IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, _initialProject);

				if ((bundleProject != null) && Objects.equals("jar", bundleProject.getBundleShape()) &&
					!bundleProject.isFragmentBundle()) {

					element().setProjectName(_initialProject.getName());

					if (_initialPackage != null) {
						element().setPackageName(_initialPackage.getElementName());
					}
				}
			}
		}
	}

	@Override
	public void performPostFinish() {
		final NewLiferayComponentOp op = element().nearest(NewLiferayComponentOp.class);

		IProject currentProject = CoreUtil.getProject(get(op.getProjectName()));

		if (currentProject == null) {
			return;
		}

		IJavaProject javaProject = JavaCore.create(currentProject);

		if (javaProject == null) {
			return;
		}

		try {
			String componentClass = get(op.getComponentClassName());
			String packageName = SapphireUtil.getText(op.getPackageName());

			final IType type = javaProject.findType(packageName, componentClass);

			if (type == null) {
				return;
			}

			final IFile classFile = (IFile)type.getResource();

			if (classFile != null) {
				UIUtil.async(
					new Runnable() {

						public void run() {
							try {
								IWorkbenchPage page = UIUtil.getActivePage();

								IDE.openEditor(page, classFile, true);
							}
							catch (Exception e) {
								ProjectUI.logError(e);
							}
						}

					});
			}
		}
		catch (Exception e) {
			ProjectUI.logError(e);
		}
	}

	private static NewLiferayComponentOp _createDefaultOp() {
		return NewLiferayComponentOp.TYPE.instantiate();
	}

	private IPackageFragment _initialPackage;
	private IPackageFragmentRoot _initialPackageRoot;
	private IProject _initialProject;

}