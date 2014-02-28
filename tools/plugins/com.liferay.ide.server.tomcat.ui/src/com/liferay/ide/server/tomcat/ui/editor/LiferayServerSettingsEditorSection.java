/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *    Greg Amerson <gregory.amerson@liferay.com>
 *******************************************************************************/
package com.liferay.ide.server.tomcat.ui.editor;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatConstants;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatServer;
import com.liferay.ide.server.tomcat.core.LiferayTomcatServer;
import com.liferay.ide.server.tomcat.ui.command.SetExternalPropertiesCommand;
import com.liferay.ide.server.tomcat.ui.command.SetMemoryArgsCommand;
import com.liferay.ide.server.tomcat.ui.command.SetServerModeCommand;
import com.liferay.ide.server.tomcat.ui.command.SetUserTimezoneCommand;
import com.liferay.ide.server.ui.LiferayServerUIPlugin;
import com.liferay.ide.server.util.ServerUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.IPublishListener;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.util.PublishAdapter;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;

/**
 * Portal Tomcat server location editor section
 */
@SuppressWarnings("restriction")
public class LiferayServerSettingsEditorSection extends ServerEditorSection {
	protected Section section;
	protected LiferayTomcatServer tomcatServer;

	protected Hyperlink setDefault;

	protected boolean defaultDeployDirIsSet;

//	protected Button serverDirMetadata;
//	protected Button serverDirInstall;
//	protected Button serverDirCustom;

//	protected Text serverDir;
//	protected Button serverDirBrowse;
//	protected Text deployDir;
//	protected Button deployDirBrowse;
	protected Text memoryArgs;
	protected Text userTimezone;
	protected Text externalProperties;
//	protected Text autoDeployDir;
//	protected Button autoDeployDirBrowse;
    protected Button standardServerMode;
    protected Button developmentServerMode;
	protected Button externalPropertiesBrowse;
	protected boolean updating;

	protected PropertyChangeListener listener;
	protected IPublishListener publishListener;
	protected IPath workspacePath;
	protected IPath defaultDeployPath;

	protected boolean allowRestrictedEditing;
	protected IPath tempDirPath;
	protected IPath installDirPath;
//	protected Text autoDeployInterval;

	// Avoid hardcoding this at some point
	private final static String METADATADIR = ".metadata"; //$NON-NLS-1$
	/**
	 * ServerGeneralEditorPart constructor comment.
	 */
	public LiferayServerSettingsEditorSection() {
		// do nothing
	}

	/**
	 * Add listeners to detect undo changes and publishing of the server.
	 */
	protected void addChangeListeners() {
		listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (updating)
					return;

				updating = true;

				if (ITomcatServer.PROPERTY_INSTANCE_DIR.equals(event.getPropertyName())
						|| ITomcatServer.PROPERTY_TEST_ENVIRONMENT.equals(event.getPropertyName())) {
					updateServerDirButtons();
					updateServerDirFields();
					validate();
				}
//				else if (ILiferayTomcatServer.PROPERTY_AUTO_DEPLOY_DIR.equals(event.getPropertyName())) {
//					String s = (String) event.getNewValue();
//					LiferayServerSettingsEditorSection.this.autoDeployDir.setText(s);
//					updateDefaultDeployLink();
//					validate();
//				}
//				else if (ILiferayTomcatServer.PROPERTY_AUTO_DEPLOY_INTERVAL.equals(event.getPropertyName())) {
//					String s = (String) event.getNewValue();
//					LiferayServerSettingsEditorSection.this.autoDeployInterval.setText(s);
//					updateDefaultDeployLink();
//					validate();
//				}
				else if (ILiferayTomcatServer.PROPERTY_MEMORY_ARGS.equals(event.getPropertyName())) {
					String s = (String) event.getNewValue();
					LiferayServerSettingsEditorSection.this.memoryArgs.setText(s);
					validate();
				}
				else if (ILiferayTomcatServer.PROPERTY_USER_TIMEZONE.equals(event.getPropertyName())) {
					String s = (String) event.getNewValue();
					LiferayServerSettingsEditorSection.this.userTimezone.setText(s);
					validate();
				}
				else if (ILiferayTomcatServer.PROPERTY_EXTERNAL_PROPERTIES.equals(event.getPropertyName())) {
					String s = (String) event.getNewValue();
					LiferayServerSettingsEditorSection.this.externalProperties.setText(s);
					validate();
				}
				else if (ILiferayTomcatServer.PROPERTY_SERVER_MODE.equals(event.getPropertyName())) {
                    int s = (Integer) event.getNewValue();
                    standardServerMode.setSelection( s == ILiferayTomcatConstants.STANDARD_SERVER_MODE );
                    developmentServerMode.setSelection( s == ILiferayTomcatConstants.DEVELOPMENT_SERVER_MODE );
                    validate();
                }

				updating = false;
			}
		};
		server.addPropertyChangeListener(listener);

		publishListener = new PublishAdapter() {
			public void publishFinished(IServer server2, IStatus status) {
				boolean flag = false;
				if (status.isOK() && server2.getModules().length == 0)
					flag = true;
				if (flag != allowRestrictedEditing) {
					allowRestrictedEditing = flag;
				}
			}
		};
		server.getOriginal().addPublishListener(publishListener);
	}

	/**
	 * Creates the SWT controls for this workbench part.
	 *
	 * @param parent the parent control
	 */
	public void createSection(Composite parent) {
		super.createSection(parent);
		FormToolkit toolkit = getFormToolkit(parent.getDisplay());

		section = toolkit.createSection(parent, ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED
			| ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);
//		section.setText(Messages.serverEditorLocationsSection);
//		section.setDescription(Messages.serverEditorLocationsDescription);
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
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(composite, ContextIds.SERVER_EDITOR);
		whs.setHelp(section, ContextIds.SERVER_EDITOR);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);

		/*
		serverDirMetadata = toolkit.createButton(composite,
				NLS.bind(Messages.serverEditorServerDirMetadata, Messages.serverEditorDoesNotModify), SWT.RADIO);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 3;
		serverDirMetadata.setLayoutData(data);
		serverDirMetadata.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (updating || !serverDirMetadata.getSelection())
					return;
				updating = true;
				execute(new SetTestEnvironmentCommand(tomcatServer, true));
				updateServerDirFields();
				updating = false;
				validate();
			}
		});

		serverDirInstall = toolkit.createButton(composite,
				NLS.bind(Messages.serverEditorServerDirInstall, Messages.serverEditorTakesControl), SWT.RADIO);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 3;
		serverDirInstall.setLayoutData(data);
		serverDirInstall.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (updating || !serverDirInstall.getSelection())
					return;
				updating = true;
				execute(new SetTestEnvironmentCommand(tomcatServer, false));
				updateServerDirFields();
				updating = false;
				validate();
			}
		});

		serverDirCustom = toolkit.createButton(composite,
				NLS.bind(Messages.serverEditorServerDirCustom, Messages.serverEditorDoesNotModify), SWT.RADIO);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 3;
		serverDirCustom.setLayoutData(data);
		serverDirCustom.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (updating || !serverDirCustom.getSelection())
					return;
				updating = true;
				execute(new SetTestEnvironmentCommand(tomcatServer, true));
				updateServerDirFields();
				updating = false;
				validate();
			}
		});
		*/

		// server directory
//		Label label = createLabel(toolkit, composite, Messages.serverEditorServerDir);
//		GridData data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
//		label.setLayoutData(data);

//		serverDir = toolkit.createText(composite, null, SWT.SINGLE);
//		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
//		data.widthHint = 75;
//		serverDir.setLayoutData(data);
//		serverDir.addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent e) {
//				if (updating)
//					return;
//				updating = true;
//				execute(new SetInstanceDirectoryCommand(tomcatServer, getServerDir()));
//				updating = false;
//				validate();
//			}
//		});

//		serverDirBrowse = toolkit.createButton(composite, Messages.editorBrowse, SWT.PUSH);
//		serverDirBrowse.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent se) {
//				DirectoryDialog dialog = new DirectoryDialog(serverDir.getShell());
//				dialog.setMessage(Messages.serverEditorBrowseDeployMessage);
//				dialog.setFilterPath(serverDir.getText());
//				String selectedDirectory = dialog.open();
//				if (selectedDirectory != null && !selectedDirectory.equals(serverDir.getText())) {
//					updating = true;
					// Make relative if relative to the workspace
//					IPath path = new Path(selectedDirectory);
//					if (workspacePath.isPrefixOf(path)) {
//						int cnt = path.matchingFirstSegments(workspacePath);
//						path = path.removeFirstSegments(cnt).setDevice(null);
//						selectedDirectory = path.toOSString();
//					}
//					execute(new SetInstanceDirectoryCommand(tomcatServer, selectedDirectory));
//					updateServerDirButtons();
//					updateServerDirFields();
//					updating = false;
//					validate();
//				}
//			}
//		});
//		serverDirBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));



		// deployment directory
//		Label label = createLabel(toolkit, composite, Messages.serverEditorDeployDir);
		GridData  data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
//		label.setLayoutData(data);

//		deployDir = toolkit.createText(composite, null);
//		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
//		deployDir.setLayoutData(data);
//		deployDir.addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent e) {
//				if (updating)
//					return;
//				updating = true;
//				execute(new SetDeployDirectoryCommand(tomcatServer, deployDir.getText().trim()));
//				updateDefaultDeployLink();
//				updating = false;
//				validate();
//			}
//		});

//		deployDirBrowse = toolkit.createButton(composite, Messages.editorBrowse, SWT.PUSH);
//		deployDirBrowse.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent se) {
//				DirectoryDialog dialog = new DirectoryDialog(deployDir.getShell());
//				dialog.setMessage(Messages.serverEditorBrowseDeployMessage);
//				dialog.setFilterPath(deployDir.getText());
//				String selectedDirectory = dialog.open();
//				if (selectedDirectory != null && !selectedDirectory.equals(deployDir.getText())) {
//					updating = true;
//					execute(new SetDeployDirectoryCommand(tomcatServer, selectedDirectory));
//					deployDir.setText(selectedDirectory);
//					updating = false;
//					validate();
//				}
//			}
//		});
//		deployDirBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));


		Label label = createLabel(toolkit, composite, Msgs.memoryArgsLabel);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		label.setLayoutData(data);

		memoryArgs = toolkit.createText(composite, null);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		memoryArgs.setLayoutData(data);
		memoryArgs.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (updating)
					return;
				updating = true;
				execute(new SetMemoryArgsCommand(tomcatServer, memoryArgs.getText().trim()));
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
		userTimezone.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (updating)
					return;
				updating = true;
				execute(new SetUserTimezoneCommand(tomcatServer, userTimezone.getText().trim()));
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
		externalProperties.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (updating) {
					return;
				}

				updating = true;
				execute(new SetExternalPropertiesCommand(tomcatServer, externalProperties.getText().trim()));
				updating = false;
				validate();
			}
		});

		externalPropertiesBrowse = toolkit.createButton(composite, Msgs.editorBrowse, SWT.PUSH);
		externalPropertiesBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		externalPropertiesBrowse.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent se) {
				FileDialog dialog = new FileDialog(externalPropertiesBrowse.getShell());
				dialog.setFilterPath(externalPropertiesBrowse.getText());
				String selectedFile = dialog.open();
				if (selectedFile != null && !selectedFile.equals(externalPropertiesBrowse.getText())) {
					updating = true;
					execute(new SetExternalPropertiesCommand(tomcatServer, selectedFile));
					externalProperties.setText(selectedFile);
					updating = false;
					validate();
				}
			}
		});

		/*
		// auto deploy directory
		label = createLabel(toolkit, composite, Msgs.autoDeployPathLabel);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		label.setLayoutData(data);

		autoDeployDir = toolkit.createText(composite, null);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		autoDeployDir.setLayoutData(data);
		autoDeployDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (updating)
					return;
				updating = true;
				execute(new SetAutoDeployDirectoryCommand(tomcatServer, autoDeployDir.getText().trim()));
				updateDefaultDeployLink();
				updating = false;
				validate();
			}
		});

		autoDeployDirBrowse = toolkit.createButton(composite, Msgs.editorBrowse, SWT.PUSH);
		autoDeployDirBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				DirectoryDialog dialog = new DirectoryDialog(autoDeployDir.getShell());
				dialog.setMessage(Msgs.serverEditorBrowseDeployMessage);
				dialog.setFilterPath(autoDeployDir.getText());
				String selectedDirectory = dialog.open();
				if (selectedDirectory != null && !selectedDirectory.equals(autoDeployDir.getText())) {
					updating = true;
					execute(new SetAutoDeployDirectoryCommand(tomcatServer, selectedDirectory));
					autoDeployDir.setText(selectedDirectory);
					updating = false;
					validate();
				}
			}
		});
		autoDeployDirBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		label = createLabel(toolkit, composite, Msgs.autoDeployIntervalLabel);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		label.setLayoutData(data);

		autoDeployInterval = toolkit.createText(composite, null);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		autoDeployInterval.setLayoutData(data);
		autoDeployInterval.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (updating) {
					return;
				}
				updating = true;
				execute(new SetAutoDeployIntervalCommand(tomcatServer, autoDeployInterval.getText()));
				updating = false;
				validate();
			}
		});

		createLabel(toolkit, composite, Msgs.millisecondsLabel);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		label.setLayoutData(data);
		*/

        label = createLabel(toolkit, composite, Msgs.serverMode);
        data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        label.setLayoutData(data);

        standardServerMode = new Button( composite, SWT.RADIO );
        standardServerMode.setText( Msgs.standardServerMode );
        data = new GridData( SWT.FILL, SWT.CENTER, false, false );
        standardServerMode.setLayoutData( data );

        standardServerMode.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                updating = true;
                execute( new SetServerModeCommand( tomcatServer, ILiferayTomcatConstants.STANDARD_SERVER_MODE ) );
                updating = false;
            }
        } );

        label = createLabel(toolkit, composite, StringPool.EMPTY);
        data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        label.setLayoutData(data);

        label = createLabel(toolkit, composite, StringPool.EMPTY);
        data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        label.setLayoutData(data);

        developmentServerMode = new Button( composite, SWT.RADIO );
        developmentServerMode.setText( Msgs.developmentServerMode );
        data = new GridData( SWT.FILL, SWT.CENTER, false, false );
        developmentServerMode.setLayoutData( data );

        developmentServerMode.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                updating = true;
                execute( new SetServerModeCommand( tomcatServer, ILiferayTomcatConstants.DEVELOPMENT_SERVER_MODE ) );
                updating = false;
            }
        } );

		setDefault = toolkit.createHyperlink(composite, Msgs.restoreDefaultsLink, SWT.WRAP);
		setDefault.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				updating = true;
				execute(new SetMemoryArgsCommand(tomcatServer, ILiferayTomcatConstants.DEFAULT_MEMORY_ARGS));
				memoryArgs.setText(ILiferayTomcatConstants.DEFAULT_MEMORY_ARGS);
				execute(new SetUserTimezoneCommand(tomcatServer, ILiferayTomcatConstants.DEFAULT_USER_TIMEZONE));
				userTimezone.setText(ILiferayTomcatConstants.DEFAULT_USER_TIMEZONE);
				execute(new SetExternalPropertiesCommand(tomcatServer, StringPool.EMPTY));
				externalProperties.setText(StringPool.EMPTY);
//				execute(new SetAutoDeployDirectoryCommand(tomcatServer, ILiferayTomcatConstants.DEFAULT_AUTO_DEPLOYDIR));
//				autoDeployDir.setText(ILiferayTomcatConstants.DEFAULT_AUTO_DEPLOYDIR);
//				execute(new SetAutoDeployIntervalCommand(tomcatServer, ILiferayTomcatConstants.DEFAULT_AUTO_DEPLOY_INTERVAL));
//				autoDeployInterval.setText(ILiferayTomcatConstants.DEFAULT_AUTO_DEPLOY_INTERVAL);
                execute( new SetServerModeCommand( tomcatServer, tomcatServer.getDefaultServerMode() ) );
                standardServerMode.setSelection(
                    tomcatServer.getDefaultServerMode() == ILiferayTomcatConstants.STANDARD_SERVER_MODE );
                developmentServerMode.setSelection(
                    tomcatServer.getDefaultServerMode() == ILiferayTomcatConstants.DEVELOPMENT_SERVER_MODE );
				updating = false;
				validate();
			}
		});
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 3;
		setDefault.setLayoutData(data);

		initialize();
	}

	protected Label createLabel(FormToolkit toolkit, Composite parent, String text) {
		Label label = toolkit.createLabel(parent, text);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		return label;
	}

	/**
	 * @see ServerEditorSection#dispose()
	 */
	public void dispose() {
		if (server != null) {
			server.removePropertyChangeListener(listener);
			if (server.getOriginal() != null)
				server.getOriginal().removePublishListener(publishListener);
		}
	}

	/**
	 * @see ServerEditorSection#init(IEditorSite, IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);

		// Cache workspace and default deploy paths
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		workspacePath = root.getLocation();
		defaultDeployPath = new Path(ILiferayTomcatConstants.DEFAULT_DEPLOYDIR);

		if (server != null) {
			tomcatServer = (LiferayTomcatServer) server.loadAdapter(LiferayTomcatServer.class, null);
			addChangeListeners();
		}

		//initialize();
	}

	/**
	 * Initialize the fields in this editor.
	 */
	protected void initialize() {
        if( tomcatServer == null )
        {
            return;
        }

		updating = true;

		IRuntime runtime = server.getRuntime();
		// If not Tomcat 3.2, update description to mention catalina.base
		if (runtime != null && runtime.getRuntimeType().getId().indexOf("32") < 0) //$NON-NLS-1$
//			section.setDescription(Messages.serverEditorLocationsDescription2);
			section.setDescription(Msgs.configureLiferayPortalServerSettings);
		if (runtime != null)
			installDirPath = runtime.getLocation();

		// determine if editing of locations is allowed
		allowRestrictedEditing = false;
		IPath basePath = tomcatServer.getRuntimeBaseDirectory();
		if (!readOnly) {
			// If server has not been published, or server is published with no modules, allow editing
			if ((basePath != null && !basePath.append("conf").toFile().exists()) //$NON-NLS-1$
					|| (server.getOriginal().getServerPublishState() == IServer.PUBLISH_STATE_NONE
							&& server.getOriginal().getModules().length == 0)) {
				allowRestrictedEditing = true;
			}
		}

		// Update server related fields
		updateServerDirButtons();
//		updateServerDirFields();

//		serverDirMetadata.setEnabled(allowRestrictedEditing);
//		serverDirInstall.setEnabled(allowRestrictedEditing);
//		serverDirCustom.setEnabled(allowRestrictedEditing);

		// Update deployment related fields
		updateDefaultDeployLink();

//		deployDir.setText(tomcatServer.getDeployDirectory());
		memoryArgs.setText(tomcatServer.getMemoryArgs());
		userTimezone.setText(tomcatServer.getUserTimezone());
		externalProperties.setText(tomcatServer.getExternalProperties());
//		autoDeployDir.setText(tomcatServer.getAutoDeployDirectory());
//		autoDeployInterval.setText(tomcatServer.getAutoDeployInterval());

        standardServerMode.setSelection(
            tomcatServer.getServerMode() == ILiferayTomcatConstants.STANDARD_SERVER_MODE );
        developmentServerMode.setSelection(
            tomcatServer.getServerMode() == ILiferayTomcatConstants.DEVELOPMENT_SERVER_MODE );

		// setDefaultDeployDir.setEnabled(allowRestrictedEditing);
//		deployDir.setEnabled(allowRestrictedEditing);
//		deployDirBrowse.setEnabled(allowRestrictedEditing);
		// autoDeployDir.setEnabled(allowRestrictedEditing);
		// autoDeployDirBrowse.setEnabled(allowRestrictedEditing);
		// autoDeployInterval.setEnabled(allowRestrictedEditing);

		updating = false;
		validate();
	}

	/*
	protected String getServerDir() {
		String dir = null;
		if (serverDir != null) {
			dir = serverDir.getText().trim();
			IPath path = new Path(dir);
			// Adjust if the temp dir is known and has been entered
			if (tempDirPath != null && tempDirPath.equals(path))
				dir = null;
			// If under the workspace, make relative
			else if (workspacePath.isPrefixOf(path)) {
				int cnt = path.matchingFirstSegments(workspacePath);
				path = path.removeFirstSegments(cnt).setDevice(null);
				dir = path.toOSString();
			}
		}
		return dir;
	}
	*/

	protected void updateServerDirButtons() {
		if (tomcatServer.getInstanceDirectory() == null) {
//			IPath path = tomcatServer.getRuntimeBaseDirectory();
//			if (path != null && path.equals(installDirPath)) {
//				serverDirInstall.setSelection(true);
//				serverDirMetadata.setSelection(false);
//				serverDirCustom.setSelection(false);
//			} else {
//				serverDirMetadata.setSelection(true);
//				serverDirInstall.setSelection(false);
//				serverDirCustom.setSelection(false);
//			}
		} else {
//			serverDirCustom.setSelection(true);
//			serverDirMetadata.setSelection(false);
//			serverDirInstall.setSelection(false);
		}
	}

	protected void updateServerDirFields() {
//		updateServerDir();
//		boolean customServerDir = true;//serverDirCustom.getSelection();
//		serverDir.setEnabled(allowRestrictedEditing && customServerDir);
//		serverDirBrowse.setEnabled(allowRestrictedEditing && customServerDir);
	}

	/*
	protected void updateServerDir() {
		IPath path = tomcatServer.getRuntimeBaseDirectory();
		if (path == null)
			serverDir.setText("");
		else if (workspacePath.isPrefixOf(path)) {
			int cnt = path.matchingFirstSegments(workspacePath);
			path = path.removeFirstSegments(cnt).setDevice(null);
			serverDir.setText(path.toOSString());
			// cache the relative temp dir path if that is what we have
			if (tempDirPath == null) {
				if (tomcatServer.isTestEnvironment() && tomcatServer.getInstanceDirectory() == null)
					tempDirPath = path;
			}
		} else
			serverDir.setText(path.toOSString());
	}
	*/

	protected void updateDefaultDeployLink() {
//		boolean newState = defaultDeployPath.equals(new Path(tomcatServer.getDeployDirectory()));
//		if (newState != defaultDeployDirIsSet) {
//			setDefaultDeployDir.setText(
//					newState ? Messages.serverEditorSetDefaultDeployDirLink2
//							: Messages.serverEditorSetDefaultDeployDirLink);
//			defaultDeployDirIsSet = newState;
//		}
	}

	/**
	 * @see ServerEditorSection#getSaveStatus()
	 */
	public IStatus[] getSaveStatus() {
		if (tomcatServer != null) {
			// Check the instance directory
			String dir = tomcatServer.getInstanceDirectory();
			if (dir != null) {
				IPath path = new Path(dir);
				// Must not be the same as the workspace location
				if (dir.length() == 0 || workspacePath.equals(path)) {
					return new IStatus [] {
							new Status(IStatus.ERROR, LiferayServerUIPlugin.PLUGIN_ID, Msgs.errorServerDirIsRoot)};
				}
				// User specified value may not be under the ".metadata" folder of the workspace
				else if (workspacePath.isPrefixOf(path)
						|| (!path.isAbsolute() && METADATADIR.equals(path.segment(0)))) {
					int cnt = path.matchingFirstSegments(workspacePath);
					if (METADATADIR.equals(path.segment(cnt))) {
						return new IStatus [] {
								new Status(IStatus.ERROR, LiferayServerUIPlugin.PLUGIN_ID, NLS.bind(Msgs.errorServerDirUnderRoot, METADATADIR))};
					}
				}
				else if (path.equals(installDirPath))
					return new IStatus [] {
						new Status(IStatus.ERROR, LiferayServerUIPlugin.PLUGIN_ID,
								NLS.bind(Msgs.errorServerDirCustomNotInstall,
										NLS.bind(Msgs.serverEditorServerDirInstall, StringPool.EMPTY).trim()))};
			}
			else {
				IPath path = tomcatServer.getRuntimeBaseDirectory();
				// If non-custom instance dir is not the install and metadata isn't the selection, return error
				if (!path.equals(installDirPath) /* && !serverDirMetadata.getSelection()*/) {
					return new IStatus [] {
							new Status(IStatus.ERROR, LiferayServerUIPlugin.PLUGIN_ID, Msgs.changingRuntimeBaseDirectoriesNotSupported)};
				}
			}

			// Check the deployment directory
			dir = tomcatServer.getDeployDirectory();
			// Deploy directory must be set
			if (dir == null || dir.length() == 0) {
				return new IStatus [] {
						new Status(IStatus.ERROR, LiferayServerUIPlugin.PLUGIN_ID, Msgs.errorDeployDirNotSpecified)};
			}

			String externalPropetiesValue = tomcatServer.getExternalProperties();

			if (!CoreUtil.isNullOrEmpty(externalPropetiesValue)) {
				File externalPropertiesFile = new File(externalPropetiesValue);

				if ((!externalPropertiesFile.exists()) || (!ServerUtil.isValidPropertiesFile(externalPropertiesFile))) {
					return new IStatus[] {
						new Status(IStatus.ERROR, LiferayServerUIPlugin.PLUGIN_ID, Msgs.invalidExternalProperitesFile)
					};
				}
			}

			/*
			String autoDeployInterval = tomcatServer.getAutoDeployInterval();

			if ( CoreUtil.isNullOrEmpty( autoDeployInterval ) )
			{
                return new IStatus[] { new Status(
                    IStatus.ERROR, LiferayServerUIPlugin.PLUGIN_ID, Msgs.specifyAutoDeployInterval ) };
			}
			else
			{
				try
				{
					Integer.parseInt( autoDeployInterval );
				}
				catch ( NumberFormatException e )
				{
                    return new IStatus[] { new Status(
                        IStatus.ERROR, LiferayServerUIPlugin.PLUGIN_ID, Msgs.autoDeployIntervalNotInteger ) };
				}

			}
			*/
		}
		// use default implementation to return success
		return super.getSaveStatus();
	}

	protected void validate() {
		if (tomcatServer != null) {
			// Validate instance directory
			String dir = tomcatServer.getInstanceDirectory();
			if (dir != null) {
				IPath path = new Path(dir);
				// Must not be the same as the workspace location
				if (dir.length() == 0 || workspacePath.equals(path)) {
					setErrorMessage(Msgs.errorServerDirIsRoot);
					return;
				}
				// User specified value may not be under the ".metadata" folder of the workspace
				else if (workspacePath.isPrefixOf(path)
						|| (!path.isAbsolute() && METADATADIR.equals(path.segment(0)))) {
					int cnt = path.matchingFirstSegments(workspacePath);
					if (METADATADIR.equals(path.segment(cnt))) {
						setErrorMessage(NLS.bind(Msgs.errorServerDirUnderRoot, METADATADIR));
						return;
					}
				}
				else if (path.equals(installDirPath)) {
					setErrorMessage(NLS.bind(Msgs.errorServerDirCustomNotInstall,
							NLS.bind(Msgs.serverEditorServerDirInstall, StringPool.EMPTY).trim()));
					return;
				}
			}
			else {
				IPath path = tomcatServer.getRuntimeBaseDirectory();
				// If non-custom instance dir is not the install and metadata isn't the selection, return error
				if (path != null && !path.equals(installDirPath) /*&& !serverDirMetadata.getSelection()*/) {
					setErrorMessage(NLS.bind(Msgs.errorServerDirCustomNotMetadata,
							NLS.bind(Msgs.serverEditorServerDirMetadata, StringPool.EMPTY).trim()));
				}
			}

			// Check the deployment directory
			dir = tomcatServer.getDeployDirectory();
			// Deploy directory must be set
			if (dir == null || dir.length() == 0) {
				setErrorMessage(Msgs.errorDeployDirNotSpecified);
				return;
			}

			String externalPropetiesValue = tomcatServer.getExternalProperties();

			if (!CoreUtil.isNullOrEmpty(externalPropetiesValue)) {
				File externalPropertiesFile = new File(externalPropetiesValue);

				if ((!externalPropertiesFile.exists()) || (!ServerUtil.isValidPropertiesFile(externalPropertiesFile))) {
					setErrorMessage(Msgs.invalidExternalPropertiesFile);

					return;
				}
			}

			/*
			String autoDeployInterval = tomcatServer.getAutoDeployInterval();

			if ( CoreUtil.isNullOrEmpty( autoDeployInterval ) )
			{
				setErrorMessage( Msgs.specifyAutoDeployInterval );
				return;
			}
			else
			{
				try
				{
					Integer.parseInt( autoDeployInterval );
				}
				catch ( NumberFormatException e )
				{
					setErrorMessage( Msgs.autoDeployIntervalNotInteger );
					return;
				}

			}
			*/
		}

		// All is okay, clear any previous error
		setErrorMessage(null);
	}

    private static class Msgs extends NLS
    {
//    	public static String autoDeployIntervalLabel;
//      public static String autoDeployIntervalNotInteger;
//      public static String autoDeployPathLabel;
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
        public static String serverMode;
        public static String standardServerMode;
        public static String invalidExternalProperitesFile;
        public static String invalidExternalPropertiesFile;
        public static String liferaySettings;
        public static String memoryArgsLabel;
//      public static String millisecondsLabel;
        public static String restoreDefaultsLink;
//      public static String serverEditorBrowseDeployMessage;
        public static String serverEditorServerDirInstall;
        public static String serverEditorServerDirMetadata;
//      public static String specifyAutoDeployInterval;
        public static String userTimezoneLabel;

        static
        {
            initializeMessages( LiferayServerSettingsEditorSection.class.getName(), Msgs.class );
        }
    }
}