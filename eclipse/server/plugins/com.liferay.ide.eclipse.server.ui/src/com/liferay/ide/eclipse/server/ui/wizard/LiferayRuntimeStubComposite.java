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

package com.liferay.ide.eclipse.server.ui.wizard;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.server.core.ILiferayRuntime;
import com.liferay.ide.eclipse.server.core.ILiferayRuntimeStub;
import com.liferay.ide.eclipse.server.core.LiferayRuntimeStubDelegate;
import com.liferay.ide.eclipse.server.core.LiferayServerCorePlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
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
public class LiferayRuntimeStubComposite extends Composite {

	protected boolean ignoreModifyEvent = false;
	protected Combo comboRuntimeStubType;
	protected IRuntimeWorkingCopy runtimeWC;
	protected Text textInstallDir;
	protected Text textName;
	protected ILiferayRuntime liferayRuntime;

	protected IWizardHandle wizard;

	public LiferayRuntimeStubComposite(Composite parent, IWizardHandle wizard) {
		super(parent, SWT.NONE);
		this.wizard = wizard;

		createControl();
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
			liferayRuntime = (ILiferayRuntime) newRuntime.loadAdapter( ILiferayRuntime.class, null );
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

		Label lblRuntimeStubType = new Label( this, SWT.NONE );
		lblRuntimeStubType.setText( "Runtime stub type" );

		createSpacer();

		comboRuntimeStubType = new Combo( this, SWT.READ_ONLY );
		comboRuntimeStubType.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
		comboRuntimeStubType.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected( SelectionEvent e ) {
				int index = comboRuntimeStubType.getSelectionIndex();
				ILiferayRuntimeStub selectedStub = LiferayServerCorePlugin.getRuntimeStubs()[index];
				LiferayRuntimeStubDelegate delegate =
					(LiferayRuntimeStubDelegate) runtimeWC.loadAdapter(
						LiferayRuntimeStubDelegate.class, new NullProgressMonitor() );
				delegate.setRuntimeStubTypeId( selectedStub.getRuntimeStubTypeId() );
				validate();
			}

		} );

		createSpacer();

		Label lblInstall = new Label( this, SWT.WRAP );
		lblInstall.setText( "Runtime stub directory" );

		new Label(this, SWT.NONE);

		textInstallDir = new Text(this, SWT.BORDER);
		textInstallDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textInstallDir.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				textInstallDirChanged(textInstallDir.getText());
			}

		});

		Button btnBrowse = new Button(this, SWT.NONE);
		btnBrowse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		btnBrowse.setText("Browse...");
		btnBrowse.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent se) {
				DirectoryDialog dialog = new DirectoryDialog(LiferayRuntimeStubComposite.this.getShell());
				dialog.setMessage("Select WebSphere installation directory.");
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

	protected IRuntimeWorkingCopy getRuntime() {
		return this.runtimeWC;
	}

	protected ILiferayRuntime getLiferayRuntime() {
		return this.liferayRuntime;
	}

	protected void initialize() {
		if ( textName == null || liferayRuntime == null ) {
			return;
		}

		ignoreModifyEvent = true;

		if (runtimeWC.getName() != null) {
			textName.setText(runtimeWC.getName());
		}
		else {
			textName.setText("");
		}

		if (runtimeWC.getLocation() != null) {
			textInstallDir.setText(runtimeWC.getLocation().toOSString());
		}
		else {
			textInstallDir.setText("");
		}

		updateStubs();

		// if (websphereRuntimeWC.getRuntimeStubLocation() != null) {
		// textStubDir.setText(websphereRuntimeWC.getRuntimeStubLocation().toOSString());
		// }
		// else {
		// textStubDir.setText("");
		// }

		ignoreModifyEvent = false;
	}

	protected void updateStubs() {
		ILiferayRuntimeStub[] stubs = LiferayServerCorePlugin.getRuntimeStubs();

		if ( CoreUtil.isNullOrEmpty( stubs ) ) {
			return;
		}

		String[] names = new String[stubs.length];

		for ( int i = 0; i < stubs.length; i++ ) {
			names[i] = stubs[i].getName();
		}

		comboRuntimeStubType.setItems( names );
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
	
	protected void textInstallDirChanged(String text) {
		if (ignoreModifyEvent) {
			return;
		}

		runtimeWC.setLocation(new Path(text));

		validate();

		// IStatus status = getRuntime().validate(null);

	}

	protected void textStubInstallDirChanged(String text) {
		if (ignoreModifyEvent) {
			return;
		}

		// Path location = new Path(text);
		// websphereRuntimeWC.setRuntimeStubLocation(location);
		// IStatus status = validate();
		//
		// if (status.getSeverity() == IStatus.ERROR) {
		// IPath newStubLocation = LiferayTomcatUtil.modifyLocationForBundle(location);
		//
		// if (newStubLocation != null && newStubLocation.toFile().exists()) {
		// textStubDir.setText(newStubLocation.toOSString());
		// }
		// }
	}


	protected IStatus validate() {
		if ( liferayRuntime == null ) {
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
