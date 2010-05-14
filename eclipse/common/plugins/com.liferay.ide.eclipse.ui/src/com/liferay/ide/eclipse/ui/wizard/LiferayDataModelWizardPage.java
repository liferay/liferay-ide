/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.ui.wizard;

import com.liferay.ide.eclipse.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.viewsupport.IViewPartInputProvider;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public abstract class LiferayDataModelWizardPage extends DataModelWizardPage {

	public LiferayDataModelWizardPage(IDataModel model, String pageName, String title, ImageDescriptor titleImage) {
		super(model, pageName, title, titleImage);
	}

	protected IJavaElement getInitialJavaElement(ISelection selection) {
		IJavaElement jelem = null;

		if (selection != null && !selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object selectedElement = ((IStructuredSelection) selection).getFirstElement();

			jelem = getJavaElement(selectedElement);

			if (jelem == null) {
				IResource resource = getResource(selectedElement);

				if (resource != null && resource.getType() != IResource.ROOT) {
					while (jelem == null && resource.getType() != IResource.PROJECT) {
						resource = resource.getParent();

						jelem = (IJavaElement) resource.getAdapter(IJavaElement.class);
					}

					if (jelem == null) {
						jelem = JavaCore.create(resource); // java project
					}
				}
			}
		}

		if (jelem == null) {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

			if (window == null) {
				return null;
			}

			IWorkbenchPart part = window.getActivePage().getActivePart();

			if (part instanceof ContentOutline) {
				part = window.getActivePage().getActiveEditor();
			}

			if (part instanceof IViewPartInputProvider) {
				Object elem = ((IViewPartInputProvider) part).getViewPartInput();

				if (elem instanceof IJavaElement) {
					jelem = (IJavaElement) elem;
				}
			}
		}

		if (jelem == null || jelem.getElementType() == IJavaElement.JAVA_MODEL) {
			try {
				IJavaProject[] projects = JavaCore.create(getWorkspaceRoot()).getJavaProjects();

				if (projects.length == 1) {
					jelem = projects[0];
				}
			}
			catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
		}

		return jelem;
	}

	protected IJavaElement getJavaElement(Object obj) {
		if (obj == null) {
			return null;
		}

		if (obj instanceof IJavaElement) {
			return (IJavaElement) obj;
		}

		if (obj instanceof IAdaptable) {
			return (IJavaElement) ((IAdaptable) obj).getAdapter(IJavaElement.class);
		}

		return (IJavaElement) Platform.getAdapterManager().getAdapter(obj, IJavaElement.class);
	}

	protected IResource getResource(Object obj) {
		if (obj == null) {
			return null;
		}

		if (obj instanceof IResource) {
			return (IResource) obj;
		}

		if (obj instanceof IAdaptable) {
			return (IResource) ((IAdaptable) obj).getAdapter(IResource.class);
		}

		return (IResource) Platform.getAdapterManager().getAdapter(obj, IResource.class);
	}

	protected IProject getSelectedProject() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (window == null) {
			return null;
		}

		ISelection selection = window.getSelectionService().getSelection();

		if (selection == null) {
			return null;
		}

		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}

		IJavaElement element = getInitialJavaElement(selection);

		if (element != null && element.getJavaProject() != null) {
			return element.getJavaProject().getProject();
		}

		IStructuredSelection stucturedSelection = (IStructuredSelection) selection;

		if (stucturedSelection.getFirstElement() instanceof EObject) {
			return ProjectUtilities.getProject(stucturedSelection.getFirstElement());
		}

		return null;
	}

	protected IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	protected String initializeProjectList(Combo projectCombo, IDataModel dataModel) {
		IProject[] workspaceProjects = CoreUtil.getAllProjects();

		List<String> items = new ArrayList<String>();

		for (int i = 0; i < workspaceProjects.length; i++) {
			IProject project = workspaceProjects[i];

			if (isProjectValid(project)) {
				items.add(project.getName());
			}
		}

		if (items.isEmpty()) {
			return null;
		}

		String[] names = new String[items.size()];

		for (int i = 0; i < items.size(); i++) {
			names[i] = (String) items.get(i);
		}

		projectCombo.setItems(names);

		IProject selectedProject = null;

		try {
			if (dataModel != null) {
				String projectNameFromModel =
					dataModel.getStringProperty(IArtifactEditOperationDataModelProperties.COMPONENT_NAME);

				if (projectNameFromModel != null && projectNameFromModel.length() > 0) {
					selectedProject = CoreUtil.getProject(projectNameFromModel);
				}
			}
		}
		catch (Exception e) {
		}

		try {
			if (selectedProject == null) {
				selectedProject = getSelectedProject();
			}

			if (selectedProject != null && selectedProject.isAccessible() &&
				selectedProject.hasNature(IModuleConstants.MODULE_NATURE_ID)) {

				projectCombo.setText(selectedProject.getName());

				validateProjectRequirements(selectedProject);

				dataModel.setProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME, selectedProject.getName());
			}
		}
		catch (CoreException ce) {
			// Ignore
		}

		if ((projectCombo.getText() == null || projectCombo.getText().length() == 0) && names[0] != null) {
			projectCombo.setText(names[0]);

			validateProjectRequirements(CoreUtil.getProject(names[0]));

			dataModel.setProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME, names[0]);
		}

		return names[0];
	}

	protected abstract boolean isProjectValid(IProject project);

	protected abstract void validateProjectRequirements(IProject project);

}
