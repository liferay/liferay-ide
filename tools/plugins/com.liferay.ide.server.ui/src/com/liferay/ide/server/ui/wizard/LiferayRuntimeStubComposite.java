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

package com.liferay.ide.server.ui.wizard;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.ILiferayRuntimeStub;
import com.liferay.ide.server.core.LiferayRuntimeStubDelegate;
import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Gregory Amerson
 */
public class LiferayRuntimeStubComposite extends Composite {

	public LiferayRuntimeStubComposite(Composite parent, IWizardHandle wizard) {
		super(parent, SWT.NONE);

		this.wizard = wizard;

		createControl(parent);
		initialize();
		validate();
	}

	public void setRuntime(IRuntimeWorkingCopy newRuntime) {
		if (newRuntime == null) {
			runtimeWC = null;
			liferayRuntime = null;
		}
		else {
			runtimeWC = newRuntime;
			liferayRuntime = (ILiferayRuntime)newRuntime.loadAdapter(ILiferayRuntime.class, null);
		}

		initialize();
		validate();
	}

	protected void createControl(Composite parent) {
		setLayout(new GridLayout(2, false));
		setLayoutData(new GridData(GridData.FILL_BOTH));
		setBackground(parent.getBackground());

		Label lblName = new Label(this, SWT.NONE);

		lblName.setText(Msgs.name);

		new Label(this, SWT.NONE);

		textName = new Text(this, SWT.BORDER);

		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textName.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					runtimeWC.setName(textName.getText());
					validate();
				}

			});

		createSpacer();

		Label lblRuntimeStubType = new Label(this, SWT.NONE);

		lblRuntimeStubType.setText(Msgs.liferayBundleType);

		createSpacer();

		comboRuntimeStubType = new Combo(this, SWT.READ_ONLY);

		comboRuntimeStubType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		createSpacer();

		Label lblInstall = new Label(this, SWT.WRAP);

		lblInstall.setText(Msgs.liferayBundleDirectory);

		new Label(this, SWT.NONE);

		textInstallDir = new Text(this, SWT.BORDER);

		textInstallDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		comboRuntimeStubType.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					int index = comboRuntimeStubType.getSelectionIndex();

					ILiferayRuntimeStub selectedStub = LiferayServerCore.getRuntimeStubs()[index];

					LiferayRuntimeStubDelegate delegate = getStubDelegate();

					delegate.setRuntimeStubTypeId(selectedStub.getRuntimeStubTypeId());

					String stubTypeId = selectedStub.getRuntimeStubTypeId();

					IRuntimeType runtimeType = ServerCore.findRuntimeType(stubTypeId);

					for (IRuntime runtime : ServerCore.getRuntimes()) {
						IRuntimeType rType = runtime.getRuntimeType();

						if (rType.equals(runtimeType)) {
							IPath runtimeLocation = runtime.getLocation();

							textInstallDir.setText(runtimeLocation.toOSString());
						}
					}

					validate();
				}

			});

		textInstallDir.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					textInstallDirChanged(textInstallDir.getText());
				}

			});

		Button btnBrowse = new Button(this, SWT.NONE);

		btnBrowse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		btnBrowse.setText(Msgs.browse);
		btnBrowse.addSelectionListener(
			new SelectionAdapter() {

				public void widgetSelected(SelectionEvent se) {
					DirectoryDialog dialog = new DirectoryDialog(LiferayRuntimeStubComposite.this.getShell());

					dialog.setMessage(Msgs.selectRuntimeStubDirectory);
					dialog.setFilterPath(textInstallDir.getText());

					String selectedDirectory = dialog.open();

					if (selectedDirectory != null) {
						textInstallDir.setText(selectedDirectory);
					}
				}

			});

		new Label(this, SWT.NONE);

		Dialog.applyDialogFont(this);

		textName.forceFocus();
	}

	protected Label createLabel(String text) {
		Label label = new Label(this, SWT.NONE);

		label.setText(text);

		GridDataFactory.generate(label, 2, 1);

		return label;
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

	protected ILiferayRuntime getLiferayRuntime() {
		return liferayRuntime;
	}

	protected IRuntimeWorkingCopy getRuntime() {
		return runtimeWC;
	}

	protected LiferayRuntimeStubDelegate getStubDelegate() {
		return (LiferayRuntimeStubDelegate)runtimeWC.loadAdapter(
			LiferayRuntimeStubDelegate.class, new NullProgressMonitor());
	}

	protected void initialize() {
		if ((textName == null) || (liferayRuntime == null)) {
			return;
		}

		ignoreModifyEvent = true;

		if (runtimeWC.getName() != null) {
			textName.setText(runtimeWC.getName());
		}
		else {
			textName.setText(StringPool.EMPTY);
		}

		if (runtimeWC.getLocation() != null) {
			IPath runtimeWclocation = runtimeWC.getLocation();

			textInstallDir.setText(runtimeWclocation.toOSString());
		}
		else {
			textInstallDir.setText(StringPool.EMPTY);
		}

		updateStubs();

		ignoreModifyEvent = false;
	}

	protected void textInstallDirChanged(String text) {
		if (ignoreModifyEvent) {
			return;
		}

		runtimeWC.setLocation(new Path(text));

		validate();

		// IStatus status = getRuntime().validate(null);

	}

	protected void updateStubs() {
		ILiferayRuntimeStub[] stubs = LiferayServerCore.getRuntimeStubs();

		if (ListUtil.isEmpty(stubs)) {
			return;
		}

		String[] names = new String[stubs.length];

		LiferayRuntimeStubDelegate delegate = getStubDelegate();

		String stubId = delegate.getRuntimeStubTypeId();

		int stubIndex = -1;

		for (int i = 0; i < stubs.length; i++) {
			names[i] = stubs[i].getName();

			String runtimeStubTypeId = stubs[i].getRuntimeStubTypeId();

			if (runtimeStubTypeId.equals(stubId)) {
				stubIndex = i;
			}
		}

		comboRuntimeStubType.setItems(names);

		if (stubIndex >= 0) {
			comboRuntimeStubType.select(stubIndex);
		}
	}

	protected IStatus validate() {
		if (liferayRuntime == null) {
			wizard.setMessage(StringPool.EMPTY, IMessageProvider.ERROR);

			return Status.OK_STATUS;
		}

		IStatus status = runtimeWC.validate(null);

		if ((status == null) || status.isOK()) {
			wizard.setMessage(null, IMessageProvider.NONE);
		}
		else if (status.getSeverity() == IStatus.WARNING) {
			wizard.setMessage(status.getMessage(), IMessageProvider.WARNING);
		}
		else {
			wizard.setMessage(status.getMessage(), IMessageProvider.ERROR);
		}

		wizard.update();

		return status;
	}

	protected Combo comboRuntimeStubType;
	protected boolean ignoreModifyEvent = false;
	protected ILiferayRuntime liferayRuntime;
	protected IRuntimeWorkingCopy runtimeWC;
	protected Text textInstallDir;
	protected Text textName;
	protected IWizardHandle wizard;

	private static class Msgs extends NLS {

		public static String browse;
		public static String liferayBundleDirectory;
		public static String liferayBundleType;
		public static String name;
		public static String selectRuntimeStubDirectory;

		static {
			initializeMessages(LiferayRuntimeStubComposite.class.getName(), Msgs.class);
		}

	}

}