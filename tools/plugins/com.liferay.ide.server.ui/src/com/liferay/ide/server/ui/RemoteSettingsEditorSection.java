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

package com.liferay.ide.server.ui;

import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.remote.IRemoteServer;
import com.liferay.ide.server.remote.RemoteServer;
import com.liferay.ide.server.ui.cmd.SetAdjustDeploymentTimestampCommand;
import com.liferay.ide.server.ui.cmd.SetHttpPortCommand;
import com.liferay.ide.server.ui.cmd.SetLiferayPortalContextPathCommand;
import com.liferay.ide.server.ui.cmd.SetPasswordCommand;
import com.liferay.ide.server.ui.cmd.SetServerManagerContextPathCommand;
import com.liferay.ide.server.ui.cmd.SetUsernameCommand;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.lang.reflect.InvocationTargetException;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;

/**
 * @author Gregory Amerson
 */
public class RemoteSettingsEditorSection extends ServerEditorSection {

	public RemoteSettingsEditorSection() {
	}

	@Override
	public void createSection(Composite parent) {
		FormToolkit toolkit = getFormToolkit(parent.getDisplay());

		remoteSettings = createSettingsSection(parent, toolkit);

		remoteSettings.setText(Msgs.remoteLiferaySettings);
		remoteSettings.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		remoteSettings.setDescription(Msgs.specifySettings);

		Composite settingsComposite = createSectionComposite(toolkit, remoteSettings);

		Label soapPortLabel = createLabel(toolkit, settingsComposite, Msgs.httpPortLabel);

		soapPortLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		textHttpPort = toolkit.createText(settingsComposite, null);

		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);

		data.widthHint = 50;

		textHttpPort.setLayoutData(data);

		textHttpPort.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (updating) {
						return;
					}

					updating = true;

					String httpPort = textHttpPort.getText();

					execute(new SetHttpPortCommand(remoteServer, httpPort.trim()));

					updating = false;

					// validate();

				}

			});

		Label usernameLabel = createLabel(toolkit, settingsComposite, Msgs.username);

		usernameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		textUsername = toolkit.createText(settingsComposite, null);

		textUsername.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		textUsername.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (updating) {
						return;
					}

					updating = true;

					String userName = textUsername.getText();

					execute(new SetUsernameCommand(remoteServer, userName.trim()));

					updating = false;

					// validate();

				}

			});

		Label passwordLabel = createLabel(toolkit, settingsComposite, Msgs.passwordLabel);

		passwordLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		textPassword = toolkit.createText(settingsComposite, null, SWT.PASSWORD);

		textPassword.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		textPassword.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (updating) {
						return;
					}

					updating = true;

					String password = textPassword.getText();

					execute(new SetPasswordCommand(remoteServer, password.trim()));

					updating = false;

					// validate();

				}

			});

		Label labelLiferayPortalContextPath = createLabel(
			toolkit, settingsComposite, Msgs.liferayPortalContextPathLabel);

		labelLiferayPortalContextPath.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		textLiferayPortalContextPath = toolkit.createText(settingsComposite, null);

		textLiferayPortalContextPath.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		textLiferayPortalContextPath.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (updating) {
						return;
					}

					updating = true;

					String liferayPortalContextPath = textLiferayPortalContextPath.getText();

					execute(new SetLiferayPortalContextPathCommand(remoteServer, liferayPortalContextPath.trim()));

					updating = false;

					// validate();

				}

			});

		Label labelServerManagerContextPath = createLabel(
			toolkit, settingsComposite, Msgs.serverManagerContextPathLabel);

		labelServerManagerContextPath.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		textServerManagerContextPath = toolkit.createText(settingsComposite, null);

		textServerManagerContextPath.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		textServerManagerContextPath.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (updating) {
						return;
					}

					updating = true;

					String serverManagerContextPath = textServerManagerContextPath.getText();

					execute(new SetServerManagerContextPathCommand(remoteServer, serverManagerContextPath.trim()));

					updating = false;

					// validate();

				}

			});

		adjustTimestamp = toolkit.createButton(settingsComposite, Msgs.adjustDeploymentTimestamps, SWT.CHECK);

		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);

		gd.horizontalSpan = 2;

		adjustTimestamp.setLayoutData(gd);

		adjustTimestamp.addSelectionListener(
			new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					if (updating) {
						return;
					}

					updating = true;

					execute(new SetAdjustDeploymentTimestampCommand(remoteServer, adjustTimestamp.getSelection()));

					updating = false;
				}

			});

		initialize();

		IStatus status = validateSection();

		if (!status.isOK()) {
			IMessageManager messageManager = getManagedForm().getMessageManager();

			messageManager.addMessage(
				remoteServer, status.getMessage(), status,
				status.getSeverity() == IStatus.ERROR ? IMessageProvider.ERROR : IMessageProvider.WARNING);
		}
	}

	@Override
	public IStatus[] getSaveStatus() {
		IStatus status = validateSection();

		IMessageManager messageManager = getManagedForm().getMessageManager();

		if (!status.isOK()) {
			messageManager.addMessage(
				remoteServer, status.getMessage(), status,
				status.getSeverity() == IStatus.ERROR ? IMessageProvider.ERROR : IMessageProvider.WARNING);
		}
		else {
			messageManager.removeMessage(remoteServer);
		}

		return new IStatus[] {Status.OK_STATUS};
	}

	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);

		if (server != null) {
			remoteServer = (RemoteServer)server.loadAdapter(RemoteServer.class, null);

			addChangeListeners();
		}

		initialize();
	}

	protected void addChangeListeners() {
		listener = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent event) {
				LiferayServerCore.updateConnectionSettings(remoteServer);

				if (updating) {
					return;
				}

				updating = true;

				Display display = Display.getDefault();

				display.syncExec(
					new Runnable() {

						public void run() {
							if (IRemoteServer.ATTR_ADJUST_DEPLOYMENT_TIMESTAMP.equals(event.getPropertyName())) {
								String s = (String)event.getNewValue();

								adjustTimestamp.setSelection(Boolean.parseBoolean(s));
							}
							else if (IRemoteServer.ATTR_USERNAME.equals(event.getPropertyName())) {
								String s = (String)event.getNewValue();

								textUsername.setText(s);
							}
							else if (IRemoteServer.ATTR_PASSWORD.equals(event.getPropertyName())) {
								String s = (String)event.getNewValue();

								textPassword.setText(s);
							}
							else if (IRemoteServer.ATTR_HTTP_PORT.equals(event.getPropertyName())) {
								String s = (String)event.getNewValue();

								textHttpPort.setText(s);
							}
							else if (IRemoteServer.ATTR_LIFERAY_PORTAL_CONTEXT_PATH.equals(event.getPropertyName())) {
								String s = (String)event.getNewValue();

								textLiferayPortalContextPath.setText(s);
							}
							else if (IRemoteServer.ATTR_SERVER_MANAGER_CONTEXT_PATH.equals(event.getPropertyName())) {
								String s = (String)event.getNewValue();

								textServerManagerContextPath.setText(s);
							}
						}

					});

				updating = false;
			}

		};

		server.addPropertyChangeListener(listener);
	}

	protected Label createLabel(FormToolkit toolkit, Composite parent, String text) {
		Label label = toolkit.createLabel(parent, text);

		FormColors colors = toolkit.getColors();

		label.setForeground(colors.getColor(IFormColors.TITLE));

		return label;
	}

	protected Composite createSectionComposite(FormToolkit toolkit, Section section) {
		Composite composite = toolkit.createComposite(section);

		GridLayout layout = new GridLayout();

		layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;

		composite.setLayout(layout);

		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

		toolkit.paintBordersFor(composite);

		section.setClient(composite);

		return composite;
	}

	protected Section createSettingsSection(Composite parent, FormToolkit toolkit) {
		return toolkit.createSection(
			parent,
			ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED |
			ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);
	}

	protected void initialize() {
		if ((remoteServer == null) || (textHttpPort == null) || (adjustTimestamp == null) || (textUsername == null) ||
			(textPassword == null) || (textLiferayPortalContextPath == null) ||
			(textServerManagerContextPath == null)) {

			return;
		}

		updating = true;

		textHttpPort.setText(remoteServer.getHTTPPort());

		textUsername.setText(remoteServer.getUsername());

		textPassword.setText(remoteServer.getPassword());

		textLiferayPortalContextPath.setText(remoteServer.getLiferayPortalContextPath());

		textServerManagerContextPath.setText(remoteServer.getServerManagerContextPath());

		boolean adjustGMTOffset = remoteServer.getAdjustDeploymentTimestamp();

		adjustTimestamp.setSelection(adjustGMTOffset);

		updating = false;
	}

	protected IStatus validateSection() {
		final IStatus[] status = new IStatus[1];

		try {
			IWorkbench workBench = PlatformUI.getWorkbench();

			IProgressService progressService = workBench.getProgressService();

			progressService.run(
				true, false,
				new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor) throws InterruptedException, InvocationTargetException {
						status[0] = remoteServer.validate(monitor);
					}

				});
		}
		catch (Exception e) {
		}

		return status[0];
	}

	protected Button adjustTimestamp;
	protected PropertyChangeListener listener;
	protected RemoteServer remoteServer;
	protected Section remoteSettings;
	protected Text textHttpPort;
	protected Text textLiferayPortalContextPath;
	protected Text textPassword;
	protected Text textServerManagerContextPath;
	protected Text textUsername;
	protected boolean updating = false;

	private static class Msgs extends NLS {

		public static String adjustDeploymentTimestamps;
		public static String httpPortLabel;
		public static String liferayPortalContextPathLabel;
		public static String passwordLabel;
		public static String remoteLiferaySettings;
		public static String serverManagerContextPathLabel;
		public static String specifySettings;
		public static String username;

		static {
			initializeMessages(RemoteSettingsEditorSection.class.getName(), Msgs.class);
		}

	}

}