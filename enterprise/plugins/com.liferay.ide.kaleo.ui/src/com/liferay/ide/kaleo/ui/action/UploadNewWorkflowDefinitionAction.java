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

package com.liferay.ide.kaleo.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.kaleo.core.IKaleoConnection;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionsFolder;
import com.liferay.ide.kaleo.ui.util.KaleoUtil;
import com.liferay.ide.kaleo.ui.util.UploadWorkflowFileJob;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Gregory Amerson
 */
public class UploadNewWorkflowDefinitionAction extends AbstractWorkflowDefinitionAction {

	public UploadNewWorkflowDefinitionAction(ISelectionProvider sp) {
		super(sp, "Upload new workflow...");
	}

	@Override
	public void perform(Object node) {
		if (getSelectionProvider() instanceof CommonViewer && node instanceof WorkflowDefinitionsFolder) {
			IFile workspaceFile = promptForWorkspaceFile();

			if ((workspaceFile == null) || !FileUtil.exists(workspaceFile)) {
				return;
			}

			String errorMsgs = KaleoUtil.checkWorkflowDefinitionForErrors(workspaceFile);

			if (!CoreUtil.empty(errorMsgs)) {
				MessageDialog.openError(
					Display.getDefault().getActiveShell(), "Upload Kaleo Workflow",
					"Unable to upload kaleo workflow:\n\n" + errorMsgs);

				return;
			}

			WorkflowDefinitionsFolder definitionsFolder = (WorkflowDefinitionsFolder)node;

			IKaleoConnection kaleoConnection = KaleoCore.getKaleoConnection(definitionsFolder.getParent());

			Runnable runnable = new Runnable() {

				public void run() {
					CommonViewer viewer = (CommonViewer)getSelectionProvider();

					Runnable runnable = new Runnable() {

						public void run() {
							definitionsFolder.clearCache();
							viewer.refresh(true);
						}

					};

					Display.getDefault().asyncExec(runnable);
				}

			};

			Job upload = new UploadWorkflowFileJob(kaleoConnection, workspaceFile, runnable);

			upload.schedule();
		}
	}

	protected ISelectionStatusValidator getContainerDialogSelectionValidator() {
		return new ISelectionStatusValidator() {

			public IStatus validate(Object[] selection) {
				if (ListUtil.isNotEmpty(selection) && (selection[0] != null) &&
					 !(selection[0] instanceof IProject) && !(selection[0] instanceof IFolder)) {

					return Status.OK_STATUS;
				}

				return KaleoUI.createErrorStatus("Choose a valid project file");
			}

		};
	}

	protected ViewerFilter getContainerDialogViewerFilter() {
		return new ViewerFilter() {

			public boolean select(Viewer viewer, Object parent, Object element) {
				if (element instanceof IProject) {
					return true;
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

	protected IFile promptForWorkspaceFile() {
		ISelectionStatusValidator validator = getContainerDialogSelectionValidator();

		ViewerFilter filter = getContainerDialogViewerFilter();

		ITreeContentProvider contentProvider = new WorkbenchContentProvider();

		IWorkbench workBench = PlatformUI.getWorkbench();

		ILabelProvider labelProvider = new DecoratingLabelProvider(
			new WorkbenchLabelProvider(), workBench.getDecoratorManager().getLabelDecorator());

		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), labelProvider, contentProvider);

		dialog.setValidator(validator);
		dialog.setTitle("workspace file");
		dialog.setMessage("workspace file");
		dialog.addFilter(filter);
		dialog.setInput(ResourcesPlugin.getWorkspace());

		if (dialog.open() == Window.OK) {
			Object element = dialog.getFirstResult();

			try {
				if (element instanceof IFile) {
					IFile file = (IFile)element;

					return file;
				}
			}
			catch (Exception ex) {

				// Do nothing

			}
		}

		return null;
	}

}