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

package com.liferay.ide.eclipse.sdk.pref;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKPlugin;
import com.liferay.ide.eclipse.sdk.util.SDKUtil;
import com.liferay.ide.eclipse.ui.util.SWTUtil;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Greg Amerson
 */
public class AddSDKDialog extends TitleAreaDialog implements ModifyListener {

	protected SDK[] existingSDKs;

	protected String lastLocation;

	protected String lastName;

	protected Text location;

	protected Text name;

	protected SDK sdkToEdit;

	protected Combo serverTargetCombo;

	public AddSDKDialog(Shell parent, SDK[] existingSDKs) {
		super(parent);

		configure(existingSDKs);
	}

	public AddSDKDialog(Shell shell, SDK[] existingSDKs, SDK sdk) {
		super(shell);

		this.sdkToEdit = sdk;

		configure(existingSDKs);
	}

	public String getLocation() {
		return lastLocation;
	}

	public String getName() {
		return lastName;
	}

	public void modifyText(ModifyEvent e) {
		IStatus status = validate();

		if (!status.isOK()) {
			switch (status.getSeverity()) {

			case IStatus.WARNING:
				setMessage(status.getMessage(), IMessageProvider.WARNING);

				break;

			case IStatus.ERROR:

				setMessage(status.getMessage(), IMessageProvider.ERROR);

				this.getButton(IDialogConstants.OK_ID).setEnabled(false);

				break;

			}
		}
		else {
			this.getButton(IDialogConstants.OK_ID).setEnabled(true);

			setMessage(getDefaultMessage(), IMessageProvider.NONE);
		}
	}

	protected void configure(SDK[] existingSdks) {
		setShellStyle(getShellStyle() | SWT.RESIZE);

		this.existingSDKs = existingSdks;

		setTitleImage(ImageDescriptor.createFromURL(
			SDKPlugin.getDefault().getBundle().getEntry("/icons/wizban/sdk_wiz.png")).createImage());
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText((sdkToEdit == null ? "New" : "Edit") + " Liferay SDK");
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);

		getButton(IDialogConstants.OK_ID).setEnabled(false);

		return control;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle((sdkToEdit == null ? "Add" : "Edit") + " Liferay SDK");

		setMessage(getDefaultMessage());

		Composite container = (Composite) SWTUtil.createTopComposite(parent, 3);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		SWTUtil.createLabel(container, "Location", 1);

		location = SWTUtil.createSingleText(container, 1);

		Button browse = SWTUtil.createButton(container, "Browse");
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				doBrowse();
			}

		});
		if (sdkToEdit == null) {
			location.addModifyListener(this);
		}
		else {
			location.setText(sdkToEdit.getLocation().toOSString());
			location.setEnabled(false);

			browse.setEnabled(false);
		}

		SWTUtil.createLabel(container, "Name", 1);

		name = SWTUtil.createSingleText(container, 1);

		if (sdkToEdit != null) {
			name.setText(sdkToEdit.getName());
		}

		name.addModifyListener(this);

		SWTUtil.createLabel(container, "", 1);

		if (sdkToEdit != null) {
			validate();
		}

		// SWTUtil.createLabel(container, "Runtime", 1);
		// serverTargetCombo = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		// serverTargetCombo.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));
		// updateRuntimeItems();
		// Button newServerTargetButton = new Button(container, SWT.NONE);
		// newServerTargetButton.setText("New...");
		// newServerTargetButton.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent e) {
		// boolean isOK = ServerUIUtil.showNewRuntimeWizard(getShell(),
		// IModuleConstants.JST_WEB_MODULE, null,
		// "com.liferay.ide.eclipse.server.tomcat.runtime.60");
		// if (isOK) {
		// updateRuntimeItems();
		// }
		// }
		// });

		return container;
	}

	protected void doBrowse() {
		DirectoryDialog dd = new DirectoryDialog(this.getShell(), SWT.OPEN);
		dd.setText("Select Liferay SDK folder");

		if (CoreUtil.isNullOrEmpty(location.getText())) {
			dd.setFilterPath(location.getText());
		}

		String dir = dd.open();

		if (!CoreUtil.isNullOrEmpty(dir)) {
			location.setText(dir);

			if (CoreUtil.isNullOrEmpty(name.getText())) {
				IPath path = new Path(dir);

				if (path.isValidPath(dir)) {
					name.setText(path.lastSegment());
				}
			}
		}
	}

	protected String getDefaultMessage() {
		return "Configure a Liferay SDK location.";
	}

	protected void updateRuntimeItems() {
		Collection<String> validRuntimes = new HashSet<String>();

		for (IRuntime runtime : ServerCore.getRuntimes()) {
			if (runtime.getRuntimeType().getId().startsWith("com.liferay.ide.eclipse.server")) {
				validRuntimes.add(runtime.getName());
			}
		}

		String[] runtimes = validRuntimes.toArray(new String[0]);

		serverTargetCombo.setItems(runtimes);

		if (serverTargetCombo.getSelectionIndex() < 0 && runtimes.length > 0) {
			serverTargetCombo.select(0);
		}
	}

	protected IStatus validate() {
		lastName = name.getText();

		if (CoreUtil.isNullOrEmpty(lastName)) {
			return CoreUtil.createErrorStatus("Name must have a value.");
		}

		// make sure new sdk name doesn't collide with existing one
		if (existingSDKs != null) {
			for (SDK sdk : existingSDKs) {
				if (lastName.equals(sdk.getName())) {
					return CoreUtil.createErrorStatus("Name already exists.");
				}
			}
		}

		lastLocation = location.getText();

		if (CoreUtil.isNullOrEmpty(lastLocation)) {
			return CoreUtil.createErrorStatus("Location must have a value.");
		}

		if (!new File(lastLocation).exists()) {
			return CoreUtil.createErrorStatus("Location must exist.");
		}

		if (!SDKUtil.isValidSDKLocation(lastLocation)) {
			return CoreUtil.createErrorStatus("Location must be a valid Liferay SDK.");
		}

		if (!SDKUtil.isSDKSupported(lastLocation)) {
			return CoreUtil.createErrorStatus("SDK version must be greater or equal to " +
				ISDKConstants.LEAST_SUPPORTED_SDK_VERSION);
		}

		return Status.OK_STATUS;
	}
}
