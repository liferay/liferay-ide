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

package com.liferay.ide.server.ui.action;

import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.ui.navigator.PropertiesFile;
import com.liferay.ide.ui.editor.LiferayPropertiesEditor;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.ide.FileStoreEditorInput;

/**
 * @author Gregory Amerson
 */
public class EditPropertiesFileAction extends SelectionProviderAction {

	public EditPropertiesFileAction(ISelectionProvider sp) {
		super(sp, "Edit Properties File");
	}

	public EditPropertiesFileAction(ISelectionProvider selectionProvider, String text) {
		this(null, selectionProvider, text);
	}

	public EditPropertiesFileAction(Shell shell, ISelectionProvider selectionProvider, String text) {
		super(selectionProvider, text);

		this.shell = shell;

		setEnabled(false);
	}

	public boolean accept(Object node) {
		return node instanceof PropertiesFile;
	}

	public Shell getShell() {
		return shell;
	}

	public void perform(Object entry) {
		if (entry instanceof PropertiesFile) {
			PropertiesFile workflowEntry = (PropertiesFile)entry;

			FileStoreEditorInput editorInput = new FileStoreEditorInput(workflowEntry.getFileStore());

			IWorkbench workbench = PlatformUI.getWorkbench();

			IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();

			IWorkbenchPage page = activeWorkbenchWindow.getActivePage();

			try {
				page.openEditor(editorInput, LiferayPropertiesEditor.ID);
			}
			catch (PartInitException pie) {
				LiferayServerUI.logError("Error opening properties editor.", pie);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void run() {
		Iterator iterator = getStructuredSelection().iterator();

		if (!iterator.hasNext()) {
			return;
		}

		Object obj = iterator.next();

		if (accept(obj)) {
			perform(obj);
		}

		selectionChanged(getStructuredSelection());
	}

	@SuppressWarnings("rawtypes")
	public void selectionChanged(IStructuredSelection sel) {
		if (sel.isEmpty()) {
			setEnabled(false);
			return;
		}

		boolean enabled = false;

		Iterator iterator = sel.iterator();

		while (iterator.hasNext()) {
			Object obj = iterator.next();

			if (obj instanceof PropertiesFile) {
				PropertiesFile node = (PropertiesFile)obj;

				if (accept(node)) {
					enabled = true;
				}
			}
			else {
				setEnabled(false);
				return;
			}
		}

		setEnabled(enabled);
	}

	protected Shell shell;

}