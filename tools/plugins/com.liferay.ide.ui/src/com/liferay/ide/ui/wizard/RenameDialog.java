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

package com.liferay.ide.ui.wizard;

import com.liferay.ide.ui.LiferayUIPlugin;

import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

/**
 * @author Gregory Amerson
 */
public class RenameDialog extends SelectionStatusDialog {

	/**
	 * Create a new rename dialog instance for the given window.
	 *
	 * @param shell
	 *            The parent of the dialog
	 * @param caseSensitive
	 *            Flags whether dialog will perform case sensitive checks
	 *            against old names
	 * @param names
	 *            Set of names which the user should not be allowed to rename to
	 * @param oldName
	 *            Current name of the item being renamed
	 */
	public RenameDialog(Shell shell, boolean caseSensitive, String[] names, String oldName) {
		super(shell);

		_caseSensitive = caseSensitive;
		initialize();

		if (names != null) {
			for (int i = 0; i < names.length; i++)addOldName(names[i]);
		}

		setOldName(oldName);
	}

	/**
	 * Create a new rename dialog instance for the given window.
	 *
	 * @param shell
	 *            The parent of the dialog
	 * @param oldName
	 *            Current name of the item being renamed
	 */
	public RenameDialog(Shell shell, String oldName) {
		super(shell);

		_caseSensitive = false;
		initialize();
		setOldName(oldName);
	}

	public void addOldName(String oldName) {
		if (!_oldNames.contains(oldName)) {
			_oldNames.add(oldName);
		}
	}

	public String getNewName() {
		return _newName;
	}

	public void initialize() {
		_oldNames = new ArrayList();
		setStatusLineAboveButtons(true);
	}

	public int open() {
		_text.setText(_oldName);
		_text.selectAll();
		Button okButton = getButton(IDialogConstants.OK_ID);

		_status = new Status(IStatus.OK, LiferayUIPlugin.PLUGIN_ID, IStatus.OK, "", null);

		updateStatus(_status);

		okButton.setEnabled(false);

		return super.open();
	}

	public void setInputValidator(IInputValidator validator) {
		_fValidator = validator;
	}

	public void setOldName(String oldName) {
		_oldName = oldName;
		addOldName(oldName);

		if (_text != null) {
			_text.setText(oldName);
		}

		_newName = oldName;
	}

	public void setTitle(String title) {
		getShell().setText(title);
	}

	/**
	 * (non-Javadoc)
	 * @see SelectionStatusDialog#computeResult()
	 */
	protected void computeResult() {
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
	}

	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();

		layout.numColumns = 2;
		layout.marginHeight = layout.marginWidth = 9;
		container.setLayout(layout);

		GridData gd = new GridData(GridData.FILL_BOTH);

		container.setLayoutData(gd);

		Label label = new Label(container, SWT.NULL);

		label.setText(Msgs.enterNewName);

		_text = new Text(container, SWT.SINGLE | SWT.BORDER);

		_text.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					_textChanged(_text.getText());
				}

			});

		gd = new GridData(GridData.FILL_HORIZONTAL);

		_text.setLayoutData(gd);

		applyDialogFont(container);

		return container;
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		_newName = _text.getText().trim();
		super.okPressed();
	}

	private void _textChanged(String text) {
		Button okButton = getButton(IDialogConstants.OK_ID);

		if (_fValidator != null) {
			String message = _fValidator.isValid(text);

			if (message != null) {
				_status = new Status(IStatus.ERROR, LiferayUIPlugin.PLUGIN_ID, IStatus.ERROR, message, null);

				updateStatus(_status);

				okButton.setEnabled(false);
				return;
			}
		}

		for (int i = 0; i < _oldNames.size(); i++) {
			if ((_caseSensitive && text.equals(_oldNames.get(i))) ||
				(!_caseSensitive && text.equalsIgnoreCase(_oldNames.get(i).toString()))) {

				_status = new Status(
					IStatus.ERROR, LiferayUIPlugin.PLUGIN_ID, IStatus.ERROR, Msgs.nameAlreadyExists, null);

				updateStatus(_status);

				okButton.setEnabled(false);

				break;
			}

			okButton.setEnabled(true);
			_status = new Status(IStatus.OK, LiferayUIPlugin.PLUGIN_ID, IStatus.OK, "", null);

			updateStatus(_status);
		}
	}

	private boolean _caseSensitive;
	private IInputValidator _fValidator;
	private String _newName;
	private String _oldName;
	private ArrayList _oldNames;
	private IStatus _status;
	private Text _text;

	private static class Msgs extends NLS {

		public static String enterNewName;
		public static String nameAlreadyExists;

		static {
			initializeMessages(RenameDialog.class.getName(), Msgs.class);
		}

	}

}