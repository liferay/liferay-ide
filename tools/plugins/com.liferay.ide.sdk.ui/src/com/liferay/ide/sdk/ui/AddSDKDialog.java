/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.sdk.ui;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.ui.util.SWTUtil;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Greg Amerson
 * @author kameshs
 */
public class AddSDKDialog extends TitleAreaDialog implements ModifyListener
{
    protected SDK[] existingSDKs;
    protected String lastLocation;
    protected String lastName;
    protected Text location;
    protected Text name;
    protected SDK sdkToEdit;
    protected Combo serverTargetCombo;
    protected boolean shouldAddProject = true;
    protected boolean shouldOpenInEclipse = false;

    public AddSDKDialog( Shell parent, SDK[] existingSDKs )
    {
        super( parent );
        configure( existingSDKs );
    }

    public AddSDKDialog( Shell shell, SDK[] existingSDKs, SDK sdk )
    {
        super( shell );
        this.sdkToEdit = sdk;
        configure( existingSDKs );
    }

    public boolean getAddProject()
    {
        return this.shouldAddProject;
    }

    public String getLocation()
    {
        return lastLocation;
    }

    public String getName()
    {
        return lastName;
    }

    public boolean getOpenInEclipse()
    {
        return this.shouldOpenInEclipse;
    }

    public void modifyText( ModifyEvent e )
    {
        IStatus status = validate();

        if( !status.isOK() )
        {
            switch( status.getSeverity() )
            {

                case IStatus.WARNING:
                    setMessage( status.getMessage(), IMessageProvider.WARNING );
                    break;

                case IStatus.ERROR:
                    setMessage( status.getMessage(), IMessageProvider.ERROR );
                    this.getButton( IDialogConstants.OK_ID ).setEnabled( false );
                    break;
            }
        }
        else
        {
            this.getButton( IDialogConstants.OK_ID ).setEnabled( true );
            setMessage( getDefaultMessage(), IMessageProvider.NONE );
        }
    }

    protected void configure( SDK[] existingSdks )
    {
        this.existingSDKs = existingSdks;
        setShellStyle( getShellStyle() | SWT.RESIZE );
        setTitleImage( ImageDescriptor.createFromURL(
            SDKUIPlugin.getDefault().getBundle().getEntry( "/icons/wizban/sdk_wiz.png" ) ).createImage() ); //$NON-NLS-1$
    }

    @Override
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );

        shell.setText( NLS.bind( Msgs.liferayPluginSDK, ( sdkToEdit == null ? Msgs.newSDKAction : Msgs.editSDKAction ) ) );
    }

    @Override
    protected Control createButtonBar( Composite parent )
    {
        Control control = super.createButtonBar( parent );

        getButton( IDialogConstants.OK_ID ).setEnabled( false );

        return control;
    }

    @Override
    protected Control createDialogArea( Composite parent )
    {
        setTitle( NLS.bind( Msgs.liferayPluginSDK, ( sdkToEdit == null ? Msgs.addSDKAction : Msgs.editSDKAction ) ) );

        setMessage( getDefaultMessage() );

        Composite container = (Composite) SWTUtil.createTopComposite( parent, 3 );
        container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );

        SWTUtil.createLabel( container, Msgs.locationLabel, 1 );

        location = SWTUtil.createSingleText( container, 1 );

        // Fix IDE-901
        location.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( e.widget instanceof Text )
                {
                    Text txtLocation = (Text) e.widget;
                    if( txtLocation.getText() != null )
                    {
                        IPath sdkLocationPath = new Path( txtLocation.getText() );
                        String strSdkLocDir = sdkLocationPath.toOSString();
                        if( sdkLocationPath.isValidPath( strSdkLocDir ) && SDKUtil.isValidSDKLocation( strSdkLocDir ) )
                        {
                            name.setText( sdkLocationPath.lastSegment() );
                        }
                    }
                }

            }
        } );
        
        Button browse = SWTUtil.createButton( container, Msgs.browse );
        browse.addSelectionListener
        (
            new SelectionAdapter()
            {
                @Override
                public void widgetSelected( SelectionEvent e )
                {
                    doBrowse();
                }
            }
        );

        if( sdkToEdit != null )
        {
            location.setText( sdkToEdit.getLocation().toOSString() );
        }

        location.addModifyListener( this );

        SWTUtil.createLabel( container, Msgs.nameLabel, 1 );

        name = SWTUtil.createSingleText( container, 1 );

        if( sdkToEdit != null )
        {
            name.setText( sdkToEdit.getName() );
        }

        name.addModifyListener( this );

        SWTUtil.createLabel( container, StringPool.EMPTY, 1 );// spacer

        SWTUtil.createLabel( container, StringPool.EMPTY, 1 );// spacer

        final Button addProject = SWTUtil.createCheckButton( container, Msgs.addEclipseProjectFile, null, true, 1 );

        SWTUtil.createLabel( container, StringPool.EMPTY, 1 ); // spacer

        SWTUtil.createLabel( container, StringPool.EMPTY, 1 );// spacer

        final Button openInEclipse = SWTUtil.createCheckButton( container, Msgs.openEclipse, null, true, 1 );
        openInEclipse.addSelectionListener
        (
            new SelectionAdapter()
            {
                @Override
                public void widgetSelected( SelectionEvent e )
                {
                    AddSDKDialog.this.shouldOpenInEclipse = openInEclipse.getSelection();
                }
            }
        );

        openInEclipse.setEnabled( shouldAddProject );
        openInEclipse.setSelection( shouldOpenInEclipse );

        addProject.addSelectionListener
        (
            new SelectionAdapter()
            {
                @Override
                public void widgetSelected( SelectionEvent e )
                {
                    boolean selection = addProject.getSelection();
                    AddSDKDialog.this.shouldAddProject = selection;
                    openInEclipse.setEnabled( selection );
                }
            }
        );

        if( sdkToEdit != null )
        {
            validate();
        }

        return container;
    }

    protected void doBrowse()
    {
        DirectoryDialog dd = new DirectoryDialog( this.getShell(), SWT.OPEN );

        /*
         * Fixed: IDE-392
         */
        String filterPath = location.getText();
        if( filterPath != null )
        {
            dd.setFilterPath( filterPath );
            dd.setText( NLS.bind( Msgs.selectFolderParamLabel, filterPath ) );
        }
        else
        {
            dd.setText( Msgs.selectFolderLabel );
        }

        if( CoreUtil.isNullOrEmpty( location.getText() ) )
        {
            dd.setFilterPath( location.getText() );
        }

        String dir = dd.open();

        if( !CoreUtil.isNullOrEmpty( dir ) )
        {
            location.setText( dir );

            if( SDKUtil.isValidSDKLocation( dir ) && CoreUtil.isNullOrEmpty( name.getText() ) )
            {
                IPath path = new Path( dir );

                if( path.isValidPath( dir ) )
                {
                    name.setText( path.lastSegment() );
                }
            }
        }
    }

    protected String getDefaultMessage()
    {
        return Msgs.configureLiferayPluginSDKLocation;
    }

    protected void updateRuntimeItems()
    {
        Collection<String> validRuntimes = new HashSet<String>();

        for( IRuntime runtime : ServerCore.getRuntimes() )
        {
            if( runtime.getRuntimeType().getId().startsWith( "com.liferay.ide.eclipse.server" ) ) //$NON-NLS-1$
            {
                validRuntimes.add( runtime.getName() );
            }
        }

        String[] runtimes = validRuntimes.toArray( new String[0] );

        serverTargetCombo.setItems( runtimes );

        if( serverTargetCombo.getSelectionIndex() < 0 && runtimes.length > 0 )
        {
            serverTargetCombo.select( 0 );
        }
    }

    protected IStatus validate()
    {
        lastName = name.getText();

        if( CoreUtil.isNullOrEmpty( lastName ) )
        {
            return CoreUtil.createErrorStatus( Msgs.nameHaveValue );
        }

        // make sure new sdk name doesn't collide with existing one
        // if ( existingSDKs != null ) {
        // for ( SDK sdk : existingSDKs ) {
        // if ( lastName.equals( sdk.getName() ) ) {
        // return CoreUtil.createErrorStatus( "Name already exists." );
        // }
        // }
        // }

        lastLocation = location.getText();

        if( CoreUtil.isNullOrEmpty( lastLocation ) )
        {
            return CoreUtil.createErrorStatus( Msgs.locationHaveValue );
        }

        if( !new File( lastLocation ).exists() )
        {
            return CoreUtil.createErrorStatus( Msgs.locationExists );
        }

        if( !SDKUtil.isValidSDKLocation( lastLocation ) )
        {
            return CoreUtil.createErrorStatus( Msgs.locationValidLiferayPluginSDK );
        }

        if( !SDKUtil.isSDKSupported( lastLocation ) )
        {
            return CoreUtil.createErrorStatus( NLS.bind( Msgs.SDKVersionGreaterEqual, ISDKConstants.LEAST_SUPPORTED_SDK_VERSION ) );
        }

        return Status.OK_STATUS;
    }

    private static class Msgs extends NLS
    {
        public static String addEclipseProjectFile;
        public static String addSDKAction;
        public static String browse;
        public static String configureLiferayPluginSDKLocation;
        public static String editSDKAction;
        public static String liferayPluginSDK;
        public static String locationExists;
        public static String locationHaveValue;
        public static String locationLabel;
        public static String locationValidLiferayPluginSDK;
        public static String nameHaveValue;
        public static String nameLabel;
        public static String newSDKAction;
        public static String openEclipse;
        public static String SDKVersionGreaterEqual;
        public static String selectFolderLabel;
        public static String selectFolderParamLabel;

        static
        {
            initializeMessages( AddSDKDialog.class.getName(), Msgs.class );
        }
    }
}
