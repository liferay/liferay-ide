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

package com.liferay.ide.hook.ui.wizard;

import com.liferay.ide.hook.core.operation.NewServiceWrapperClassDataModelProvider;
import com.liferay.ide.hook.core.operation.NewServiceWrapperClassOperation;
import com.liferay.ide.hook.ui.HookUI;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class NewServiceWrapperClassDialog extends NewEventActionClassDialog {

	public NewServiceWrapperClassDialog(Shell shell, IDataModel model, String serviceType, String wrapperType) {
		super(shell, model);

		this.serviceType = serviceType;
		this.wrapperType = wrapperType;
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		Button button = super.createButton(parent, id, label, defaultButton);

		if (IDialogConstants.OK_ID == id) {
			int begin = this.serviceType.lastIndexOf('.') + 1;

			String defaultClassname = "Ext" + this.serviceType.substring(begin, this.serviceType.length());

			classText.setText(defaultClassname);

			button.setEnabled(true);
		}

		return button;
	}

	protected void createSuperclassGroup(Composite parent) {

		// superclass

		superLabel = new Label(parent, SWT.LEFT);

		superLabel.setText(J2EEUIMessages.SUPERCLASS_LABEL);
		superLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		superclassText = new Text(parent, SWT.SINGLE | SWT.BORDER);

		superclassText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		superclassText.setText(wrapperType);
		superclassText.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					qualifiedSuperclassname = classText.getText();
				}

			});

		superclassText.setEditable(false);

		superclassText.setEditable(false);

		new Label(parent, SWT.NONE);
	}

	@Override
	protected void okPressed() {

		// Create the class

		IDataModel dataModel = DataModelFactory.createDataModel(
			new NewServiceWrapperClassDataModelProvider(model, getQualifiedClassname(), superclassText.getText()));

		NewServiceWrapperClassOperation operation = new NewServiceWrapperClassOperation(dataModel);

		try {
			operation.execute(null, null);
		}
		catch (ExecutionException ee) {
			HookUI.logError("Unable to create class", ee);
		}

		setReturnCode(OK);

		close();
	}

	protected String serviceType;
	protected Text superclassText;
	protected String wrapperType;

}