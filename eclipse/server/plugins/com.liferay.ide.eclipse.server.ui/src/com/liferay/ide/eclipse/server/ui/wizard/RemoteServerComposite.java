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
import com.liferay.ide.eclipse.server.core.LiferayServerCorePlugin;
import com.liferay.ide.eclipse.server.remote.IRemoteServer;
import com.liferay.ide.eclipse.server.remote.IRemoteServerWorkingCopy;
import com.liferay.ide.eclipse.server.remote.RemoteServer;
import com.liferay.ide.eclipse.server.remote.RemoteUtil;
import com.liferay.ide.eclipse.server.ui.LiferayServerUIPlugin;
import com.liferay.ide.eclipse.ui.util.SWTUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Greg Amerson
 */
public class RemoteServerComposite extends Composite
	implements ModifyListener, SelectionListener, PropertyChangeListener {

	// protected Button checkboxCustomPortletXml;
	// protected Button checkboxSecurity;
	// protected Button detectButton;
	protected boolean disableValidation;
	protected RemoteServerWizardFragment fragment;
	protected boolean ignoreModifyEvents;
	protected Label labelServerManagerContextPath;
	protected Label labelLiferayPortalContextPath;
//	protected Label labelPassword;
	protected Label labelHttpPort;
//	protected Label labelUsername;
	protected IServerWorkingCopy serverWC;
	protected Text textServerManagerContextPath;
	protected Text textLiferayPortalContextPath;
	protected Text textHostname;
//	protected Text textPassword;
	protected Text textHTTP;
//	protected Text textUsername;
	protected IRemoteServerWorkingCopy remoteServerWC;
	protected IWizardHandle wizard;

	public RemoteServerComposite(Composite parent, RemoteServerWizardFragment fragment, IWizardHandle wizard) {
		super(parent, SWT.NONE);
		this.fragment = fragment;
		this.wizard = wizard;

		createControl();
	}

	public void modifyText(ModifyEvent e) {
		Object src = e.getSource();

		if (src == null || ignoreModifyEvents) {
			return;
		}

		if (src.equals(textHostname)) {
			this.serverWC.setHost(textHostname.getText());
		}
		else if (src.equals(textHTTP)) {
			this.remoteServerWC.setHTTPPort( textHTTP.getText() );
		}
//		else if (src.equals(textUsername)) {
//			this.remoteServerWC.setUsername( textUsername.getText() );
		// }
//		else if (src.equals(textPassword)) {
//			this.remoteServerWC.setPassword( textPassword.getText() );
//		}
		else if ( src.equals( textServerManagerContextPath ) ) {
			this.remoteServerWC.setServerManagerContextPath( textServerManagerContextPath.getText() );
		}
		else if ( src.equals( textLiferayPortalContextPath ) ) {
			this.remoteServerWC.setLiferayPortalContextPath( textLiferayPortalContextPath.getText() );
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if ( IRemoteServer.ATTR_HOSTNAME.equals( evt.getPropertyName() ) ||
			IRemoteServer.ATTR_HTTP_PORT.equals( evt.getPropertyName() ) ||
			IRemoteServer.ATTR_SERVER_MANAGER_CONTEXT_PATH.equals( evt.getPropertyName() ) ) { // ||
		// IRemoteServer.ATTR_USERNAME.equals( evt.getPropertyName() ) ||
		// IRemoteServer.ATTR_PASSWORD.equals( evt.getPropertyName() ) ||
		// IRemoteServer.ATTR_SECURITY_ENABLED.equals( evt.getPropertyName() ) ) {

			LiferayServerCorePlugin.updateConnectionSettings( (IRemoteServer) serverWC.loadAdapter(
				IRemoteServer.class, null ) );
		}
	}

	public void setServer(IServerWorkingCopy newServer) {
		if (newServer == null) {
			serverWC = null;
			remoteServerWC = null;
		}
		else {
			serverWC = newServer;
			remoteServerWC = (IRemoteServerWorkingCopy) serverWC.loadAdapter( IRemoteServerWorkingCopy.class, null );

			serverWC.addPropertyChangeListener(this);
		}

		disableValidation = true;
		initialize();
		disableValidation = false;
		validate();
		// detectAppName();
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	public void widgetSelected(SelectionEvent e) {
		Object src = e.getSource();

		if (src == null) {
			return;
		}

		// if (src.equals(checkboxSecurity)) {
		// this.remoteServerWC.setSecurityEnabled( checkboxSecurity.getSelection() );
		// }
		// else if (src.equals(checkboxCustomPortletXml)) {
		// this.remoteServerWC.setDeployCustomPortletXml( checkboxCustomPortletXml.getSelection() );
		// }
	}

	protected void createControl() {
		setLayout(new GridLayout(1, false));

		disableValidation = true;

		Group connectionGroup = SWTUtil.createGroup( this, "Connection Settings", 2 );
		connectionGroup.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );

		Label labelHostname = new Label( connectionGroup, SWT.NONE );
		labelHostname.setText("Hostname:");

		textHostname = new Text( connectionGroup, SWT.BORDER );
		textHostname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textHostname.addModifyListener(this);

		labelHttpPort = new Label( connectionGroup, SWT.NONE );
		labelHttpPort.setText( "HTTP port:" );
		
		textHTTP = new Text( connectionGroup, SWT.BORDER );
		textHTTP.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		textHTTP.addModifyListener(this);

		// checkboxSecurity = new Button(websphereGroup, SWT.CHECK);
		// checkboxSecurity.setText("Security enabled on this server");
		// checkboxSecurity.addSelectionListener(this);

		// labelUsername = new Label(websphereGroup, SWT.NONE);
		// labelUsername.setEnabled(false);
		// labelUsername.setText("Username:");
		//
		// textUsername = new Text(websphereGroup, SWT.BORDER);
		// textUsername.setEnabled(false);
		// textUsername.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		// textUsername.addModifyListener(this);
		//
		// labelPassword = new Label(websphereGroup, SWT.NONE);
		// labelPassword.setEnabled(false);
		// labelPassword.setText("Password:");
		//
		// textPassword = new Text(websphereGroup, SWT.BORDER | SWT.PASSWORD);
		// textPassword.setEnabled(false);
		// textPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		// textPassword.addModifyListener(this);
	
		labelLiferayPortalContextPath = new Label( connectionGroup, SWT.NONE );
		labelLiferayPortalContextPath.setText( "Liferay Portal Context Path:" );
		labelLiferayPortalContextPath.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false ) );

		textLiferayPortalContextPath = new Text( connectionGroup, SWT.BORDER );
		textLiferayPortalContextPath.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
		textLiferayPortalContextPath.addModifyListener( this );

		labelServerManagerContextPath = new Label( connectionGroup, SWT.NONE );
		labelServerManagerContextPath.setText( "Server Manager Context Path:" );
		labelServerManagerContextPath.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false ) );

		textServerManagerContextPath = new Text( connectionGroup, SWT.BORDER );
		textServerManagerContextPath.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
		textServerManagerContextPath.addModifyListener( this );

		// detectButton = new Button( connectionGroup, SWT.PUSH );
		// detectButton.setText("Detect");
		// detectButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		// detectButton.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// detectContextUrl();
		// }
		// });

		// deploymentGroup = SWTUtil.createGroup(this, "Deployment Settings", 1);
		// deploymentGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		// checkboxCustomPortletXml = new Button(deploymentGroup, SWT.CHECK);
		// checkboxCustomPortletXml.setText("Use portlet-custom.xml for deployment (required for WebSphere 6.x)");
		// checkboxCustomPortletXml.addSelectionListener(this);
		// checkboxCustomPortletXml.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

		Composite validateComposite = new Composite(this, SWT.NONE);
		validateComposite.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, true));
		validateComposite.setLayout(new GridLayout(1, false));

		Button validateButton = new Button(validateComposite, SWT.PUSH);
		validateButton.setText("Validate connection");
		validateButton.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));
		validateButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});

		// initDataBindings();
		disableValidation = false;

		validate();
	}

	// protected void detectContextUrl() {
	// final String[] contextPath = new String[1];
	//
	// try {
	// IRunnableWithProgress runnable = new IRunnableWithProgress() {
	//
	// public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
	// RemoteServer rs = getRemoteServer();
	//
	// if ( rs.canMakeHttpConnection() ) {
	// contextPath[0] = RemoteUtil.detectServerManagerContextPath( rs, monitor );
	//
	// remoteServerWC.setServerManagerContextPath( contextPath[0] );
	// }
	// }
	// };
	//
	// wizard.run( true, true, runnable );
	//
	// if ( !CoreUtil.isNullOrEmpty( contextPath[0] ) ) {
	// ignoreModifyEvents = true;
	// textContextPath.setText( contextPath[0] );
	// ignoreModifyEvents = false;
	// }
	//
	// validate();
	//
	// wizard.update();
	// }
	// catch ( Exception ex ) {
	//
	// }
	// }

	protected RemoteServer getRemoteServer() {
		if (serverWC != null) {
			return (RemoteServer) serverWC.loadAdapter( RemoteServer.class, null );
		}
		else {
			return null;
		}
	}

	protected void initialize() {
		if ( this.serverWC != null && this.remoteServerWC != null ) {
			ignoreModifyEvents = true;
			this.textHostname.setText(this.serverWC.getHost());
			this.textHTTP.setText( this.remoteServerWC.getHTTPPort() );
			this.textLiferayPortalContextPath.setText( this.remoteServerWC.getLiferayPortalContextPath() );
			this.textServerManagerContextPath.setText( this.remoteServerWC.getServerManagerContextPath() );
			// this.checkboxSecurity.setSelection( this.remoteServerWC.getSecurityEnabled() );
			// this.textUsername.setText( this.remoteServerWC.getUsername() );
			// this.textPassword.setText( this.remoteServerWC.getPassword() );
			ignoreModifyEvents = false;
		}
	}

	protected void validate() {
		if (disableValidation) {
			return;
		}

		if (serverWC == null) {
			wizard.setMessage("", IMessageProvider.ERROR);
			return;
		}

		try {
			IRunnableWithProgress validateRunnable = new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {

					final IStatus updateStatus = validateServer(monitor);

					if (updateStatus.isOK()) {
						String contextPath = RemoteUtil.detectServerManagerContextPath( getRemoteServer(), monitor );

						remoteServerWC.setServerManagerContextPath( contextPath );
					}


					RemoteServerComposite.this.getDisplay().syncExec(new Runnable() {

						public void run() {
							if (updateStatus == null || updateStatus.isOK()) {
								wizard.setMessage(null, IMessageProvider.NONE);
							}
							else if (updateStatus.getSeverity() == IStatus.WARNING ||
								updateStatus.getSeverity() == IStatus.ERROR) {
								wizard.setMessage(updateStatus.getMessage(), IMessageProvider.WARNING);
							}

							wizard.update();

						}
					});
				}
			};

			wizard.run(true, true, validateRunnable);
			wizard.update();

			if (fragment.lastServerStatus != null && fragment.lastServerStatus.isOK()) {
				ignoreModifyEvents = true;

				textServerManagerContextPath.setText( this.remoteServerWC.getServerManagerContextPath() );
				textLiferayPortalContextPath.setText( this.remoteServerWC.getLiferayPortalContextPath() );

				ignoreModifyEvents = false;
			}
		}
		catch (final Exception e) {
			RemoteServerComposite.this.getDisplay().syncExec(new Runnable() {

				public void run() {
					wizard.setMessage(e.getMessage(), IMessageProvider.WARNING);
					wizard.update();
				}
			});
		}

	}

	protected IStatus validateServer(IProgressMonitor monitor) {
		String host = serverWC.getHost();

		IStatus status = null;

		if (CoreUtil.isNullOrEmpty(host)) {
			status = LiferayServerUIPlugin.createErrorStatus( "Must specify hostname" );
		}

		String port = remoteServerWC.getHTTPPort();

		if (CoreUtil.isNullOrEmpty(port)) {
			status = LiferayServerUIPlugin.createErrorStatus( "Must specify SOAP port" );
		}

		if (status == null) {
			status = remoteServerWC.validate( monitor );
		}

		// if (status.getException() instanceof ServerCertificateException) {
		// boolean answer =
		// UIUtil.promptQuestion(
		// "WebSphere Server",
		// "Could not connect to WebSphere because of untrusted certificate.  Do you want to accept the server certificate at "
		// +
		// host + ":" + port);
		//
		// if (answer) {
		// boolean accepted = remoteServerWC.acceptServerCertificate();
		//
		// if (accepted) {
		// UIUtil.postInfo(
		// "WebSphere Server",
		// "SSL Certificate accepted. The Eclipse workbench will have to be restarted for changes to take into effect.");
		// }
		// }
		// }

		if (status.getSeverity() == IStatus.ERROR) {
			fragment.lastServerStatus =
				new Status(IStatus.WARNING, status.getPlugin(), status.getMessage(), status.getException());
		}
		else {
			fragment.lastServerStatus = status;
		}

		return status;
	}

}
