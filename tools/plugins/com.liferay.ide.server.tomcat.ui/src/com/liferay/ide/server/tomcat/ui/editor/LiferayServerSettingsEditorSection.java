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

package com.liferay.ide.server.tomcat.ui.editor;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatConstants;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatServer;
import com.liferay.ide.server.tomcat.core.LiferayTomcatServer;
import com.liferay.ide.server.tomcat.ui.command.SetExternalPropertiesCommand;
import com.liferay.ide.server.tomcat.ui.command.SetMemoryArgsCommand;
import com.liferay.ide.server.tomcat.ui.command.SetServerModeCommand;
import com.liferay.ide.server.tomcat.ui.command.SetUseDefaultPortalSeverSettingsCommand;
import com.liferay.ide.server.tomcat.ui.command.SetUserTimezoneCommand;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.ui.cmd.SetPasswordCommand;
import com.liferay.ide.server.ui.cmd.SetUsernameCommand;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatServer;
import org.eclipse.jst.server.tomcat.ui.internal.ContextIds;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IPublishListener;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.util.PublishAdapter;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class LiferayServerSettingsEditorSection extends ServerEditorSection {

	public LiferayServerSettingsEditorSection() {

		// do nothing

	}

	public void createSection(Composite parent) {
		super.createSection(parent);

		FormToolkit toolkit = getFormToolkit(parent.getDisplay());

		section = toolkit.createSection(
			parent,
			ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR |
			Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);

		section.setText(Msgs.liferaySettings);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

		Composite composite = toolkit.createComposite(section);

		GridLayout layout = new GridLayout();

		layout.numColumns = 3;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;

		composite.setLayout(layout);

		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

		UIUtil.setHelp(composite, ContextIds.SERVER_EDITOR);
		UIUtil.setHelp(section, ContextIds.SERVER_EDITOR);

		toolkit.paintBordersFor(composite);
		section.setClient(composite);

		useDefaultPortalServerSettings = new Button(composite, SWT.CHECK);

		useDefaultPortalServerSettings.setText(Msgs.useDefaultPortalServerSetting);

		GridData data = new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 3, 1);

		useDefaultPortalServerSettings.setLayoutData(data);

		useDefaultPortalServerSettings.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					updating = true;
					execute(
						new SetUseDefaultPortalSeverSettingsCommand(
							tomcatServer, useDefaultPortalServerSettings.getSelection()));
					updating = false;

					_applyDefaultPortalServerSetting(useDefaultPortalServerSettings.getSelection());

					validate();
				}

			});

		Label label = createLabel(toolkit, composite, Msgs.memoryArgsLabel);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label.setLayoutData(data);

		memoryArgs = toolkit.createText(composite, null);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);

		data.widthHint = 300;
		memoryArgs.setLayoutData(data);

		memoryArgs.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (updating) {
						return;
					}

					updating = true;
					execute(new SetMemoryArgsCommand(tomcatServer, StringUtil.trim(memoryArgs.getText())));
					updating = false;
					validate();
				}

			});

		label = createLabel(toolkit, composite, StringPool.EMPTY);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label.setLayoutData(data);

		label = createLabel(toolkit, composite, Msgs.userTimezoneLabel);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label.setLayoutData(data);

		userTimezone = toolkit.createText(composite, null);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);

		userTimezone.setLayoutData(data);

		userTimezone.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (updating) {
						return;
					}

					updating = true;
					execute(new SetUserTimezoneCommand(tomcatServer, StringUtil.trim(userTimezone.getText())));
					updating = false;
					validate();
				}

			});

		label = createLabel(toolkit, composite, StringPool.EMPTY);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label.setLayoutData(data);

		label = createLabel(toolkit, composite, Msgs.externalPropertiesLabel);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label.setLayoutData(data);

		externalProperties = toolkit.createText(composite, null);
		data = new GridData(SWT.FILL, SWT.CENTER, false, false);

		data.widthHint = 150;
		externalProperties.setLayoutData(data);

		externalProperties.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (updating) {
						return;
					}

					updating = true;
					execute(
						new SetExternalPropertiesCommand(tomcatServer, StringUtil.trim(externalProperties.getText())));
					updating = false;
					validate();
				}

			});

		externalPropertiesBrowse = toolkit.createButton(composite, Msgs.editorBrowse, SWT.PUSH);

		externalPropertiesBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		externalPropertiesBrowse.addSelectionListener(
			new SelectionAdapter() {

				public void widgetSelected(SelectionEvent se) {
					FileDialog dialog = new FileDialog(externalPropertiesBrowse.getShell());

					dialog.setFilterPath(externalPropertiesBrowse.getText());

					String selectedFile = dialog.open();

					if ((selectedFile != null) && !selectedFile.equals(externalPropertiesBrowse.getText())) {
						updating = true;
						execute(new SetExternalPropertiesCommand(tomcatServer, selectedFile));
						externalProperties.setText(selectedFile);
						updating = false;
						validate();
					}
				}

			});

		label = createLabel(toolkit, composite, Msgs.serverMode);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label.setLayoutData(data);

		standardServerMode = new Button(composite, SWT.RADIO);

		standardServerMode.setText(Msgs.standardServerMode);

		data = new GridData(SWT.FILL, SWT.CENTER, false, false);

		standardServerMode.setLayoutData(data);

		standardServerMode.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					updating = true;
					execute(new SetServerModeCommand(tomcatServer, ILiferayTomcatConstants.STANDARD_SERVER_MODE));
					updating = false;
				}

			});

		label = createLabel(toolkit, composite, StringPool.EMPTY);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label.setLayoutData(data);

		label = createLabel(toolkit, composite, StringPool.EMPTY);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label.setLayoutData(data);

		developmentServerMode = new Button(composite, SWT.RADIO);

		developmentServerMode.setText(Msgs.developmentServerMode);

		data = new GridData(SWT.FILL, SWT.CENTER, false, false);

		developmentServerMode.setLayoutData(data);

		developmentServerMode.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					updating = true;
					execute(new SetServerModeCommand(tomcatServer, ILiferayTomcatConstants.DEVELOPMENT_SERVER_MODE));
					updating = false;
				}

			});

		label = createLabel(toolkit, composite, StringPool.EMPTY);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label.setLayoutData(data);

		Label usernameLabel = createLabel(toolkit, composite, Msgs.username);

		usernameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		username = toolkit.createText(composite, null);

		username.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		username.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (updating) {
						return;
					}

					updating = true;
					execute(new SetUsernameCommand(tomcatServer, StringUtil.trim(username.getText())));
					updating = false;
				}

			});

		label = createLabel(toolkit, composite, StringPool.EMPTY);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label.setLayoutData(data);

		Label passwordLabel = createLabel(toolkit, composite, Msgs.password);

		passwordLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		password = toolkit.createText(composite, null, SWT.PASSWORD);

		password.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		password.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (updating) {
						return;
					}

					updating = true;
					execute(new SetPasswordCommand(tomcatServer, StringUtil.trim(password.getText())));
					updating = false;
				}

			});

		setDefault = toolkit.createHyperlink(composite, Msgs.restoreDefaultsLink, SWT.WRAP);

		setDefault.addHyperlinkListener(
			new HyperlinkAdapter() {

				public void linkActivated(HyperlinkEvent e) {
					updating = true;
					execute(new SetMemoryArgsCommand(tomcatServer, ILiferayTomcatConstants.DEFAULT_MEMORY_ARGS));
					memoryArgs.setText(ILiferayTomcatConstants.DEFAULT_MEMORY_ARGS);
					execute(new SetUserTimezoneCommand(tomcatServer, ILiferayTomcatConstants.DEFAULT_USER_TIMEZONE));
					userTimezone.setText(ILiferayTomcatConstants.DEFAULT_USER_TIMEZONE);
					execute(new SetExternalPropertiesCommand(tomcatServer, StringPool.EMPTY));
					externalProperties.setText(StringPool.EMPTY);

					execute(new SetServerModeCommand(tomcatServer, tomcatServer.getDefaultServerMode()));
					standardServerMode.setSelection(
						tomcatServer.getDefaultServerMode() == ILiferayTomcatConstants.STANDARD_SERVER_MODE);
					developmentServerMode.setSelection(
						tomcatServer.getDefaultServerMode() == ILiferayTomcatConstants.DEVELOPMENT_SERVER_MODE);

					execute(
						new SetUseDefaultPortalSeverSettingsCommand(
							tomcatServer, tomcatServer.getUseDefaultPortalServerSettings()));
					useDefaultPortalServerSettings.setSelection(tomcatServer.getUseDefaultPortalServerSettings());
					_applyDefaultPortalServerSetting(tomcatServer.getUseDefaultPortalServerSettings());

					updating = false;
					validate();
				}

			});

		data = new GridData(SWT.FILL, SWT.CENTER, true, false);

		data.horizontalSpan = 3;
		setDefault.setLayoutData(data);

		initialize();
	}

	public void dispose() {
		if (server != null) {
			server.removePropertyChangeListener(listener);

			IServer originalServer = server.getOriginal();

			if (originalServer != null) {
				originalServer.removePublishListener(publishListener);
			}
		}
	}

	public IStatus[] getSaveStatus() {
		if (tomcatServer != null) {

			// Check the instance directory

			String dir = tomcatServer.getInstanceDirectory();

			if (dir != null) {
				IPath path = new Path(dir);

				// Must not be the same as the workspace location

				if ((dir.length() == 0) || workspacePath.equals(path)) {
					return new IStatus[] {
						new Status(IStatus.ERROR, LiferayServerUI.PLUGIN_ID, Msgs.errorServerDirIsRoot)
					};
				}

				// User specified value may not be under the ".metadata" folder of the workspace

				else if (workspacePath.isPrefixOf(path) ||
						 (!path.isAbsolute() && _METADATADIR.equals(path.segment(0)))) {

					int cnt = path.matchingFirstSegments(workspacePath);

					if (_METADATADIR.equals(path.segment(cnt))) {
						return new IStatus[] {
							new Status(
								IStatus.ERROR, LiferayServerUI.PLUGIN_ID,
								NLS.bind(Msgs.errorServerDirUnderRoot, _METADATADIR))
						};
					}
				}
				else if (path.equals(installDirPath)) {
					String binding = NLS.bind(Msgs.serverEditorServerDirInstall, StringPool.EMPTY);

					return new IStatus[] {
						new Status(
							IStatus.ERROR, LiferayServerUI.PLUGIN_ID,
							NLS.bind(Msgs.errorServerDirCustomNotInstall, binding.trim()))
					};
				}
			}
			else {
				IPath path = tomcatServer.getRuntimeBaseDirectory();

				// If non-custom instance dir is not the install and metadata isn't the selection, return error

				if (!path.equals(installDirPath)) {
					return new IStatus[] {
						new Status(
							IStatus.ERROR, LiferayServerUI.PLUGIN_ID, Msgs.changingRuntimeBaseDirectoriesNotSupported)
					};
				}
			}

			// Check the deployment directory

			dir = tomcatServer.getDeployDirectory();

			// Deploy directory must be set

			if ((dir == null) || (dir.length() == 0)) {
				return new IStatus[] {
					new Status(IStatus.ERROR, LiferayServerUI.PLUGIN_ID, Msgs.errorDeployDirNotSpecified)
				};
			}

			String externalPropetiesValue = tomcatServer.getExternalProperties();

			if (!CoreUtil.isNullOrEmpty(externalPropetiesValue)) {
				File externalPropertiesFile = new File(externalPropetiesValue);

				if (FileUtil.notExists(externalPropertiesFile) ||
					!ServerUtil.isValidPropertiesFile(externalPropertiesFile)) {

					return new IStatus[] {
						new Status(IStatus.ERROR, LiferayServerUI.PLUGIN_ID, Msgs.invalidExternalProperitesFile)
					};
				}
			}

			String memoryValue = memoryArgs.getText();

			String[] memory = DebugPlugin.parseArguments(memoryValue);

			if (!CoreUtil.isNullOrEmpty(memoryValue)) {
				for (String str : memory) {
					if (!str.startsWith("-X")) {
						return new IStatus[] {
							new Status(
								IStatus.ERROR, LiferayServerUI.PLUGIN_ID,
								"Error in memory argument format, expecting it to start with '-X'")
						};
					}
				}
			}
		}

		// use default implementation to return success

		return super.getSaveStatus();
	}

	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);

		// Cache workspace and default deploy paths

		workspacePath = CoreUtil.getWorkspaceRootLocation();

		defaultDeployPath = new Path(ILiferayTomcatConstants.DEFAULT_DEPLOYDIR);

		if (server != null) {
			tomcatServer = (LiferayTomcatServer)server.loadAdapter(LiferayTomcatServer.class, null);
			addChangeListeners();
		}
	}

	protected void addChangeListeners() {
		listener = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent event) {
				if (updating) {
					return;
				}

				updating = true;

				if (ITomcatServer.PROPERTY_INSTANCE_DIR.equals(event.getPropertyName()) ||
					ITomcatServer.PROPERTY_TEST_ENVIRONMENT.equals(event.getPropertyName())) {

					updateServerDirButtons();
					updateServerDirFields();
					validate();
				}
				else if (ILiferayTomcatServer.PROPERTY_MEMORY_ARGS.equals(event.getPropertyName())) {
					String s = (String)event.getNewValue();

					LiferayServerSettingsEditorSection.this.memoryArgs.setText(s);

					validate();
				}
				else if (ILiferayTomcatServer.PROPERTY_USER_TIMEZONE.equals(event.getPropertyName())) {
					String s = (String)event.getNewValue();

					LiferayServerSettingsEditorSection.this.userTimezone.setText(s);

					validate();
				}
				else if (ILiferayTomcatServer.PROPERTY_EXTERNAL_PROPERTIES.equals(event.getPropertyName())) {
					String s = (String)event.getNewValue();

					LiferayServerSettingsEditorSection.this.externalProperties.setText(s);

					validate();
				}
				else if (ILiferayTomcatServer.PROPERTY_SERVER_MODE.equals(event.getPropertyName())) {
					int s = (Integer)event.getNewValue();

					standardServerMode.setSelection(s == ILiferayTomcatConstants.STANDARD_SERVER_MODE);
					developmentServerMode.setSelection(s == ILiferayTomcatConstants.DEVELOPMENT_SERVER_MODE);

					validate();
				}
				else if (ILiferayTomcatServer.PROPERTY_USE_DEFAULT_PORTAL_SERVER_SETTINGS.equals(
							event.getPropertyName())) {

					boolean s = (Boolean)event.getNewValue();

					useDefaultPortalServerSettings.setSelection(s);

					validate();
				}

				updating = false;
			}

		};

		server.addPropertyChangeListener(listener);

		publishListener = new PublishAdapter() {

			public void publishFinished(IServer server2, IStatus status) {
				boolean flag = false;

				IModule[] modules = server2.getModules();

				if (status.isOK() && (modules.length == 0)) {
					flag = true;
				}

				if (flag != allowRestrictedEditing) {
					allowRestrictedEditing = flag;
				}
			}

		};

		IServer originalServer = server.getOriginal();

		originalServer.addPublishListener(publishListener);
	}

	protected Label createLabel(FormToolkit toolkit, Composite parent, String text) {
		Label label = toolkit.createLabel(parent, text);

		FormColors colors = toolkit.getColors();

		label.setForeground(colors.getColor(IFormColors.TITLE));

		return label;
	}

	protected void initialize() {
		if (tomcatServer == null) {
			return;
		}

		updating = true;

		IRuntime runtime = server.getRuntime();

		// If not Tomcat 3.2, update description to mention catalina.base

		IRuntimeType runtimeType = runtime.getRuntimeType();

		String runtimeId = runtimeType.getId();

		if ((runtime != null) && (runtimeId.indexOf("32") < 0)) {
			section.setDescription(Msgs.configureLiferayPortalServerSettings);
		}

		if (runtime != null) {
			installDirPath = runtime.getLocation();
		}

		// determine if editing of locations is allowed

		allowRestrictedEditing = false;
		IPath basePath = tomcatServer.getRuntimeBaseDirectory();

		if (!readOnly) {

			// If server has not been published, or server is published with no modules, allow editing

			File confFile = FileUtil.getFile(basePath.append("conf"));

			IServer original = server.getOriginal();

			IModule[] modules = original.getModules();

			if (((basePath != null) && FileUtil.notExists(confFile)) ||
				((original.getServerPublishState() == IServer.PUBLISH_STATE_NONE) && (modules.length == 0))) {

				allowRestrictedEditing = true;
			}
		}

		// Update server related fields

		updateServerDirButtons();

		// Update deployment related fields

		updateDefaultDeployLink();

		memoryArgs.setText(tomcatServer.getMemoryArgs());
		userTimezone.setText(tomcatServer.getUserTimezone());
		externalProperties.setText(tomcatServer.getExternalProperties());

		standardServerMode.setSelection(tomcatServer.getServerMode() == ILiferayTomcatConstants.STANDARD_SERVER_MODE);
		developmentServerMode.setSelection(
			tomcatServer.getServerMode() == ILiferayTomcatConstants.DEVELOPMENT_SERVER_MODE);
		username.setText(tomcatServer.getUsername());
		password.setText(tomcatServer.getPassword());

		useDefaultPortalServerSettings.setSelection(tomcatServer.getUseDefaultPortalServerSettings());
		_applyDefaultPortalServerSetting(tomcatServer.getUseDefaultPortalServerSettings());

		updating = false;
		validate();
	}

	protected void updateDefaultDeployLink() {

		/**
		 * boolean newState = defaultDeployPath.equals(new
		 *  Path(tomcatServer.getDeployDirectory()));
		 *  if (newState != defaultDeployDirIsSet) {
		 *  setDefaultDeployDir.setText(
		 *  newState ? Messages.serverEditorSetDefaultDeployDirLink2
		 *  : Messages.serverEditorSetDefaultDeployDirLink);
		 *  defaultDeployDirIsSet = newState;
		 *  }
		 */
	}

	protected void updateServerDirButtons() {
		if (tomcatServer.getInstanceDirectory() == null) {

			/**
			 * IPath path = tomcatServer.getRuntimeBaseDirectory();
			 *  if (path != null && path.equals(installDirPath)) {
			 * serverDirInstall.setSelection(true);
			 * serverDirMetadata.setSelection(false);
			 * serverDirCustom.setSelection(false);
			 * } else {
			 * serverDirMetadata.setSelection(true);
			 * serverDirInstall.setSelection(false);
			 * serverDirCustom.setSelection(false);
			 *  }
			 */
		}
		else {

			/**
			 * serverDirCustom.setSelection(true);
			 * serverDirMetadata.setSelection(false);
			 * serverDirInstall.setSelection(false);
			 */
		}
	}

	protected void updateServerDirFields() {

		/**
		 * updateServerDir();
		 * boolean customServerDir = true;//serverDirCustom.getSelection();
		 * serverDir.setEnabled(allowRestrictedEditing && customServerDir);
		 * serverDirBrowse.setEnabled(allowRestrictedEditing &&
		 * customServerDir);
		 */
	}

	protected void validate() {
		if (tomcatServer != null) {

			// Validate instance directory

			String dir = tomcatServer.getInstanceDirectory();

			if (dir != null) {
				IPath path = new Path(dir);

				// Must not be the same as the workspace location

				if ((dir.length() == 0) || workspacePath.equals(path)) {
					setErrorMessage(Msgs.errorServerDirIsRoot);

					return;
				}

				// User specified value may not be under the ".metadata" folder of the workspace

				else if (workspacePath.isPrefixOf(path) ||
						 (!path.isAbsolute() && _METADATADIR.equals(path.segment(0)))) {

					int cnt = path.matchingFirstSegments(workspacePath);

					if (_METADATADIR.equals(path.segment(cnt))) {
						setErrorMessage(NLS.bind(Msgs.errorServerDirUnderRoot, _METADATADIR));

						return;
					}
				}
				else if (path.equals(installDirPath)) {
					String binding = NLS.bind(Msgs.serverEditorServerDirInstall, StringPool.EMPTY);

					setErrorMessage(NLS.bind(Msgs.errorServerDirCustomNotInstall, binding.trim()));

					return;
				}
			}
			else {
				IPath path = tomcatServer.getRuntimeBaseDirectory();

				// If non-custom instance dir is not the install and metadata isn't the selection, return error

				String binding = NLS.bind(Msgs.serverEditorServerDirMetadata, StringPool.EMPTY);

				if ((path != null) && !path.equals(installDirPath)) {
					setErrorMessage(NLS.bind(Msgs.errorServerDirCustomNotMetadata, binding.trim()));
				}
			}

			// Check the deployment directory

			dir = tomcatServer.getDeployDirectory();

			// Deploy directory must be set

			if ((dir == null) || (dir.length() == 0)) {
				setErrorMessage(Msgs.errorDeployDirNotSpecified);

				return;
			}

			String externalPropetiesValue = tomcatServer.getExternalProperties();

			if (!CoreUtil.isNullOrEmpty(externalPropetiesValue)) {
				File externalPropertiesFile = new File(externalPropetiesValue);

				if (FileUtil.notExists(externalPropertiesFile) ||
					!ServerUtil.isValidPropertiesFile(externalPropertiesFile)) {

					setErrorMessage(Msgs.invalidExternalPropertiesFile);

					return;
				}
			}
		}

		// All is okay, clear any previous error

		setErrorMessage(null);
	}

	protected boolean allowRestrictedEditing;
	protected boolean defaultDeployDirIsSet;
	protected IPath defaultDeployPath;
	protected Button developmentServerMode;
	protected Text externalProperties;
	protected Button externalPropertiesBrowse;
	protected IPath installDirPath;
	protected PropertyChangeListener listener;
	protected Text memoryArgs;

	// Avoid hardcoding this at some point

	protected Text password;
	protected IPublishListener publishListener;
	protected Section section;
	protected Hyperlink setDefault;
	protected Button standardServerMode;
	protected IPath tempDirPath;
	protected LiferayTomcatServer tomcatServer;
	protected boolean updating;
	protected Button useDefaultPortalServerSettings;
	protected Text username;
	protected Text userTimezone;
	protected IPath workspacePath;

	private void _applyDefaultPortalServerSetting(boolean useDefaultPortalSeverSetting) {
		if (useDefaultPortalSeverSetting) {
			memoryArgs.setEnabled(false);
			userTimezone.setEnabled(false);
			externalProperties.setEnabled(false);
			standardServerMode.setEnabled(false);
			developmentServerMode.setEnabled(false);
			externalPropertiesBrowse.setEnabled(false);
		}
		else {
			memoryArgs.setEnabled(true);
			userTimezone.setEnabled(true);
			externalProperties.setEnabled(true);
			standardServerMode.setEnabled(true);
			developmentServerMode.setEnabled(true);
			externalPropertiesBrowse.setEnabled(true);
		}
	}

	private static final String _METADATADIR = ".metadata";

	private static class Msgs extends NLS {

		public static String changingRuntimeBaseDirectoriesNotSupported;
		public static String configureLiferayPortalServerSettings;
		public static String developmentServerMode;
		public static String editorBrowse;
		public static String errorDeployDirNotSpecified;
		public static String errorServerDirCustomNotInstall;
		public static String errorServerDirCustomNotMetadata;
		public static String errorServerDirIsRoot;
		public static String errorServerDirUnderRoot;
		public static String externalPropertiesLabel;
		public static String invalidExternalProperitesFile;
		public static String invalidExternalPropertiesFile;
		public static String liferaySettings;
		public static String memoryArgsLabel;
		public static String password;
		public static String restoreDefaultsLink;
		public static String serverEditorServerDirInstall;

		// public static String millisecondsLabel;

		public static String serverEditorServerDirMetadata;

		// public static String serverEditorBrowseDeployMessage;

		public static String serverMode;
		public static String standardServerMode;

		// public static String specifyAutoDeployInterval;

		public static String useDefaultPortalServerSetting;
		public static String username;
		public static String userTimezoneLabel;

		static {
			initializeMessages(LiferayServerSettingsEditorSection.class.getName(), Msgs.class);
		}

	}

}