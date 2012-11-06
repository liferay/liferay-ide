/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ILiferayProjectImportDataModelProperties;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.sdk.SDKManager;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.wst.web.ui.internal.wizards.DataModelFacetCreationWizardPage;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( { "restriction" } )
public class LiferayProjectImportWizardPage extends DataModelFacetCreationWizardPage
    implements ILiferayProjectImportDataModelProperties
{
    protected Text projectLocation;
    protected Text pluginType;
    protected Text sdkVersion;
    protected ProjectRecord projectRecord = null;
    protected Combo serverTargetCombo;
    protected IProject[] wsProjects;

    public LiferayProjectImportWizardPage( IDataModel model, String pageName, IWizard parentWizard )
    {
        super( model, pageName );

        if( parentWizard instanceof NewProjectFromSourceWizard )
        {
            setTitle( "New Liferay Project from Existing Source" );
            setDescription( "Browse to an existing Liferay project to import." );
        }
        else
        {
            setTitle( "Import Liferay Project" );
            setDescription( "Select an existing Liferay project to import." );
        }
    }

    protected void createProjectLocationField( Composite topComposite )
    {
        SWTUtil.createLabel( topComposite, SWT.LEAD, "Liferay project location:", 1 );

        projectLocation = SWTUtil.createText( topComposite, 1 );
        this.synchHelper.synchText( projectLocation, PROJECT_LOCATION, null );
        this.synchHelper.getDataModel().addListener( new IDataModelListener()
        {
            public void propertyChanged( DataModelEvent event )
            {
                if( PROJECT_LOCATION.equals( event.getPropertyName() ) )
                {
                    updateProjectRecord( event.getDataModel().getStringProperty( PROJECT_LOCATION ) );
                    synchHelper.synchAllUIWithModel();
                    validatePage();
                }
            }
        } );

        Button iconFileBrowse = SWTUtil.createPushButton( topComposite, "Browse...", null );
        iconFileBrowse.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );
        iconFileBrowse.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                handleFileBrowseButton( LiferayProjectImportWizardPage.this.projectLocation );
            }

        } );
    }

    protected void createSDKVersionField( Composite topComposite )
    {
        SWTUtil.createLabel( topComposite, SWT.LEAD, "Liferay Plugin SDK version:", 1 );

        sdkVersion = SWTUtil.createText( topComposite, 1 );
        this.synchHelper.synchText( sdkVersion, SDK_VERSION, null );

        SWTUtil.createLabel( topComposite, "", 1 );
    }

    protected void createPluginTypeField( Composite topComposite )
    {
        SWTUtil.createLabel( topComposite, SWT.LEAD, "Liferay plugin type:", 1 );

        pluginType = SWTUtil.createText( topComposite, 1 );
        this.synchHelper.synchText( pluginType, PLUGIN_TYPE, null );

        SWTUtil.createLabel( topComposite, "", 1 );
    }

    protected void createTargetRuntimeGroup( Composite parent )
    {
        Label label = new Label( parent, SWT.NONE );
        label.setText( "Liferay target runtime:" );
        label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

        serverTargetCombo = new Combo( parent, SWT.BORDER | SWT.READ_ONLY );
        serverTargetCombo.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

        Button newServerTargetButton = new Button( parent, SWT.NONE );
        newServerTargetButton.setText( "New..." );
        newServerTargetButton.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                final DataModelPropertyDescriptor[] preAdditionDescriptors =
                    model.getValidPropertyDescriptors( FACET_RUNTIME );

                boolean isOK = ServerUIUtil.showNewRuntimeWizard( getShell(), getModuleTypeID(), null, "com.liferay." );

                if( isOK )
                {
                    DataModelPropertyDescriptor[] postAdditionDescriptors =
                        model.getValidPropertyDescriptors( FACET_RUNTIME );

                    Object[] preAddition = new Object[preAdditionDescriptors.length];

                    for( int i = 0; i < preAddition.length; i++ )
                    {
                        preAddition[i] = preAdditionDescriptors[i].getPropertyValue();
                    }

                    Object[] postAddition = new Object[postAdditionDescriptors.length];

                    for( int i = 0; i < postAddition.length; i++ )
                    {
                        postAddition[i] = postAdditionDescriptors[i].getPropertyValue();
                    }

                    Object newAddition = CoreUtil.getNewObject( preAddition, postAddition );

                    if( newAddition != null ) // can this ever be null?
                        model.setProperty( FACET_RUNTIME, newAddition );
                }
            }
        } );

        Control[] deps = new Control[] { newServerTargetButton };

        synchHelper.synchCombo( serverTargetCombo, FACET_RUNTIME, deps );

        if( serverTargetCombo.getSelectionIndex() == -1 && serverTargetCombo.getVisibleItemCount() != 0 )
        {
            serverTargetCombo.select( 0 );
        }
    }

    @Override
    protected Composite createTopLevelComposite( Composite parent )
    {
        Composite topComposite = SWTUtil.createTopComposite( parent, 3 );

        GridLayout gl = new GridLayout( 3, false );
        // gl.marginLeft = 5;
        topComposite.setLayout( gl );
        topComposite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );

        createProjectLocationField( topComposite );
        createPluginTypeField( topComposite );
        createSDKVersionField( topComposite );
        createTargetRuntimeGroup( topComposite );

        return topComposite;
    }

    @Override
    protected String[] getValidationPropertyNames()
    {
        return new String[] { PROJECT_LOCATION, PROJECT_RECORD, SDK_VERSION, PLUGIN_TYPE, FACET_RUNTIME };
    }

    protected void handleFileBrowseButton( final Text text )
    {
        DirectoryDialog dd = new DirectoryDialog( this.getShell(), SWT.OPEN );

        dd.setText( "Select Liferay project folder" );

        if( !CoreUtil.isNullOrEmpty( projectLocation.getText() ) )
        {
            dd.setFilterPath( projectLocation.getText() );
        }
        else
        {
            // FIX:IDE-242
            SDKManager sdkManager = SDKManager.getInstance();
            SDK defaultSDK = sdkManager.getDefaultSDK();
            if( defaultSDK != null )
            {
                String sdkLocation = defaultSDK.getLocation().toOSString();
                dd.setFilterPath( sdkLocation );
            }
            // END FIX:IDE-242
        }

        String dir = dd.open();

        if( !CoreUtil.isNullOrEmpty( dir ) )
        {
            projectLocation.setText( dir );
        }
    }

    protected void updateProjectRecord( String projectLocation )
    {
        if( CoreUtil.isNullOrEmpty( projectLocation ) )
        {
            return;
        }

        this.projectRecord = ProjectUtil.getProjectRecordForDir( projectLocation );

        getDataModel().setProperty( PROJECT_RECORD, this.projectRecord );
    }

    protected IProject[] getProjectsInWorkspace()
    {
        if( wsProjects == null )
        {
            wsProjects = IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getProjects();
        }
        return wsProjects;
    }

    protected boolean isProjectInWorkspace( String projectName )
    {
        if( projectName == null )
        {
            return false;
        }

        IProject[] workspaceProjects = getProjectsInWorkspace();

        for( int i = 0; i < workspaceProjects.length; i++ )
        {
            if( projectName.equals( workspaceProjects[i].getName() ) )
            {
                return true;
            }
        }

        return false;
    }
}
