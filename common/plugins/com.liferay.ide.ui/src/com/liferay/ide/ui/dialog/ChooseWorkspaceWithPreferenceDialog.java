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

package com.liferay.ide.ui.dialog;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.ide.ChooseWorkspaceData;
import org.eclipse.ui.internal.ide.ChooseWorkspaceDialog;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.ui.preferences.SettingsTransfer;

/**
 * @author Andy Wu
 */
@SuppressWarnings( "restriction" )
public class ChooseWorkspaceWithPreferenceDialog extends ChooseWorkspaceDialog
{

    private static final String WORKBENCH_SETTINGS = "WORKBENCH_SETTINGS"; //$NON-NLS-1$
    private static final String ENABLED_TRANSFERS = "ENABLED_TRANSFERS"; //$NON-NLS-1$

    /**
     * The class attribute for a settings transfer.
     */
    private static final String ATT_CLASS = "class"; //$NON-NLS-1$
    /**
     * The name attribute for the settings transfer.
     */
    private static final String ATT_NAME = "name"; //$NON-NLS-1$
    /**
     * The id attribute for the settings transfer.
     */
    private static final String ATT_ID = "id"; //$NON-NLS-1$
    private static final String ATT_HELP_CONTEXT = "helpContext"; //$NON-NLS-1$

    @SuppressWarnings( "rawtypes" )
    private Collection selectedSettings = new HashSet();

    public ChooseWorkspaceWithPreferenceDialog(
        Shell parentShell, ChooseWorkspaceData launchData, boolean suppressAskAgain, boolean centerOnMonitor )
    {
        super( parentShell, launchData, suppressAskAgain, centerOnMonitor );
    }

    @Override
    protected Control createDialogArea( Composite parent )
    {
        Control top = super.createDialogArea( parent );
        PlatformUI.getWorkbench().getHelpSystem().setHelp( parent, IIDEHelpContextIds.SWITCH_WORKSPACE_ACTION );
        createButtons( (Composite) top );
        applyDialogFont( parent );
        return top;
    }

    @SuppressWarnings( "unchecked" )
    private boolean createButtons( Composite parent )
    {

        IConfigurationElement[] settings = SettingsTransfer.getSettingsTransfers();

        String[] enabledSettings =
            getEnabledSettings( IDEWorkbenchPlugin.getDefault().getDialogSettings().getSection( WORKBENCH_SETTINGS ) );

        Composite panel = new Composite( parent, SWT.NONE );

        GridLayout layout = new GridLayout( 1, false );

        layout.marginWidth = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_MARGIN );
        layout.horizontalSpacing = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_SPACING );
        panel.setLayout( layout );
        panel.setFont( parent.getFont() );

        Group group = new Group( panel, SWT.NONE );

        group.setText( "Copy Settings" );
        group.setLayout( layout );
        group.setFont( parent.getFont() );

        for( int i = 0; i < settings.length; i++ )
        {
            final IConfigurationElement settingsTransfer = settings[i];

            final Button button = new Button( group, SWT.CHECK );

            button.setText( settings[i].getAttribute( ATT_NAME ) );

            String helpId = settings[i].getAttribute( ATT_HELP_CONTEXT );

            if( helpId != null )
                PlatformUI.getWorkbench().getHelpSystem().setHelp( button, helpId );

            if( enabledSettings != null && enabledSettings.length > 0 )
            {

                String id = settings[i].getAttribute( ATT_ID );
                for( int j = 0; j < enabledSettings.length; j++ )
                {
                    if( enabledSettings[j] != null && enabledSettings[j].equals( id ) )
                    {
                        button.setSelection( true );
                        selectedSettings.add( settingsTransfer );
                        break;
                    }
                }
            }

            button.setBackground( parent.getBackground() );
            button.addSelectionListener( new SelectionAdapter()
            {

                @Override
                public void widgetSelected( SelectionEvent e )
                {
                    if( button.getSelection() )
                        selectedSettings.add( settingsTransfer );
                    else
                        selectedSettings.remove( settingsTransfer );
                }
            } );

        }

        return enabledSettings != null && enabledSettings.length > 0;
    }

    /**
     * Get the settings for the receiver based on the entries in section.
     *
     * @param section
     * @return String[] or <code>null</code>
     */
    private String[] getEnabledSettings( IDialogSettings section )
    {

        if( section == null )
            return null;

        return section.getArray( ENABLED_TRANSFERS );

    }

    @SuppressWarnings( "rawtypes" )
    @Override
    protected void okPressed()
    {
        Iterator settingsIterator = selectedSettings.iterator();
        MultiStatus result = new MultiStatus(
            PlatformUI.PLUGIN_ID, IStatus.OK,
            IDEWorkbenchMessages.ChooseWorkspaceWithSettingsDialog_ProblemsTransferTitle, null );

        IPath path = new Path( getWorkspaceLocation() );
        String[] selectionIDs = new String[selectedSettings.size()];
        int index = 0;

        while( settingsIterator.hasNext() )
        {
            IConfigurationElement elem = (IConfigurationElement) settingsIterator.next();
            result.add( transferSettings( elem, path ) );
            selectionIDs[index++] = elem.getAttribute( ATT_ID );
        }
        if( result.getSeverity() != IStatus.OK )
        {
            ErrorDialog.openError(
                getShell(), IDEWorkbenchMessages.ChooseWorkspaceWithSettingsDialog_TransferFailedMessage,
                IDEWorkbenchMessages.ChooseWorkspaceWithSettingsDialog_SaveSettingsFailed, result );
            return;
        }

        saveSettings( selectionIDs );
        super.okPressed();
    }

    /**
     * Save the ids of the selected elements.
     *
     * @param selectionIDs
     */
    private void saveSettings( String[] selectionIDs )
    {
        IDialogSettings settings = IDEWorkbenchPlugin.getDefault().getDialogSettings().getSection( WORKBENCH_SETTINGS );

        if( settings == null )
            settings = IDEWorkbenchPlugin.getDefault().getDialogSettings().addNewSection( WORKBENCH_SETTINGS );

        settings.put( ENABLED_TRANSFERS, selectionIDs );

    }

    /**
     * Take the values from element and execute the class for path.
     *
     * @param elem
     * @param path
     * @return IStatus the result of the settings transfer.
     */
    private IStatus transferSettings( final IConfigurationElement element, final IPath path )
    {
        final IStatus[] exceptions = new IStatus[1];

        SafeRunner.run( new ISafeRunnable()
        {
            @Override
            public void run() throws Exception
            {
                try
                {
                    SettingsTransfer transfer =
                        (SettingsTransfer) WorkbenchPlugin.createExtension( element, ATT_CLASS );

                    patchWorkingSets(element,path);

                    transfer.transferSettings( path );
                }
                catch( CoreException exception )
                {
                    exceptions[0] = exception.getStatus();
                }
            }

            @Override
            public void handleException( Throwable exception )
            {
                exceptions[0] = StatusUtil.newStatus(
                    IStatus.ERROR,
                    NLS.bind(
                        IDEWorkbenchMessages.ChooseWorkspaceWithSettingsDialog_ClassCreationFailed,
                        element.getAttribute( ATT_CLASS ) ),
                    exception );

            }
        } );

        if( exceptions[0] != null )
            return exceptions[0];

        return Status.OK_STATUS;

    }

    @Override
    protected int getDialogBoundsStrategy()
    {
        return DIALOG_PERSISTLOCATION;
    }

    private IPath getNewWorkbenchStateLocation( IPath newWorkspaceRoot )
    {
        IPath currentWorkspaceRoot = Platform.getLocation();

        IPath dataLocation = WorkbenchPlugin.getDefault().getDataLocation();

        if( dataLocation == null )
            return null;
        int segmentsToRemove = dataLocation.matchingFirstSegments( currentWorkspaceRoot );

        // Strip it down to the extension
        dataLocation = dataLocation.removeFirstSegments( segmentsToRemove );
        // Now add in the
        dataLocation = newWorkspaceRoot.append( dataLocation );
        return dataLocation;
    }

    private void patchWorkingSets( final IConfigurationElement element, final IPath path )
    {
        String name = element.getAttribute( ATT_NAME );

        if( name.trim().equals( "Working Sets" ) )
        {
            IPath dataLocation = getNewWorkbenchStateLocation( path );

            if( dataLocation == null )
                return;

            File dir = new File( dataLocation.toOSString() );

            dir.mkdirs();
        }
    }

}
