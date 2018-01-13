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

import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.editor.WorkflowDefinitionEditor;
import com.liferay.ide.kaleo.ui.editor.WorkflowDefinitionEditorInput;
import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionEntry;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Gregory Amerson
 */
public class EditWorkflowDefinitionAction extends AbstractWorkflowDefinitionAction {

	public EditWorkflowDefinitionAction(ISelectionProvider sp) {
		super(sp, "Edit Kaleo workflow");
	}

	@Override
	public void perform(Object entry) {
		if (entry instanceof WorkflowDefinitionEntry) {
			WorkflowDefinitionEntry workflowEntry = (WorkflowDefinitionEntry)entry;

			IWorkbench workBench = PlatformUI.getWorkbench();

			IWorkbenchWindow wbWindow = workBench.getActiveWorkbenchWindow();

			IWorkbenchPage page = wbWindow.getActivePage();

			IEditorPart[] dirtyEditors = page.getDirtyEditors();

			for (IEditorPart dirtyEditor : dirtyEditors) {
				IEditorInput editorInput = dirtyEditor.getEditorInput();

				if (editorInput instanceof WorkflowDefinitionEditorInput) {
					WorkflowDefinitionEditorInput dirtyWorkflowEditorInput = (WorkflowDefinitionEditorInput)editorInput;

					boolean opened = dirtyWorkflowEditorInput.getName().contains(workflowEntry.getName());

					if (opened) {
						IEditorSite editorSite = dirtyEditor.getEditorSite();

						boolean saveOld = MessageDialog.openQuestion(
							editorSite.getShell(), "Save " + dirtyWorkflowEditorInput.getName(),
							"Do you want to save contents of this editor?");

						page.closeEditor(dirtyEditor, saveOld);
					}
				}
			}

			try {
				WorkflowDefinitionEditorInput editorInput = new WorkflowDefinitionEditorInput(workflowEntry);

				IEditorPart editor = page.openEditor(
					editorInput, WorkflowDefinitionEditor.EDITOR_ID, true, IWorkbenchPage.MATCH_INPUT);

				editor.addPropertyListener(
					new IPropertyListener() {

						public void propertyChanged(Object source, int propId) {
							if (source.equals(editor) && (propId == WorkflowDefinitionEditor.propUpdateVersion)) {
								workflowEntry.getParent().clearCache();
								((CommonViewer)EditWorkflowDefinitionAction.this.getSelectionProvider()).refresh(true);
							}
						}

					});
			}
			catch (PartInitException pie) {
				KaleoUI.logError("Error opening kaleo workflow editor.", pie);
			}
		}
	}

}