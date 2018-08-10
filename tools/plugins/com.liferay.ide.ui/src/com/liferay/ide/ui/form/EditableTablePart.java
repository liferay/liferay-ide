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

package com.liferay.ide.ui.form;

import com.liferay.ide.ui.wizard.RenameDialog;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Gregory Amerson
 */
public class EditableTablePart extends TablePart {

	/**
	 * Constructor for EditableTablePart.
	 *
	 * @param buttonLabels
	 */
	public EditableTablePart(String[] buttonLabels) {
		super(buttonLabels);
	}

	public IAction getRenameAction() {
		if (_renameAction == null) {
			_renameAction = new RenameAction();
		}

		return _renameAction;
	}

	public boolean isEditable() {
		return _editable;
	}

	public void setEditable(boolean editable) {
		_editable = editable;
	}

	public class NameModifier implements ICellModifier {

		public boolean canModify(Object object, String property) {
			return true;
		}

		public Object getValue(Object object, String property) {
			return object.toString();
		}

		public void modify(Object object, String property, Object value) {
			entryModified(object, value.toString());
		}

	}

	public class RenameAction extends Action {

		public RenameAction() {
			super(Msgs.renameAction);
		}

		public void run() {
			_rename();
		}

	}

	protected StructuredViewer createStructuredViewer(Composite parent, int style, FormToolkit toolkit) {
		TableViewer tableViewer = (TableViewer)super.createStructuredViewer(parent, style, toolkit);

		return tableViewer;
	}

	protected void entryModified(Object entry, String value) {
	}

	private void _rename() {
		TableViewer viewer = getTableViewer();

		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();

		if ((selection.size() == 1) && isEditable()) {
			Object obj = selection.getFirstElement();

			String oldName = obj.toString();

			RenameDialog dialog = new RenameDialog(getControl().getShell(), oldName);

			dialog.create();

			Shell shell = dialog.getShell();

			shell.setText(Msgs.renameTitle);
			shell.setSize(300, 150);

			if (dialog.open() == Window.OK) {
				entryModified(obj, dialog.getNewName());
			}
		}
	}

	private boolean _editable;
	private Action _renameAction;

	private static class Msgs extends NLS {

		public static String renameAction;
		public static String renameTitle;

		static {
			initializeMessages(EditableTablePart.class.getName(), Msgs.class);
		}

	}

}