/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.server.jboss.ui.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.server.core.IPublishListener;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.util.PublishAdapter;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;
import org.jboss.ide.eclipse.as.ui.Messages;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.server.core.ILiferayServerConstants;
import com.liferay.ide.eclipse.server.jboss.core.ILiferayJBossServer;
import com.liferay.ide.eclipse.server.jboss.core.LiferayJBoss7Server;
import com.liferay.ide.eclipse.server.jboss.ui.command.SetAutoDeployDirectoryCommand;
import com.liferay.ide.eclipse.server.jboss.ui.command.SetAutoDeployIntervalCommand;
import com.liferay.ide.eclipse.server.jboss.ui.command.SetExternalPropertiesCommand;
import com.liferay.ide.eclipse.server.jboss.ui.command.SetMemoryArgsCommand;
import com.liferay.ide.eclipse.server.jboss.ui.command.SetUserTimeZoneCommand;
import com.liferay.ide.eclipse.server.util.ServerUtil;

/**
 * @author kamesh
 */
public class LiferayServerSettingsEditorSection extends ServerEditorSection
{

	protected LiferayJBoss7Server liferayJBoss7Server;

	protected Section section;
	protected Hyperlink setDefault;

	protected boolean defaultDeployDirIsSet;

	protected Text memoryArgs;
	protected Text userTimezone;
	protected Text externalProperties;
	protected Text autoDeployDir;
	protected Button autoDeployDirBrowse;
	protected Button externalPropertiesBrowse;
	protected boolean updating;

	protected IPath workspacePath;
	protected IPath defaultDeployPath;

	protected boolean allowRestrictedEditing;
	protected IPath tempDirPath;
	protected IPath installDirPath;
	protected Text autoDeployInterval;

	private PropertyChangeListener propertyChangeListener;
	protected IPublishListener publishListener;

	// Avoid hardcoding this at some point
	// private final static String METADATADIR = ".metadata";

	protected void addChangeListeners()
	{
		propertyChangeListener = new PropertyChangeListener()
		{

			@Override
			public void propertyChange( PropertyChangeEvent event )
			{
				if ( ILiferayJBossServer.PROPERTY_AUTO_DEPLOY_DIR.equals( event.getPropertyName() ) )
				{
					String s = (String) event.getNewValue();
					LiferayServerSettingsEditorSection.this.autoDeployDir.setText( s );
					validate();
				}
				else if ( ILiferayJBossServer.PROPERTY_AUTO_DEPLOY_INTERVAL.equals( event.getPropertyName() ) )
				{
					String s = (String) event.getNewValue();
					LiferayServerSettingsEditorSection.this.autoDeployInterval.setText( s );
					validate();
				}
				else if ( ILiferayJBossServer.PROPERTY_MEMORY_ARGS.equals( event.getPropertyName() ) )
				{
					String s = (String) event.getNewValue();
					LiferayServerSettingsEditorSection.this.memoryArgs.setText( s );
					validate();
				}
				else if ( ILiferayJBossServer.PROPERTY_USER_TIMEZONE.equals( event.getPropertyName() ) )
				{
					String s = (String) event.getNewValue();
					LiferayServerSettingsEditorSection.this.userTimezone.setText( s );
					validate();
				}
				else if ( ILiferayJBossServer.PROPERTY_EXTERNAL_PROPERTIES.equals( event.getPropertyName() ) )
				{
					String s = (String) event.getNewValue();
					LiferayServerSettingsEditorSection.this.externalProperties.setText( s );
					validate();
				}

			}
		};

		publishListener = new PublishAdapter()
		{

			public void publishFinished( IServer server2, IStatus status )
			{
				boolean flag = false;
				if ( status.isOK() && server2.getModules().length == 0 )
					flag = true;
				if ( flag != allowRestrictedEditing )
				{
					allowRestrictedEditing = flag;
				}
			}
		};
		server.getOriginal().addPublishListener( publishListener );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.editor.ServerEditorSection#createSection(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createSection( Composite parent )
	{
		super.createSection( parent );
		FormToolkit toolkit = getFormToolkit( parent.getDisplay() );

		section =
			toolkit.createSection( parent, ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED |
				ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE );
		// section.setText(Messages.serverEditorLocationsSection);
		// section.setDescription(Messages.serverEditorLocationsDescription);
		section.setText( "Liferay settings" );
		section.setLayoutData( new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL ) );

		Composite composite = toolkit.createComposite( section );
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;
		composite.setLayout( layout );
		composite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL ) );
		// IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		// whs.setHelp( composite, org.eclipse.wst.server.ui.internal.ContextIds.SERVER_EDITOR );
		// whs.setHelp( section, ContextIds.SERVER_EDITOR );
		toolkit.paintBordersFor( composite );
		section.setClient( composite );

		GridData data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );

		// Memory Args
		Label label = createLabel( toolkit, composite, "Memory args:" );
		data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
		label.setLayoutData( data );

		memoryArgs = toolkit.createText( composite, null );
		data = new GridData( SWT.FILL, SWT.CENTER, true, false );
		memoryArgs.setLayoutData( data );

		memoryArgs.addModifyListener( new ModifyListener()
		{

			@Override
			public void modifyText( ModifyEvent e )
			{
				// TODO why this is required ??
				if ( updating )
				{
					return;
				}
				updating = true;
				execute( new SetMemoryArgsCommand(
					liferayJBoss7Server.getServerWorkingCopy(), memoryArgs.getText().trim() ) );
				updating = false;
				validate();
			}
		} );

		addSpacer( toolkit, composite );

		// Timezone Settings
		label = createLabel( toolkit, composite, "User timezone:" );
		data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
		label.setLayoutData( data );

		userTimezone = toolkit.createText( composite, null );
		data = new GridData( SWT.FILL, SWT.CENTER, true, false );
		userTimezone.setLayoutData( data );

		userTimezone.addModifyListener( new ModifyListener()
		{

			@Override
			public void modifyText( ModifyEvent e )
			{
				// TODO why this is required ??
				if ( updating )
				{
					return;
				}
				updating = true;
				execute( new SetUserTimeZoneCommand(
					liferayJBoss7Server.getServerWorkingCopy(), userTimezone.getText().trim() ) );
				updating = false;
				validate();
			}
		} );

		addSpacer( toolkit, composite );

		// External Properties
		label = createLabel( toolkit, composite, "External properties:" );
		data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
		label.setLayoutData( data );

		externalProperties = toolkit.createText( composite, null );
		data = new GridData( SWT.FILL, SWT.CENTER, false, false );
		data.widthHint = 150;
		externalProperties.setLayoutData( data );

		externalProperties.addModifyListener( new ModifyListener()
		{

			@Override
			public void modifyText( ModifyEvent e )
			{
				// TODO why this is required ??
				if ( updating )
				{
					return;
				}
				updating = true;
				execute( new SetExternalPropertiesCommand(
					liferayJBoss7Server.getServerWorkingCopy(), externalProperties.getText().trim() ) );
				updating = false;
				validate();
			}
		} );
		externalPropertiesBrowse = toolkit.createButton( composite, Messages.browse, SWT.PUSH );
		externalPropertiesBrowse.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );

		externalPropertiesBrowse.addSelectionListener( new SelectionAdapter()
		{

			public void widgetSelected( SelectionEvent se )
			{
				FileDialog dialog = new FileDialog( externalProperties.getShell() );
				dialog.setFilterPath( externalProperties.getText() );
				String selectedFile = dialog.open();
				if ( selectedFile != null && !selectedFile.equals( externalProperties.getText() ) )
				{
					updating = true;
					execute( new SetExternalPropertiesCommand(
						liferayJBoss7Server.getServerWorkingCopy(), externalProperties.getText().trim() ) );
					externalProperties.setText( selectedFile );
					updating = false;
					validate();
				}
			}
		} );

		// auto deploy directory
		label = createLabel( toolkit, composite, "Auto deploy path:" );
		data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
		label.setLayoutData( data );

		autoDeployDir = toolkit.createText( composite, null );
		data = new GridData( SWT.FILL, SWT.CENTER, true, false );
		autoDeployDir.setLayoutData( data );

		autoDeployDir.addModifyListener( new ModifyListener()
		{

			@Override
			public void modifyText( ModifyEvent e )
			{
				// TODO why this is required ??
				if ( updating )
				{
					return;
				}
				updating = true;
				execute( new SetAutoDeployDirectoryCommand(
					liferayJBoss7Server.getServerWorkingCopy(), autoDeployDir.getText().trim() ) );
				updating = false;
				validate();
			}
		} );

		autoDeployDirBrowse = toolkit.createButton( composite, Messages.browse, SWT.PUSH );
		autoDeployDirBrowse.addSelectionListener( new SelectionAdapter()
		{

			public void widgetSelected( SelectionEvent se )
			{
				FileDialog dialog = new FileDialog( autoDeployDir.getShell() );
				dialog.setFilterPath( autoDeployDir.getText() );
				String selectedFile = dialog.open();
				if ( selectedFile != null && !selectedFile.equals( autoDeployDir.getText() ) )
				{
					updating = true;
					execute( new SetAutoDeployDirectoryCommand(
						liferayJBoss7Server.getServerWorkingCopy(), autoDeployDir.getText().trim() ) );
					autoDeployDir.setText( selectedFile );
					updating = false;
					validate();
				}
			}
		} );

		autoDeployDirBrowse.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );

		label = createLabel( toolkit, composite, "Auto deploy interval:" );
		data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
		label.setLayoutData( data );

		autoDeployInterval = toolkit.createText( composite, null );
		data = new GridData( SWT.FILL, SWT.CENTER, true, false );
		autoDeployInterval.setLayoutData( data );
		autoDeployInterval.addModifyListener( new ModifyListener()
		{

			@Override
			public void modifyText( ModifyEvent e )
			{
				// TODO why this is required ??
				if ( updating )
				{
					return;
				}
				updating = true;
				execute( new SetAutoDeployIntervalCommand(
					liferayJBoss7Server.getServerWorkingCopy(), autoDeployInterval.getText().trim() ) );
				updating = false;
				validate();
			}
		} );

		createLabel( toolkit, composite, "milliseconds" );
		data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
		label.setLayoutData( data );

		setDefault = toolkit.createHyperlink( composite, "Restore defaults.", SWT.WRAP );
		setDefault.addHyperlinkListener( new HyperlinkAdapter()
		{

			public void linkActivated( HyperlinkEvent e )
			{
				updating = true;

				memoryArgs.setText( ILiferayServerConstants.DEFAULT_MEMORY_ARGS );
				execute( new SetMemoryArgsCommand(
					liferayJBoss7Server.getServerWorkingCopy(), ILiferayServerConstants.DEFAULT_MEMORY_ARGS ) );

				execute( new SetUserTimeZoneCommand(
					liferayJBoss7Server.getServerWorkingCopy(), ILiferayServerConstants.DEFAULT_USER_TIMEZONE ) );
				userTimezone.setText( ILiferayServerConstants.DEFAULT_USER_TIMEZONE );

				execute( new SetExternalPropertiesCommand( liferayJBoss7Server.getServerWorkingCopy(), "" ) );
				externalProperties.setText( "" );

				autoDeployDir.setText( ILiferayServerConstants.DEFAULT_AUTO_DEPLOYDIR );
				execute( new SetAutoDeployDirectoryCommand(
					liferayJBoss7Server.getServerWorkingCopy(), ILiferayServerConstants.DEFAULT_AUTO_DEPLOYDIR ) );

				autoDeployInterval.setText( ILiferayServerConstants.DEFAULT_AUTO_DEPLOY_INTERVAL );
				execute( new SetAutoDeployIntervalCommand(
					liferayJBoss7Server.getServerWorkingCopy(), ILiferayServerConstants.DEFAULT_AUTO_DEPLOY_INTERVAL ) );
				updating = false;
				validate();
			}
		} );

		data = new GridData( SWT.FILL, SWT.CENTER, true, false );
		data.horizontalSpan = 3;
		setDefault.setLayoutData( data );

		initialize();
	}

	private void addSpacer( FormToolkit toolkit, Composite composite )
	{
		GridData data;
		Label label;
		label = createLabel( toolkit, composite, "" );
		data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
		label.setLayoutData( data );
	}

	protected Label createLabel( FormToolkit toolkit, Composite parent, String text )
	{
		Label label = toolkit.createLabel( parent, text );
		label.setForeground( toolkit.getColors().getColor( IFormColors.TITLE ) );
		return label;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.editor.ServerEditorSection#init(org.eclipse.ui.IEditorSite,
	 * org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init( IEditorSite site, IEditorInput input )
	{
		super.init( site, input );
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		workspacePath = root.getLocation();
		defaultDeployPath = new Path( ILiferayServerConstants.DEFAULT_DEPLOYDIR );

		if ( server != null )
		{
			liferayJBoss7Server = (LiferayJBoss7Server) server.loadAdapter( LiferayJBoss7Server.class, null );
			addChangeListeners();
		}
		initialize();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.editor.ServerEditorSection#dispose()
	 */
	@Override
	public void dispose()
	{
		if ( server != null )
		{
			server.removePropertyChangeListener( propertyChangeListener );
			if ( server.getOriginal() != null )
			{
				server.getOriginal().removePublishListener( publishListener );
			}
		}
	}

	protected void initialize()
	{
		if ( autoDeployInterval == null || liferayJBoss7Server == null )
			return;

		IRuntime runtime = server.getRuntime();

		if ( runtime != null )
		{
			installDirPath = runtime.getLocation();
		}
		memoryArgs.setText( liferayJBoss7Server.getMemoryArgs() );
		userTimezone.setText( liferayJBoss7Server.getUserTimezone() );
		externalProperties.setText( liferayJBoss7Server.getExternalProperties() );
		autoDeployDir.setText( liferayJBoss7Server.getAutoDeployDirectory() );
		autoDeployInterval.setText( liferayJBoss7Server.getAutoDeployInterval() );
		validate();

		// TODO check to see if we need to cross check the aroguments with run.conf or stanalone.conf

	}

	// TODO update it
	protected void validate()
	{
		if ( liferayJBoss7Server != null )
		{

			// Check the deployment directory
			String dir = liferayJBoss7Server.getDeployFolder();
			// Deploy directory must be set
			if ( dir == null || dir.length() == 0 )
			{
				setErrorMessage( "No deploy directory" );
				return;
			}

			String externalPropetiesValue = liferayJBoss7Server.getExternalProperties();

			if ( !CoreUtil.isNullOrEmpty( externalPropetiesValue ) )
			{
				File externalPropertiesFile = new File( externalPropetiesValue );

				if ( ( !externalPropertiesFile.exists() ) ||
					( !ServerUtil.isValidPropertiesFile( externalPropertiesFile ) ) )
				{
					setErrorMessage( "Invalid external properties file" );

					return;
				}
			}

			String autoDeployInterval = liferayJBoss7Server.getAutoDeployInterval();

			if ( CoreUtil.isNullOrEmpty( autoDeployInterval ) )
			{
				setErrorMessage( "Must specify auto deploy interval in milliseconds." );
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
					setErrorMessage( "Auto deploy interval is not an integer." );
					return;
				}

			}
		}

		// All is okay, clear any previous error
		setErrorMessage( null );
	}

}
