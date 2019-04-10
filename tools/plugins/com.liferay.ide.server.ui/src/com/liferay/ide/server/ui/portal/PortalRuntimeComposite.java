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

package com.liferay.ide.server.ui.portal;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
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
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Andy Wu
 */
public class PortalRuntimeComposite extends Composite implements ModifyListener {

	public static void setFieldValue(Text field, String value) {
		if ((field != null) && !field.isDisposed()) {
			field.setText((value != null) ? value : StringPool.EMPTY);
		}
	}

	public PortalRuntimeComposite(Composite parent, IWizardHandle wizard) {
		super(parent, SWT.NONE);

		_wizard = wizard;

		wizard.setTitle(Msgs.liferayPortalRuntime);
		wizard.setDescription(Msgs.specifyInstallationDirectory);
		wizard.setImageDescriptor(LiferayServerUI.getImageDescriptor(LiferayServerUI.IMG_WIZ_RUNTIME));

		createControl(parent);
	}

	@Override
	public void modifyText(ModifyEvent e) {
		Object source = e.getSource();

		if (source.equals(_dirField)) {
			getRuntime().setLocation(new Path(_dirField.getText()));

			_updateFields();

			validate();

			enableJREControls(true);

			updateJREs();
		}
		else if (source.equals(_nameField)) {
			getRuntime().setName(_nameField.getText());
		}
	}

	public void setRuntime(IRuntimeWorkingCopy newRuntime) {
		if (newRuntime == null) {
			_runtimeWC = null;
		}
		else {
			_runtimeWC = newRuntime;
		}

		init();

		try {
			validate();
		}
		catch (NullPointerException npe) {
		}
	}

	protected void createControl(final Composite parent) {
		setLayout(createLayout());
		setLayoutData(_createLayoutData());
		setBackground(parent.getBackground());

		_createFields();

		enableJREControls(false);

		Dialog.applyDialogFont(this);
	}

	protected Label createLabel(String text) {
		Label label = new Label(this, SWT.NONE);

		label.setText(text);

		GridDataFactory.generate(label, 2, 1);

		return label;
	}

	protected Layout createLayout() {
		return new GridLayout(2, false);
	}

	protected Text createReadOnlyTextField(String labelText) {
		return createTextField(labelText, SWT.READ_ONLY);
	}

	protected Text createTextField(String labelText) {
		return createTextField(labelText, SWT.NONE);
	}

	protected Text createTextField(String labelText, int style) {
		createLabel(labelText);

		Text text = new Text(this, SWT.BORDER | style);

		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return text;
	}

	protected void enableJREControls(boolean enabled) {
		_jreLabel.setEnabled(enabled);
		_jreCombo.setEnabled(enabled);
		_jreButton.setEnabled(enabled);
	}

	protected PortalRuntime getPortalRuntime() {
		return (PortalRuntime)getRuntime().loadAdapter(PortalRuntime.class, null);
	}

	protected IRuntimeWorkingCopy getRuntime() {
		return _runtimeWC;
	}

	protected void init() {
		if ((_dirField == null) || (_nameField == null) || (getRuntime() == null)) {
			return;
		}

		IPath location = getRuntime().getLocation();

		setFieldValue(_nameField, getRuntime().getName());
		setFieldValue(_dirField, (location != null) ? location.toOSString() : StringPool.EMPTY);

		_updateFields();
	}

	protected void updateJREs() {
		PortalRuntime portalRuntime = getPortalRuntime();

		IVMInstall currentVM = null;

		if ((portalRuntime != null) && (portalRuntime.getVMInstall() != null)) {
			currentVM = portalRuntime.getVMInstall();
		}
		else {
			currentVM = JavaRuntime.getDefaultVMInstall();
		}

		int currentJREIndex = -1;

		_installedJREs = new ArrayList<>();

		IVMInstallType[] vmInstallTypes = JavaRuntime.getVMInstallTypes();

		int size = vmInstallTypes.length;

		for (int i = 0; i < size; i++) {
			IVMInstall[] vmInstalls = vmInstallTypes[i].getVMInstalls();

			int size2 = vmInstalls.length;

			for (int j = 0; j < size2; j++) {
				_installedJREs.add(vmInstalls[j]);
			}
		}

		size = _installedJREs.size();

		_jreNames = new String[size + 1];

		_jreNames[0] = Msgs.defaultWorkbenchJRE;

		for (int i = 0; i < size; i++) {
			IVMInstall vmInstall = _installedJREs.get(i);

			_jreNames[i + 1] = vmInstall.getName();

			if (vmInstall.equals(currentVM)) {
				currentJREIndex = i + 1;
			}
		}

		if (_jreCombo != null) {
			_jreCombo.setItems(_jreNames);
			_jreCombo.select(currentJREIndex);
		}
	}

	protected void validate() {
		IStatus status = _runtimeWC.validate(null);

		if ((status == null) || status.isOK()) {
			_wizard.setMessage(null, IMessageProvider.NONE);
		}
		else if (status.getSeverity() == IStatus.WARNING) {
			_wizard.setMessage(status.getMessage(), IMessageProvider.WARNING);
		}
		else {
			_wizard.setMessage(status.getMessage(), IMessageProvider.ERROR);
		}

		_wizard.update();
	}

	private void _createFields() {
		_nameField = createTextField(Msgs.name);

		_nameField.addModifyListener(this);

		_dirField = createTextField(Msgs.liferayPortalRuntimeDirectory);

		_dirField.addModifyListener(this);

		Button button = SWTUtil.createButton(this, Msgs.browse);

		button.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dd = new DirectoryDialog(getShell());

					dd.setMessage(Msgs.selectLiferayPortalDirectory);

					String selectedDir = dd.open();

					if (selectedDir != null) {
						_dirField.setText(selectedDir);
					}
				}

			});

		_typeField = createReadOnlyTextField(Msgs.detectedPortalBundleType);
		_jreLabel = createLabel(Msgs.selecteRuntimeJRE);
		_jreCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);

		_jreCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_jreCombo.addModifyListener(
			new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					int sel = _jreCombo.getSelectionIndex();

					IVMInstall vmInstall = null;

					if (sel > 0) {
						vmInstall = _installedJREs.get(sel - 1);
					}

					PortalRuntime portalRuntime = getPortalRuntime();

					if (portalRuntime != null) {
						portalRuntime.setVMInstall(vmInstall);
					}

					validate();
				}

			});

		_jreButton = SWTUtil.createButton(this, Msgs.installedJREs);

		_jreButton.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (SWTUtil.showPreferencePage(
							"org.eclipse.jdt.debug.ui.preferences.VMPreferencePage", getShell())) {

						updateJREs();

						validate();
					}
				}

			});
	}

	private GridData _createLayoutData() {
		return new GridData(GridData.FILL_BOTH);
	}

	private void _updateFields() {
		PortalRuntime portalRuntime = getPortalRuntime();

		if (portalRuntime != null) {
			PortalBundle portalBundle = portalRuntime.getPortalBundle();

			setFieldValue(_typeField, (portalBundle != null) ? portalBundle.getDisplayName() : StringPool.BLANK);
		}
	}

	private Text _dirField;
	private List<IVMInstall> _installedJREs;
	private Button _jreButton;
	private Combo _jreCombo;
	private Label _jreLabel;
	private String[] _jreNames;
	private Text _nameField;
	private IRuntimeWorkingCopy _runtimeWC;
	private Text _typeField;
	private final IWizardHandle _wizard;

	private static class Msgs extends NLS {

		public static String browse;
		public static String defaultWorkbenchJRE;
		public static String detectedPortalBundleType;
		public static String installedJREs;
		public static String liferayPortalRuntime;
		public static String liferayPortalRuntimeDirectory;
		public static String name;
		public static String selecteRuntimeJRE;
		public static String selectLiferayPortalDirectory;
		public static String specifyInstallationDirectory;

		static {
			initializeMessages(PortalRuntimeComposite.class.getName(), Msgs.class);
		}

	}

}