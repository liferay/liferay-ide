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

package com.liferay.ide.kaleo.ui.wizard;

import com.liferay.ide.kaleo.core.op.NewWorkflowDefinitionOp;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * @author Gregory Amerson
 */
public class NewWorkflowDefinitionWizard
	extends SapphireWizard<NewWorkflowDefinitionOp> implements INewWizard, IWorkbenchWizard {

	public NewWorkflowDefinitionWizard() {
		this(null);
	}

	public NewWorkflowDefinitionWizard(NewWorkflowDefinitionOp modelElement) {
		super(_getModelElement(modelElement), _loaderSedf.wizard("newWorkflowDefinitionWizard"));
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		if (selection instanceof IStructuredSelection) {
			IProject selectedProject = null;

			IContainer selectedFolder = null;

			IStructuredSelection sel = (IStructuredSelection)selection;

			Object selectedObject = sel.getFirstElement();

			if (selectedObject instanceof IProject) {
				selectedProject = (IProject)selectedObject;
			}
			else if (selectedObject instanceof IJavaProject) {
				selectedProject = ((IJavaProject)selectedObject).getProject();
			}
			else if (selectedObject instanceof IContainer) {
				selectedFolder = (IContainer)selectedObject;

				selectedProject = selectedFolder.getProject();
			}
			else if (selectedObject instanceof IFile) {
				selectedFolder = ((IFile)selectedObject).getParent();

				selectedProject = selectedFolder.getProject();
			}

			if (selectedProject != null) {
				NewWorkflowDefinitionOp op = element().nearest(NewWorkflowDefinitionOp.class);

				op.setProject(selectedProject.getName());

				if (selectedFolder != null) {
					op.setFolder(selectedFolder.getProjectRelativePath().toPortableString());
				}
			}
		}
	}

	@Override
	protected void performPostFinish() {
		Value<String> filePath = element().getNewFilePath();

		String newFilePath = filePath.content(false);

		if (newFilePath != null) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();

			IWorkspaceRoot workspaceRoot = workspace.getRoot();

			openFileEditors(workspaceRoot.getFile(new Path(newFilePath)));
		}
	}

	private static NewWorkflowDefinitionOp _getModelElement(NewWorkflowDefinitionOp modelElement) {
		if (modelElement == null) {
			modelElement = NewWorkflowDefinitionOp.TYPE.instantiate();
		}

		return modelElement;
	}

	private static DefinitionLoader _loader = DefinitionLoader.context(NewWorkflowDefinitionWizard.class);
	private static DefinitionLoader _loaderSedf = _loader.sdef(
		"com.liferay.ide.kaleo.ui.wizard.WorkflowDefinitionWizards");

}