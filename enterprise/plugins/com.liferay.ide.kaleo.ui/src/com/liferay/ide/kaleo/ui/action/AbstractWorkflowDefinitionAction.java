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

import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionEntry;
import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionsFolder;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractWorkflowDefinitionAction extends SelectionProviderAction {

	public AbstractWorkflowDefinitionAction(ISelectionProvider selectionProvider, String text) {
		this(null, selectionProvider, text);
	}

	public AbstractWorkflowDefinitionAction(Shell shell, ISelectionProvider selectionProvider, String text) {
		super(selectionProvider, text);

		this.shell = shell;
		setEnabled(false);
	}

	public boolean accept(Object node) {
		if (node instanceof WorkflowDefinitionEntry || node instanceof WorkflowDefinitionsFolder) {
			return true;
		}

		return false;
	}

	public Shell getShell() {
		return shell;
	}

	public abstract void perform(Object node);

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

			if (obj instanceof WorkflowDefinitionEntry) {
				WorkflowDefinitionEntry node = (WorkflowDefinitionEntry)obj;

				if (accept(node)) {
					enabled = true;
				}
			}
			else if (obj instanceof WorkflowDefinitionsFolder) {
				WorkflowDefinitionsFolder node = (WorkflowDefinitionsFolder)obj;

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