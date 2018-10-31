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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.remote.IRemoteServer;
import com.liferay.ide.server.remote.IRemoteServerWorkingCopy;
import com.liferay.ide.server.remote.RemoteServer;
import com.liferay.ide.server.remote.RemoteUtil;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.ui.LiferayUIPlugin;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.lang.reflect.InvocationTargetException;

import java.net.URL;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Gregory Amerson
 */
public class RemoteServerComposite extends Composite implements ModifyListener, PropertyChangeListener {

	public RemoteServerComposite(Composite parent, RemoteServerWizardFragment fragment, IWizardHandle wizard) {
		super(parent, SWT.NONE);

		this.fragment = fragment;
		this.wizard = wizard;

		createControl(parent);
	}

	public void modifyText(ModifyEvent e) {
		Object src = e.getSource();

		if ((src == null) || ignoreModifyEvents) {
			return;
		}

		if (src.equals(textHostname)) {
			serverWC.setHost(textHostname.getText());

			// IDE-425

			if ((_initialServerName != null) && _initialHostName.contains(_initialHostName)) {
				serverWC.setName(_initialServerName.replaceAll(_initialHostName, textHostname.getText()));
			}
		}
		else if (src.equals(textHTTP)) {
			remoteServerWC.setHTTPPort(textHTTP.getText());
		}
		else if (src.equals(textServerManagerContextPath)) {
			remoteServerWC.setServerManagerContextPath(textServerManagerContextPath.getText());
		}
		else if (src.equals(textLiferayPortalContextPath)) {
			remoteServerWC.setLiferayPortalContextPath(textLiferayPortalContextPath.getText());
		}
		else if (src.equals(textUsername)) {
			remoteServerWC.setUsername(textUsername.getText());
		}
		else if (src.equals(textPassword)) {
			remoteServerWC.setPassword(textPassword.getText());
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (IRemoteServer.ATTR_HOSTNAME.equals(evt.getPropertyName()) ||
			IRemoteServer.ATTR_HTTP_PORT.equals(evt.getPropertyName()) ||
			IRemoteServer.ATTR_USERNAME.equals(evt.getPropertyName()) ||
			IRemoteServer.ATTR_PASSWORD.equals(evt.getPropertyName()) ||
			IRemoteServer.ATTR_LIFERAY_PORTAL_CONTEXT_PATH.equals(evt.getPropertyName()) ||
			IRemoteServer.ATTR_SERVER_MANAGER_CONTEXT_PATH.equals(evt.getPropertyName())) {

			LiferayServerCore.updateConnectionSettings((IRemoteServer)serverWC.loadAdapter(IRemoteServer.class, null));
		}
	}

	public void setServer(IServerWorkingCopy newServer) {
		if (newServer == null) {
			serverWC = null;
			remoteServerWC = null;
		}
		else {
			serverWC = newServer;

			remoteServerWC = (IRemoteServerWorkingCopy)serverWC.loadAdapter(IRemoteServerWorkingCopy.class, null);

			serverWC.addPropertyChangeListener(this);
		}

		disableValidation = true;

		initialize();

		disableValidation = false;

		validate();
	}

	protected void createControl(Composite parent) {
		setLayout(new GridLayout(1, false));
		setBackground(parent.getBackground());

		disableValidation = true;

		Group connectionGroup = SWTUtil.createGroup(this, Msgs.connectionSettings, 2);

		connectionGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label labelHostname = new Label(connectionGroup, SWT.NONE);

		labelHostname.setText(Msgs.hostname);

		textHostname = new Text(connectionGroup, SWT.BORDER);

		textHostname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textHostname.addModifyListener(this);

		labelHttpPort = new Label(connectionGroup, SWT.NONE);

		labelHttpPort.setText(Msgs.httpPortLabel);

		textHTTP = new Text(connectionGroup, SWT.BORDER);

		textHTTP.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		textHTTP.addModifyListener(this);

		labelUsername = new Label(connectionGroup, SWT.NONE);

		labelUsername.setText(Msgs.username);

		textUsername = new Text(connectionGroup, SWT.BORDER);

		textUsername.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		textUsername.addModifyListener(this);

		labelPassword = new Label(connectionGroup, SWT.NONE);

		labelPassword.setText(Msgs.password);

		textPassword = new Text(connectionGroup, SWT.BORDER | SWT.PASSWORD);

		textPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textPassword.addModifyListener(this);

		labelLiferayPortalContextPath = new Label(connectionGroup, SWT.NONE);

		labelLiferayPortalContextPath.setText(Msgs.liferayPortalContextPath);
		labelLiferayPortalContextPath.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		textLiferayPortalContextPath = new Text(connectionGroup, SWT.BORDER);

		textLiferayPortalContextPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textLiferayPortalContextPath.addModifyListener(this);

		labelServerManagerContextPath = new Label(connectionGroup, SWT.NONE);

		labelServerManagerContextPath.setText(Msgs.serverManagerContextPath);
		labelServerManagerContextPath.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		textServerManagerContextPath = new Text(connectionGroup, SWT.BORDER);

		textServerManagerContextPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textServerManagerContextPath.addModifyListener(this);

		String marketplaceLinkLabel = Msgs.installRemoteIDEConnector;
		String appUrl = "http://www.liferay.com/marketplace/-/mp/application/15193785";

		SWTUtil.createHyperLink(this, SWT.NONE, marketplaceLinkLabel, 1, appUrl);

		String installLabel = NLS.bind("<a>{0}</a>", Msgs.clickHereLink);
		String installUrl =
			"{0}/group/control_panel/manage?p_p_id=1_WAR_marketplaceportlet&p_p_lifecycle=0&" +
				"p_p_state=maximized&p_p_mode=view&appId=15193785";

		Link installLink = SWTUtil.createLink(this, SWT.NONE, installLabel, 1);

		installLink.addSelectionListener(
			new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					try {
						String url = MessageFormat.format(
							installUrl, "http://" + textHostname.getText() + ":" + textHTTP.getText());

						IWorkbenchBrowserSupport browserSupport = UIUtil.getBrowserSupport();

						IWebBrowser externalBrowser = browserSupport.getExternalBrowser();

						externalBrowser.openURL(new URL(url));
					}
					catch (Exception e1) {
						LiferayUIPlugin.logError("Could not open external browser.", e1);
					}
				}

			});

		Composite validateComposite = new Composite(this, SWT.NONE);

		validateComposite.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, true));
		validateComposite.setLayout(new GridLayout(1, false));

		Button validateButton = new Button(validateComposite, SWT.PUSH);

		validateButton.setText(Msgs.validateConnection);
		validateButton.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));
		validateButton.addSelectionListener(
			new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					validate();
				}

			});

		// initDataBindings();

		disableValidation = false;

		validate();
	}

	protected RemoteServer getRemoteServer() {
		if (serverWC != null) {
			return (RemoteServer)serverWC.loadAdapter(RemoteServer.class, null);
		}
		else {
			return null;
		}
	}

	protected void initialize() {
		if ((serverWC != null) && (remoteServerWC != null)) {
			ignoreModifyEvents = true;
			textHostname.setText(serverWC.getHost());
			textHTTP.setText(this.remoteServerWC.getHTTPPort());
			textLiferayPortalContextPath.setText(remoteServerWC.getLiferayPortalContextPath());
			textServerManagerContextPath.setText(remoteServerWC.getServerManagerContextPath());
			textUsername.setText(this.remoteServerWC.getUsername());
			textPassword.setText(this.remoteServerWC.getPassword());

			_initialServerName = serverWC.getName();
			_initialHostName = serverWC.getHost();
			ignoreModifyEvents = false;
		}
	}

	protected void validate() {
		if (disableValidation) {
			return;
		}

		if (serverWC == null) {
			wizard.setMessage(StringPool.EMPTY, IMessageProvider.ERROR);

			return;
		}

		try {
			IRunnableWithProgress validateRunnable = new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor) throws InterruptedException, InvocationTargetException {
					IStatus updateStatus = validateServer(monitor);

					if (updateStatus.isOK()) {
						String contextPath = RemoteUtil.detectServerManagerContextPath(getRemoteServer(), monitor);

						remoteServerWC.setServerManagerContextPath(contextPath);
					}

					Display remoteServerCompositeDisplay = RemoteServerComposite.this.getDisplay();

					remoteServerCompositeDisplay.syncExec(
						new Runnable() {

							public void run() {
								if ((updateStatus == null) || updateStatus.isOK()) {
									wizard.setMessage(null, IMessageProvider.NONE);
								}
								else if ((updateStatus.getSeverity() == IStatus.WARNING) ||
										 (updateStatus.getSeverity() == IStatus.ERROR)) {

									String updateStatusMessage = updateStatus.getMessage();

									if (updateStatusMessage.contains("Your license key has expired") ||
										updateStatusMessage.contains("Register Your Server or Application")) {

										wizard.setMessage(
											"Server is not registered or license key has expired ",
											IMessageProvider.WARNING);
									}
									else {
										wizard.setMessage(updateStatus.getMessage(), IMessageProvider.WARNING);
									}
								}

								wizard.update();
							}

						});
				}

			};

			wizard.run(true, true, validateRunnable);

			wizard.update();

			if ((fragment.lastServerStatus != null) && fragment.lastServerStatus.isOK()) {
				ignoreModifyEvents = true;

				textServerManagerContextPath.setText(remoteServerWC.getServerManagerContextPath());
				textLiferayPortalContextPath.setText(remoteServerWC.getLiferayPortalContextPath());

				ignoreModifyEvents = false;
			}
		}
		catch (Exception e) {
			Display remoteServerCompositeDisplay = RemoteServerComposite.this.getDisplay();

			remoteServerCompositeDisplay.syncExec(
				new Runnable() {

					public void run() {
						wizard.setMessage(e.getMessage(), IMessageProvider.WARNING);
						wizard.update();
					}

				});
		}
	}

	protected IStatus validateServer(IProgressMonitor monitor) {
		String host = serverWC.getHost();

		if (CoreUtil.isNullOrEmpty(host)) {
			return LiferayServerUI.createErrorStatus(Msgs.specifyHostname);
		}

		String username = remoteServerWC.getUsername();

		if (CoreUtil.isNullOrEmpty(username)) {
			return LiferayServerUI.createErrorStatus(Msgs.specifyUsernamePassword);
		}

		String port = remoteServerWC.getHTTPPort();

		if (CoreUtil.isNullOrEmpty(port)) {
			return LiferayServerUI.createErrorStatus(Msgs.specifyHTTPPort);
		}

		IStatus status = remoteServerWC.validate(monitor);

		if ((status != null) && (status.getSeverity() == IStatus.ERROR)) {
			fragment.lastServerStatus = new Status(
				IStatus.WARNING, status.getPlugin(), status.getMessage(), status.getException());
		}
		else {
			fragment.lastServerStatus = status;
		}

		return status;
	}

	protected boolean disableValidation;
	protected RemoteServerWizardFragment fragment;
	protected boolean ignoreModifyEvents;
	protected Label labelHttpPort;
	protected Label labelLiferayPortalContextPath;
	protected Label labelPassword;
	protected Label labelServerManagerContextPath;
	protected Label labelUsername;
	protected IRemoteServerWorkingCopy remoteServerWC;
	protected IServerWorkingCopy serverWC;
	protected Text textHostname;
	protected Text textHTTP;
	protected Text textLiferayPortalContextPath;
	protected Text textPassword;
	protected Text textServerManagerContextPath;
	protected Text textUsername;
	protected IWizardHandle wizard;

	private String _initialHostName;
	private String _initialServerName;

	private static class Msgs extends NLS {

		public static String clickHereLink;
		public static String connectionSettings;
		public static String hostname;
		public static String httpPortLabel;
		public static String installRemoteIDEConnector;
		public static String liferayPortalContextPath;
		public static String password;
		public static String serverManagerContextPath;
		public static String specifyHostname;
		public static String specifyHTTPPort;
		public static String specifyUsernamePassword;
		public static String username;
		public static String validateConnection;

		static {
			initializeMessages(RemoteServerComposite.class.getName(), Msgs.class);
		}

	}

}