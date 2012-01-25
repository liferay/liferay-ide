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

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.server.core.IPublishListener;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.util.PublishAdapter;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;
import org.jboss.ide.eclipse.as.ui.Messages;

import com.liferay.ide.eclipse.server.core.ILiferayServerConstants;
import com.liferay.ide.eclipse.server.jboss.core.LiferayJBoss7Server;

/**
 * @author kamesh
 */
public class LiferayServerSettingsSection extends ServerEditorSection
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

	// TODO add other properties based on need

	protected void addChangeListeners()
	{
		propertyChangeListener = new PropertyChangeListener()
		{

			@Override
			public void propertyChange( PropertyChangeEvent evt )
			{
				// TODO Auto-generated method stub

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

		// Add modify listenter

		addSpacer( toolkit, composite );

		// Timezone Settings
		label = createLabel( toolkit, composite, "User timezone:" );
		data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
		label.setLayoutData( data );

		userTimezone = toolkit.createText( composite, null );
		data = new GridData( SWT.FILL, SWT.CENTER, true, false );
		userTimezone.setLayoutData( data );

		// Add modify listenter

		addSpacer( toolkit, composite );

		// External Properties
		label = createLabel( toolkit, composite, "External properties:" );
		data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
		label.setLayoutData( data );

		externalProperties = toolkit.createText( composite, null );
		data = new GridData( SWT.FILL, SWT.CENTER, false, false );
		data.widthHint = 150;
		externalProperties.setLayoutData( data );

		// Add modify listenter
		externalPropertiesBrowse = toolkit.createButton( composite, Messages.browse, SWT.PUSH );
		externalPropertiesBrowse.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );
		// Add Selection Listener

		// auto deploy directory
		label = createLabel( toolkit, composite, "Auto deploy path:" );
		data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
		label.setLayoutData( data );

		autoDeployDir = toolkit.createText( composite, null );
		data = new GridData( SWT.FILL, SWT.CENTER, true, false );
		autoDeployDir.setLayoutData( data );

		// Add modify listenter
		autoDeployDirBrowse = toolkit.createButton( composite, Messages.browse, SWT.PUSH );
		// Add Selection Listener

		autoDeployDirBrowse.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );

		label = createLabel( toolkit, composite, "Auto deploy interval:" );
		data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
		label.setLayoutData( data );

		autoDeployInterval = toolkit.createText( composite, null );
		data = new GridData( SWT.FILL, SWT.CENTER, true, false );
		autoDeployInterval.setLayoutData( data );
		// Add modify listenter

		createLabel( toolkit, composite, "milliseconds" );
		data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
		label.setLayoutData( data );

		setDefault = toolkit.createHyperlink( composite, "Restore defaults.", SWT.WRAP );
		// Add Hyperlink Adapter

		data = new GridData( SWT.FILL, SWT.CENTER, true, false );
		data.horizontalSpan = 3;
		setDefault.setLayoutData( data );

		// initialize();
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
		// TODO
	}

}
