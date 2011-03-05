/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.server.tomcat.ui.wizard;

import com.liferay.ide.eclipse.server.tomcat.core.ILiferayTomcatRuntime;
import com.liferay.ide.eclipse.server.tomcat.core.util.LiferayTomcatUtil;
import com.liferay.ide.eclipse.server.ui.LiferayServerUIPlugin;
import com.liferay.ide.eclipse.ui.util.SWTUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jst.server.core.IJavaRuntime;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatRuntimeWorkingCopy;
import org.eclipse.jst.server.tomcat.ui.internal.TomcatRuntimeComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Greg Amerson
 */
@SuppressWarnings({
	"restriction"
})
public class LiferayTomcatRuntimeOptionalComposite extends TomcatRuntimeComposite implements ModifyListener {

	public static void setFieldValue(Text field, String value) {
		if (field != null && !field.isDisposed()) {
			field.setText(value != null ? value : "");
		}
	}

	protected Text bundleZipField;

	protected boolean ignoreModifyEvent;

	public LiferayTomcatRuntimeOptionalComposite(Composite parent, IWizardHandle wizard) {
		super(parent, wizard);

		wizard.setTitle("Liferay Runtime Tomcat Bundle");
		wizard.setDescription("Specify extra settings for Liferay Tomcat bundle (required for Ext plugins)");
		wizard.setImageDescriptor(LiferayServerUIPlugin.getImageDescriptor(LiferayServerUIPlugin.IMG_WIZ_RUNTIME));
	}

	public void modifyText(ModifyEvent e) {
		if (ignoreModifyEvent) {
			ignoreModifyEvent = false;

			return;
		}

		if (e.getSource().equals(bundleZipField)) {
			geLiferayTomcatRuntime().setBundleZipLocation(new Path(bundleZipField.getText()));
		}
		// else if (e.getSource().equals(sourceFolderField)) {
		// getLiferayTomcatRuntime().setPortalSourceLocation(new Path(sourceFolderField.getText()));
		// }

		validate();
	}

	@Override
	public void setRuntime(IRuntimeWorkingCopy newRuntime) {
		if (newRuntime == null) {
			runtimeWC = null;
			runtime = null;
		}
		else {
			runtimeWC = newRuntime;
			runtime = (ITomcatRuntimeWorkingCopy) newRuntime.loadAdapter(ITomcatRuntimeWorkingCopy.class, null);
		}

		init();
		validate();
	}

	protected Button createButton(String text, int style) {
		Button button = new Button(this, style);

		button.setText(text);

		GridDataFactory.generate(button, 2, 1);

		return button;
	}

	@Override
	protected void createControl() {
		setLayout(createLayout());

		setLayoutData(new GridData(GridData.FILL_BOTH));

		createFields();

		init();

		validate();

		Dialog.applyDialogFont(this);
	}

	protected void createFields() {
		bundleZipField = createTextField("Liferay Tomcat bundle zip file (required for Ext plugins)");
		bundleZipField.addModifyListener(this);

		SWTUtil.createButton(this, "Browse...").addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(LiferayTomcatRuntimeOptionalComposite.this.getShell());

				fd.setText("Select Liferay Tomcat bundle zip file");
				fd.setFilterPath(bundleZipField.getText());

				String selectedFile = fd.open();

				if (selectedFile != null) {
					bundleZipField.setText(selectedFile);
				}
			}
		});

		// sourceFolderField = createTextField("Liferay Portal source folder (optional)");
		// sourceFolderField.addModifyListener(this);
		//
		// SWTUtil.createButton(this, "Browse...").addSelectionListener(new SelectionAdapter() {
		//
		// public void widgetSelected(SelectionEvent e) {
		// DirectoryDialog dd = new DirectoryDialog(LiferayTomcatRuntimeOptionalComposite.this.getShell());
		//
		// dd.setMessage("Select Liferay Portal source folder directory");
		// dd.setFilterPath(sourceFolderField.getText());
		//
		// String selectedDir = dd.open();
		//
		// if (selectedDir != null) {
		// sourceFolderField.setText(selectedDir);
		// }
		// }
		// });

	}

	protected Label createLabel(String text) {
		Label label = new Label(this, SWT.NONE);
		label.setText(text);

		GridDataFactory.generate(label, 2, 1);

		return label;
	}

	protected Layout createLayout() {
		GridLayout layout = new GridLayout(2, false);
		return layout;
	}

	protected void createSpacer() {
		new Label(this, SWT.NONE);
	}

	protected Text createTextField(String labelText) {
		createLabel(labelText);

		Text text = new Text(this, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return text;
	}

	protected IJavaRuntime getJavaRuntime() {
		return (IJavaRuntime) this.runtime;
	}

	protected ILiferayTomcatRuntime geLiferayTomcatRuntime() {
		return LiferayTomcatUtil.getLiferayTomcatRuntime(this.runtimeWC);
	}

	protected IRuntimeWorkingCopy getRuntime() {
		return this.runtimeWC;
	}

	@Override
	protected void init() {
		if ((bundleZipField == null) || getRuntime() == null) {
			return;
		}

		IPath bundleZipLocation = geLiferayTomcatRuntime().getBundleZipLocation();
		// IPath bundleSourceLocation = getLiferayTomcatRuntime().getPortalSourceLocation();

		setFieldValue(bundleZipField, bundleZipLocation != null ? bundleZipLocation.toOSString() : "");
		// setFieldValue(sourceFolderField, bundleSourceLocation != null ? bundleSourceLocation.toOSString() : "");
	}

}
