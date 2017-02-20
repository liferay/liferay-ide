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

package com.liferay.ide.project.ui.pref;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.Version;

/**
 * @author Andy Wu
 */
public class BladeCLIPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
    private final ScopedPreferenceStore prefStore;

    public BladeCLIPreferencePage()
    {
        super( GRID );

        prefStore = new ScopedPreferenceStore( InstanceScope.INSTANCE, ProjectCore.PLUGIN_ID );
    }

    @Override
    protected void createFieldEditors()
    {
        Group group = SWTUtil.createGroup( getFieldEditorParent(), "", 1 );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        group.setLayoutData( gd );
        Composite composite = SWTUtil.createComposite( group, 2, 2, GridData.FILL_BOTH );

        addField( new StringFieldEditor( BladeCLI.BLADE_CLI_REPO_URL, "Blade CLI Repo URL:", composite ) );

        Label currentVersionLabel = new Label( composite, SWT.NONE );

        currentVersionLabel.setText( "Current Blade Version:" );

        Text currentBladeVersionText = new Text( composite, SWT.BORDER );

        currentBladeVersionText.setEditable( false );
        currentBladeVersionText.setText( " " + BladeCLI.getCurrentVersion() + " " );

        Label latestVersionLabel = new Label( composite, SWT.NONE );

        latestVersionLabel.setText( "Latest Blade Version:" );

        Text latestBladeVersionText = new Text( composite, SWT.BORDER );

        latestBladeVersionText.setEditable( false );
        latestBladeVersionText.setText( " checking...                   " );

        Button updateBladeButton = new Button( group, SWT.PUSH );

        updateBladeButton.setText( "Update Blade to Latest Version" );
        updateBladeButton.setEnabled( false );

        updateBladeButton.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent event )
            {
                try
                {
                    BladeCLI.updateBladeToLatest();

                    currentBladeVersionText.setText( " " + BladeCLI.getCurrentVersion() + " " );

                    MessageDialog.openInformation( getShell(), "Blade", "update successful" );
                }
                catch( BladeCLIException e )
                {
                    MessageDialog.openError( getShell(), "Blade", e.getMessage() );
                }
            }
        } );

        Job job = new WorkspaceJob( "checking latest blade version..." )
        {

            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
            {
                try
                {
                    String latestVersionStr = BladeCLI.fetchLatestVersion();

                    Version latestVersion = new Version( latestVersionStr );
                    Version currentVersion = new Version( BladeCLI.getCurrentVersion() );

                    final boolean needUpdate = latestVersion.compareTo( currentVersion ) > 0 ? true : false;

                    UIUtil.async( new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            latestBladeVersionText.setText( " " + latestVersionStr + " " );

                            updateBladeButton.setEnabled( needUpdate );
                        }
                    } );
                }
                catch( BladeCLIException e )
                {
                    return ProjectUI.createErrorStatus( "get blade version error", e );
                }

                return Status.OK_STATUS;
            }
        };

        job.schedule();

    }

    @Override
    public IPreferenceStore getPreferenceStore()
    {
        return prefStore;
    }

    @Override
    public void init( IWorkbench workbench )
    {
    }

}
