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
package com.liferay.ide.eclipse.server.tomcat.ui.editor;

import com.liferay.ide.eclipse.server.tomcat.core.IPortalTomcatConstants;
import com.liferay.ide.eclipse.server.tomcat.core.IPortalTomcatServer;
import com.liferay.ide.eclipse.server.tomcat.core.PortalTomcatServer;
import com.liferay.ide.eclipse.server.tomcat.ui.command.SetAutoDeployDirectoryCommand;
import com.liferay.ide.eclipse.server.tomcat.ui.command.SetAutoDeployIntervalCommand;
import com.liferay.ide.eclipse.server.tomcat.ui.command.SetMemoryArgsCommand;
import com.liferay.ide.eclipse.server.tomcat.ui.command.SetUserTimezoneCommand;
import com.liferay.ide.eclipse.server.ui.PortalServerUIPlugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatServer;
import org.eclipse.jst.server.tomcat.ui.internal.ContextIds;
import org.eclipse.jst.server.tomcat.ui.internal.Messages;
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
import org.eclipse.swt.widgets.DirectoryDialog;
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
public class PortalServerLocationEditorSection extends ServerEditorSection {
	protected Section section;
	protected PortalTomcatServer tomcatServer;

	protected Hyperlink setDefaultDeployDir;
	
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
	protected Text autoDeployDir;
	protected Button autoDeployDirBrowse;
	protected boolean updating;

	protected PropertyChangeListener listener;
	protected IPublishListener publishListener;
	protected IPath workspacePath;
	protected IPath defaultDeployPath;
	
	protected boolean allowRestrictedEditing;
	protected IPath tempDirPath;
	protected IPath installDirPath;
	private Text autoDeployInterval;

	// Avoid hardcoding this at some point
	private final static String METADATADIR = ".metadata";
	/**
	 * ServerGeneralEditorPart constructor comment.
	 */
	public PortalServerLocationEditorSection() {
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
				else if (ITomcatServer.PROPERTY_DEPLOY_DIR.equals(event.getPropertyName())) {
//					String s = (String) event.getNewValue();
//					PortalServerLocationEditorSection.this.deployDir.setText(s);
//					updateDefaultDeployLink();					
//					validate();
				}
				else if (IPortalTomcatServer.PROPERTY_AUTO_DEPLOY_DIR.equals(event.getPropertyName())) {
					String s = (String) event.getNewValue();
					PortalServerLocationEditorSection.this.autoDeployDir.setText(s);
					updateDefaultDeployLink();					
					validate();
				}
				else if (IPortalTomcatServer.PROPERTY_AUTO_DEPLOY_INTERVAL.equals(event.getPropertyName())) {
					String s = (String) event.getNewValue();
					PortalServerLocationEditorSection.this.autoDeployInterval.setText(s);
					updateDefaultDeployLink();					
					validate();
				}
				else if (IPortalTomcatServer.PROPERTY_MEMORY_ARGS.equals(event.getPropertyName())) {
					String s = (String) event.getNewValue();
					PortalServerLocationEditorSection.this.memoryArgs.setText(s);
					validate();
				}
				else if (IPortalTomcatServer.PROPERTY_USER_TIMEZONE.equals(event.getPropertyName())) {
					String s = (String) event.getNewValue();
					PortalServerLocationEditorSection.this.userTimezone.setText(s);
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
					// Update the state of the fields
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
//							boolean customServerDir = false;
//							if (!PortalServerLocationEditorSection.this.serverDirCustom.isDisposed())
//								customServerDir = PortalServerLocationEditorSection.this.serverDirCustom.getSelection();
//							if (!PortalServerLocationEditorSection.this.serverDirMetadata.isDisposed())
//								PortalServerLocationEditorSection.this.serverDirMetadata.setEnabled(allowRestrictedEditing);
//							if (!PortalServerLocationEditorSection.this.serverDirInstall.isDisposed())
//								PortalServerLocationEditorSection.this.serverDirInstall.setEnabled(allowRestrictedEditing);
//							if (!PortalServerLocationEditorSection.this.serverDirCustom.isDisposed())
//								PortalServerLocationEditorSection.this.serverDirCustom.setEnabled(allowRestrictedEditing);
//							if (!PortalServerLocationEditorSection.this.serverDir.isDisposed())
//								PortalServerLocationEditorSection.this.serverDir.setEnabled(allowRestrictedEditing && customServerDir);
//							if (!PortalServerLocationEditorSection.this.serverDirBrowse.isDisposed())
//								PortalServerLocationEditorSection.this.serverDirBrowse.setEnabled(allowRestrictedEditing && customServerDir);
							// if (!PortalServerLocationEditorSection.this.setDefaultDeployDir.isDisposed())
							// PortalServerLocationEditorSection.this.setDefaultDeployDir.setEnabled(allowRestrictedEditing);
//							if (!PortalServerLocationEditorSection.this.deployDir.isDisposed())
//								PortalServerLocationEditorSection.this.deployDir.setEnabled(allowRestrictedEditing);
//							if (!PortalServerLocationEditorSection.this.deployDirBrowse.isDisposed())
//								PortalServerLocationEditorSection.this.deployDirBrowse.setEnabled(allowRestrictedEditing);
							// if (!PortalServerLocationEditorSection.this.autoDeployDir.isDisposed())
							// PortalServerLocationEditorSection.this.autoDeployDir.setEnabled(allowRestrictedEditing);
							// if (!PortalServerLocationEditorSection.this.autoDeployDirBrowse.isDisposed())
							// PortalServerLocationEditorSection.this.autoDeployDirBrowse.setEnabled(allowRestrictedEditing);
							// if (!PortalServerLocationEditorSection.this.autoDeployInterval.isDisposed())
							// PortalServerLocationEditorSection.this.autoDeployInterval.setEnabled(allowRestrictedEditing);
						}
					});
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
		section.setText("Liferay settings");
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
		

		Label label = createLabel(toolkit, composite, "Memory args:");
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

		label = createLabel(toolkit, composite, "");
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		label.setLayoutData(data);

		label = createLabel(toolkit, composite, "User timezone:");
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

		label = createLabel(toolkit, composite, "");
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		label.setLayoutData(data);

		// auto deploy directory
		label = createLabel(toolkit, composite, "Auto deploy path:");
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

		autoDeployDirBrowse = toolkit.createButton(composite, Messages.editorBrowse, SWT.PUSH);
		autoDeployDirBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				DirectoryDialog dialog = new DirectoryDialog(autoDeployDir.getShell());
				dialog.setMessage(Messages.serverEditorBrowseDeployMessage);
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
		
		label = createLabel(toolkit, composite, "Auto deploy interval:");
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
		
		createLabel(toolkit, composite, "milliseconds");
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		label.setLayoutData(data);
		
		setDefaultDeployDir = toolkit.createHyperlink(composite, "Restore defaults.", SWT.WRAP);
		setDefaultDeployDir.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				updating = true;
//				execute(new SetDeployDirectoryCommand(tomcatServer, PortalTomcatServer.DEFAULT_DEPLOYDIR));
//				deployDir.setText(PortalTomcatServer.DEFAULT_DEPLOYDIR);
				execute(new SetMemoryArgsCommand(tomcatServer, IPortalTomcatConstants.DEFAULT_MEMORY_ARGS));
				memoryArgs.setText(IPortalTomcatConstants.DEFAULT_MEMORY_ARGS);
				execute(new SetUserTimezoneCommand(tomcatServer, IPortalTomcatConstants.DEFAULT_USER_TIMEZONE));
				userTimezone.setText(IPortalTomcatConstants.DEFAULT_USER_TIMEZONE);
				execute(new SetAutoDeployDirectoryCommand(tomcatServer, IPortalTomcatConstants.DEFAULT_AUTO_DEPLOYDIR));
				autoDeployDir.setText(IPortalTomcatConstants.DEFAULT_AUTO_DEPLOYDIR);
				execute(new SetAutoDeployIntervalCommand(tomcatServer, IPortalTomcatConstants.DEFAULT_AUTO_DEPLOY_INTERVAL));
				autoDeployInterval.setText(IPortalTomcatConstants.DEFAULT_AUTO_DEPLOY_INTERVAL);
				updateDefaultDeployLink();
				updating = false;
				validate();
			}
		});
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 3;
		setDefaultDeployDir.setLayoutData(data);

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
		defaultDeployPath = new Path(IPortalTomcatConstants.DEFAULT_DEPLOYDIR);

		if (server != null) {
			tomcatServer = (PortalTomcatServer) server.loadAdapter(PortalTomcatServer.class, null);
			addChangeListeners();
		}
		initialize();
	}

	/**
	 * Initialize the fields in this editor.
	 */
	protected void initialize() {
		if (/*deployDir == null ||*/autoDeployInterval == null || tomcatServer == null)
			return;
		updating = true;

		IRuntime runtime = server.getRuntime();
		// If not Tomcat 3.2, update description to mention catalina.base
		if (runtime != null && runtime.getRuntimeType().getId().indexOf("32") < 0)
//			section.setDescription(Messages.serverEditorLocationsDescription2);
			section.setDescription("Configure Liferay portal server settings.");
		if (runtime != null)
			installDirPath = runtime.getLocation();

		// determine if editing of locations is allowed
		allowRestrictedEditing = false;
		IPath basePath = tomcatServer.getRuntimeBaseDirectory();
		if (!readOnly) {
			// If server has not been published, or server is published with no modules, allow editing
			// TODO Find better way to determine if server hasn't been published
			if ((basePath != null && !basePath.append("conf").toFile().exists())
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
		autoDeployDir.setText(tomcatServer.getAutoDeployDirectory());
		autoDeployInterval.setText(tomcatServer.getAutoDeployInterval());

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
							new Status(IStatus.ERROR, PortalServerUIPlugin.PLUGIN_ID, Messages.errorServerDirIsRoot)};
				}
				// User specified value may not be under the ".metadata" folder of the workspace 
				else if (workspacePath.isPrefixOf(path)
						|| (!path.isAbsolute() && METADATADIR.equals(path.segment(0)))) {
					int cnt = path.matchingFirstSegments(workspacePath);
					if (METADATADIR.equals(path.segment(cnt))) {
						return new IStatus [] {
								new Status(IStatus.ERROR, PortalServerUIPlugin.PLUGIN_ID, NLS.bind(Messages.errorServerDirUnderRoot, METADATADIR))};
					}
				}
				else if (path.equals(installDirPath))
					return new IStatus [] {
						new Status(IStatus.ERROR, PortalServerUIPlugin.PLUGIN_ID,
								NLS.bind(Messages.errorServerDirCustomNotInstall,
										NLS.bind(Messages.serverEditorServerDirInstall, "").trim()))};
			}
			else {
				IPath path = tomcatServer.getRuntimeBaseDirectory();
				// If non-custom instance dir is not the install and metadata isn't the selection, return error
				if (!path.equals(installDirPath) /* && !serverDirMetadata.getSelection()*/) {
					return new IStatus [] {
							new Status(IStatus.ERROR, PortalServerUIPlugin.PLUGIN_ID, "Changing runtime base directories not supported.")};
				}
			}

			// Check the deployment directory
			dir = tomcatServer.getDeployDirectory();
			// Deploy directory must be set
			if (dir == null || dir.length() == 0) {
				return new IStatus [] {
						new Status(IStatus.ERROR, PortalServerUIPlugin.PLUGIN_ID, Messages.errorDeployDirNotSpecified)};
			}
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
					setErrorMessage(Messages.errorServerDirIsRoot);
					return;
				}
				// User specified value may not be under the ".metadata" folder of the workspace 
				else if (workspacePath.isPrefixOf(path)
						|| (!path.isAbsolute() && METADATADIR.equals(path.segment(0)))) {
					int cnt = path.matchingFirstSegments(workspacePath);
					if (METADATADIR.equals(path.segment(cnt))) {
						setErrorMessage(NLS.bind(Messages.errorServerDirUnderRoot, METADATADIR));
						return;
					}
				}
				else if (path.equals(installDirPath)) {
					setErrorMessage(NLS.bind(Messages.errorServerDirCustomNotInstall,
							NLS.bind(Messages.serverEditorServerDirInstall, "").trim()));
					return;
				}
			}
			else {
				IPath path = tomcatServer.getRuntimeBaseDirectory();
				// If non-custom instance dir is not the install and metadata isn't the selection, return error
				if (path != null && !path.equals(installDirPath) /*&& !serverDirMetadata.getSelection()*/) {
					setErrorMessage(NLS.bind(Messages.errorServerDirCustomNotMetadata, 
							NLS.bind(Messages.serverEditorServerDirMetadata, "").trim()));
				}
			}

			// Check the deployment directory
			dir = tomcatServer.getDeployDirectory();
			// Deploy directory must be set
			if (dir == null || dir.length() == 0) {
				setErrorMessage(Messages.errorDeployDirNotSpecified);
				return;
			}
		}
		// All is okay, clear any previous error
		setErrorMessage(null);
	}
}