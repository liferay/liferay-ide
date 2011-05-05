/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.eclipse.server.aws.ui.wizard;

import com.liferay.ide.eclipse.server.aws.core.BeanstalkRuntime;
import com.liferay.ide.eclipse.server.aws.core.IBeanstalkRuntimeWorkingCopy;
import com.liferay.ide.eclipse.server.tomcat.core.util.LiferayTomcatUtil;
import com.liferay.ide.eclipse.ui.util.SWTUtil;

import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.server.core.IJavaRuntime;
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
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Greg Amerson
 */
public class BeanstalkRuntimeComposite extends Composite {

	protected Button btnInstalledJres;
	protected Combo comboBeanstalkJDK;
	protected boolean ignoreModifyEvent = false;
	protected ArrayList<IVMInstall> installedJREs = new ArrayList<IVMInstall>();
	protected String[] jreNames;
	protected Label lblSpecifyBeanstalkJdk;
	protected IRuntimeWorkingCopy runtimeWC;
	protected Text textName;
	protected Text textStubDir;
	protected IBeanstalkRuntimeWorkingCopy websphereRuntimeWC;

	protected IWizardHandle wizard;

	public BeanstalkRuntimeComposite(Composite parent, IWizardHandle wizard) {
		super(parent, SWT.NONE);
		this.wizard = wizard;

		// updateJREs();
		createControl();
		enableJREControls(false);
		initialize();
		validate();
	}

	public void setRuntime(IRuntimeWorkingCopy newRuntime) {
		if (newRuntime == null) {
			runtimeWC = null;
			websphereRuntimeWC = null;
		}
		else {
			runtimeWC = newRuntime;
			websphereRuntimeWC =
				(IBeanstalkRuntimeWorkingCopy) newRuntime.loadAdapter(IBeanstalkRuntimeWorkingCopy.class, null);
		}

		initialize();
		validate();
	}

	protected void createControl() {
		setLayout(new GridLayout(2, false));
		setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lblName = new Label(this, SWT.NONE);
		lblName.setText("Name");

		new Label(this, SWT.NONE);

		textName = new Text(this, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textName.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				runtimeWC.setName(textName.getText());
				validate();
			}

		});

		createSpacer();

		textStubDir = createTextField("Liferay 6.0 EE Tomcat bundle directory");
		textStubDir.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				textStubInstallDirChanged(textStubDir.getText());
			}

		});

		SWTUtil.createButton(this, "Browse...").addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dd = new DirectoryDialog(BeanstalkRuntimeComposite.this.getShell());

				dd.setMessage("Select Liferay Tomcat bundle directory");
				dd.setFilterPath(textStubDir.getText());

				String selectedDir = dd.open();

				if (selectedDir != null) {
					textStubDir.setText(selectedDir);
				}
			}
		});

		lblSpecifyBeanstalkJdk = new Label(this, SWT.NONE);
		lblSpecifyBeanstalkJdk.setEnabled(false);
		lblSpecifyBeanstalkJdk.setText("Specify JDK");

		new Label(this, SWT.NONE);

		comboBeanstalkJDK = new Combo(this, SWT.NONE);
		comboBeanstalkJDK.setEnabled(false);
		comboBeanstalkJDK.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboBeanstalkJDK.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int sel = comboBeanstalkJDK.getSelectionIndex();
				IVMInstall vmInstall = null;

				if (sel > 0) {
					vmInstall = (IVMInstall) installedJREs.get(sel - 1);
				}

				websphereRuntimeWC.setVMInstall(vmInstall);
				validate();
			}

		});

		btnInstalledJres = new Button(this, SWT.NONE);
		btnInstalledJres.setEnabled(false);
		btnInstalledJres.setText("Installed JREs...");
		btnInstalledJres.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				String currentVM = comboBeanstalkJDK.getText();

				if (showPreferencePage()) {
					updateJREs();
					comboBeanstalkJDK.setItems(jreNames);
					comboBeanstalkJDK.setText(currentVM);

					if (comboBeanstalkJDK.getSelectionIndex() == -1) {
						comboBeanstalkJDK.select(0);
					}

					validate();
				}
			}

		});

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

	protected void enableJREControls(boolean enabled) {
		lblSpecifyBeanstalkJdk.setEnabled(enabled);
		btnInstalledJres.setEnabled(enabled);
		comboBeanstalkJDK.setEnabled(enabled);
	}

	protected IJavaRuntime getJavaRuntime() {
		return this.websphereRuntimeWC;
	}

	protected IRuntimeWorkingCopy getRuntime() {
		return this.runtimeWC;
	}

	protected BeanstalkRuntime getBeanstalkRuntime() {
		return (BeanstalkRuntime) this.websphereRuntimeWC;
	}

	protected void initialize() {
		if (textName == null || websphereRuntimeWC == null) {
			return;
		}

		ignoreModifyEvent = true;

		if (runtimeWC.getName() != null) {
			textName.setText(runtimeWC.getName());
		}
		else {
			textName.setText("");
		}

		if (websphereRuntimeWC.getRuntimeStubLocation() != null) {
			textStubDir.setText(websphereRuntimeWC.getRuntimeStubLocation().toOSString());
		}
		else {
			textStubDir.setText("");
		}

		// set selection
		if (websphereRuntimeWC.isUsingDefaultJRE()) {
			comboBeanstalkJDK.select(0);
		}
		else {
			updateJREs();

			boolean found = false;
			int size = installedJREs.size();

			for (int i = 0; i < size; i++) {
				IVMInstall vmInstall = (IVMInstall) installedJREs.get(i);

				if (vmInstall.equals(websphereRuntimeWC.getVMInstall())) {
					comboBeanstalkJDK.select(i + 1);
					found = true;
				}
			}

			if (!found) {
				comboBeanstalkJDK.select(0);
			}
		}

		ignoreModifyEvent = false;
	}

	protected boolean showPreferencePage() {
		String id = "org.eclipse.jdt.debug.ui.preferences.VMPreferencePage";

		// should be using the following API, but it only allows a single preference page instance.
		// see bug 168211 for details
		String[] displayedIds = new String[] {
			id
		};
		PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getShell(), id, displayedIds, null);

		return (dialog.open() == Window.OK);

		// PreferenceManager manager = PlatformUI.getWorkbench().getPreferenceManager();
		// IPreferenceNode node = manager.find("org.eclipse.jdt.ui.preferences.JavaBasePreferencePage").findSubNode(id);
		// PreferenceManager manager2 = new PreferenceManager();
		// manager2.addToRoot(node);
		// PreferenceDialog dialog = new PreferenceDialog(getShell(), manager2);
		// dialog.create();
		// return (dialog.open() == Window.OK);
	}
	
	protected void textStubInstallDirChanged(String text) {
		if (ignoreModifyEvent) {
			return;
		}

		Path location = new Path(text);
		websphereRuntimeWC.setRuntimeStubLocation(location);
		IStatus status = validate();

		if (status.getSeverity() == IStatus.ERROR) {
			IPath newStubLocation = LiferayTomcatUtil.modifyLocationForBundle(location);

			if (newStubLocation != null && newStubLocation.toFile().exists()) {
				textStubDir.setText(newStubLocation.toOSString());
			}
		}

		updateJREs();

		if (status != null && status.isOK()) {
			enableJREControls(true);
		}
	}

	protected void updateJREs() {
		IVMInstall currentVM = getJavaRuntime().getVMInstall();

		int currentJREIndex = -1;

		// get all installed JVMs
		installedJREs = new ArrayList<IVMInstall>();

		IVMInstallType[] vmInstallTypes = JavaRuntime.getVMInstallTypes();

		int size = vmInstallTypes.length;

		for (int i = 0; i < size; i++) {
			IVMInstall[] vmInstalls = vmInstallTypes[i].getVMInstalls();

			int size2 = vmInstalls.length;

			for (int j = 0; j < size2; j++) {
				installedJREs.add(vmInstalls[j]);
			}
		}

		// get names
		size = installedJREs.size();

		jreNames = new String[size + 1];
		jreNames[0] = "<Workspace default JRE>";

		for (int i = 0; i < size; i++) {
			IVMInstall vmInstall = (IVMInstall) installedJREs.get(i);

			jreNames[i + 1] = vmInstall.getName();

			if (vmInstall.equals(currentVM)) {
				currentJREIndex = i + 1;
			}
		}

		if (comboBeanstalkJDK != null) {
			comboBeanstalkJDK.setItems(jreNames);
			comboBeanstalkJDK.select(currentJREIndex);
		}
	}

	protected IStatus validate() {
		if (websphereRuntimeWC == null) {
			wizard.setMessage("", IMessageProvider.ERROR);
			return Status.OK_STATUS;
		}

		IStatus status = runtimeWC.validate(null);

		if (status == null || status.isOK()) {
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

}
