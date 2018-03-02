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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.ui.LiferayUIPlugin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.viewsupport.IViewPartInputProvider;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
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

	protected ISelectionStatusValidator getContainerDialogSelectionValidator() {
		return new ISelectionStatusValidator() {

			public IStatus validate(Object[] selection) {
				if (ListUtil.isNotEmpty(selection) && (selection[0] != null) &&
					 !(selection[0] instanceof IProject) && !(selection[0] instanceof IFolder)) {

					return Status.OK_STATUS;
				}

				return LiferayUIPlugin.createErrorStatus(Msgs.chooseValidProjectFile);
			}

		};
	}

	protected ViewerFilter getContainerDialogViewerFilter() {
		return new ViewerFilter() {

			public boolean select(Viewer viewer, Object parent, Object element) {
				if (element instanceof IProject) {
					IProject project = (IProject)element;

					return project.getName().equals(
						model.getProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME));
				}
				else if (element instanceof IFolder) {
					return true;
				}
				else if (element instanceof IFile) {
					return true;
				}

				return false;
			}

		};
	}

	protected IJavaElement getInitialJavaElement(ISelection selection) {
		IJavaElement jelem = null;

		if ((selection != null) && !selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object selectedElement = ((IStructuredSelection)selection).getFirstElement();

			jelem = getJavaElement(selectedElement);

			if (jelem == null) {
				IResource resource = getResource(selectedElement);

				if ((resource != null) && (resource.getType() != IResource.ROOT)) {
					while ((jelem == null) && (resource.getType() != IResource.PROJECT)) {
						resource = resource.getParent();

						jelem = (IJavaElement)resource.getAdapter(IJavaElement.class);
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
				Object elem = ((IViewPartInputProvider)part).getViewPartInput();

				if (elem instanceof IJavaElement) {
					jelem = (IJavaElement)elem;
				}
			}
		}

		if ((jelem == null) || (jelem.getElementType() == IJavaElement.JAVA_MODEL)) {
			try {
				IJavaProject[] projects = JavaCore.create(getWorkspaceRoot()).getJavaProjects();

				if (projects.length == 1) {
					jelem = projects[0];
				}
			}
			catch (JavaModelException jme) {
				JavaPlugin.log(jme);
			}
		}

		return jelem;
	}

	protected IJavaElement getJavaElement(Object obj) {
		if (obj == null) {
			return null;
		}

		if (obj instanceof IJavaElement) {
			return (IJavaElement)obj;
		}

		if (obj instanceof IAdaptable) {
			return (IJavaElement)((IAdaptable)obj).getAdapter(IJavaElement.class);
		}

		return (IJavaElement)Platform.getAdapterManager().getAdapter(obj, IJavaElement.class);
	}

	protected IResource getResource(Object obj) {
		if (obj == null) {
			return null;
		}

		if (obj instanceof IResource) {
			return (IResource)obj;
		}

		if (obj instanceof IAdaptable) {
			return (IResource)((IAdaptable)obj).getAdapter(IResource.class);
		}

		return (IResource)Platform.getAdapterManager().getAdapter(obj, IResource.class);
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

		if ((element != null) && (element.getJavaProject() != null)) {
			return element.getJavaProject().getProject();
		}

		IStructuredSelection stucturedSelection = (IStructuredSelection)selection;

		if (stucturedSelection.getFirstElement() instanceof EObject) {
			return ProjectUtilities.getProject(stucturedSelection.getFirstElement());
		}

		return null;
	}

	protected IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	protected void handleFileBrowseButton(final Text text, String title, String message) {
		ISelectionStatusValidator validator = getContainerDialogSelectionValidator();

		ViewerFilter filter = getContainerDialogViewerFilter();

		ITreeContentProvider contentProvider = new WorkbenchContentProvider();

		IDecoratorManager decoratorManager = PlatformUI.getWorkbench().getDecoratorManager();

		ILabelProvider labelProvider = new DecoratingLabelProvider(
			new WorkbenchLabelProvider(), decoratorManager.getLabelDecorator());

		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), labelProvider, contentProvider);

		dialog.setValidator(validator);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.addFilter(filter);

		final IWebProject lrproject = LiferayCore.create(
			IWebProject.class,
			CoreUtil.getProject(
				getDataModel().getStringProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME)));

		// IDE-110

		if (lrproject != null) {
			final IFolder defaultDocroot = lrproject.getDefaultDocrootFolder();

			if (defaultDocroot != null) {
				dialog.setInput(defaultDocroot);

				if (dialog.open() == Window.OK) {
					Object element = dialog.getFirstResult();

					try {
						if (element instanceof IFile) {
							IFile file = (IFile)element;

							final IPath relativePath = file.getFullPath().makeRelativeTo(defaultDocroot.getFullPath());

							text.setText("/" + relativePath.toPortableString());

							// dealWithSelectedContainerResource(container);

						}
					}
					catch (Exception ex) {

						// Do nothing

					}
				}
			}
		}
	}

	protected String initializeProjectList(Combo projectCombo, IDataModel dataModel) {
		IProject[] workspaceProjects = CoreUtil.getAllProjects();

		List<String> items = new ArrayList<>();

		for (int i = 0; i < workspaceProjects.length; i++) {
			IProject project = workspaceProjects[i];

			if (isProjectValid(project)) {
				items.add(project.getName());
			}
		}

		if (ListUtil.isEmpty(items)) {
			return null;
		}

		String[] names = new String[items.size()];

		for (int i = 0; i < items.size(); i++) {
			names[i] = (String)items.get(i);
		}

		projectCombo.setItems(names);

		IProject selectedProject = null;

		try {
			if (dataModel != null) {
				String projectNameFromModel = dataModel.getStringProperty(
					IArtifactEditOperationDataModelProperties.COMPONENT_NAME);

				if ((projectNameFromModel != null) && (projectNameFromModel.length() > 0)) {
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

			if ((selectedProject != null) && selectedProject.isAccessible() &&
				selectedProject.hasNature(IModuleConstants.MODULE_NATURE_ID)) {

				projectCombo.setText(selectedProject.getName());

				validateProjectRequirements(selectedProject);

				dataModel.setProperty(
					IArtifactEditOperationDataModelProperties.PROJECT_NAME, selectedProject.getName());
			}
		}
		catch (CoreException ce) {

			// Ignore

		}

		if (((projectCombo.getText() == null) || (projectCombo.getText().length() == 0)) && (names[0] != null)) {
			projectCombo.setText(names[0]);

			validateProjectRequirements(CoreUtil.getProject(names[0]));

			dataModel.setProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME, names[0]);
		}

		return names[0];
	}

	protected abstract boolean isProjectValid(IProject project);

	protected void validateProjectRequirements(IProject project) {
	}

	private static class Msgs extends NLS {

		public static String chooseValidProjectFile;

		static {
			initializeMessages(LiferayDataModelWizardPage.class.getName(), Msgs.class);
		}

	}

}