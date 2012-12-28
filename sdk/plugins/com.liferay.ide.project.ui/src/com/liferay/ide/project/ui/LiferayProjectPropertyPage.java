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

package com.liferay.ide.project.ui;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.pref.SDKsPreferencePage;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Greg Amerson
 */
public class LiferayProjectPropertyPage extends PropertyPage
    implements IWorkbenchPropertyPage, IPluginProjectDataModelProperties
{

    private Combo runtimeCombo;

    public LiferayProjectPropertyPage()
    {
        super();

        setImageDescriptor( ProjectUIPlugin.imageDescriptorFromPlugin(
            ProjectUIPlugin.PLUGIN_ID, "/icons/e16/liferay.png" ) ); //$NON-NLS-1$
    }

    protected void configureSDKsLinkSelected( SelectionEvent e )
    {
        // boolean noSDKs = SDKManager.getAllSDKs().length == 0;

        String[] id = new String[] { SDKsPreferencePage.ID };

        PreferenceDialog dialog =
            PreferencesUtil.createPreferenceDialogOn( this.getShell(), SDKsPreferencePage.ID, id, null );

        int retval = dialog.open();

        if( retval == Window.OK )
        {
            getContainer().updateButtons();
        }
    }

    @Override
    protected Control createContents( Composite parent )
    {
        Composite top = SWTUtil.createTopComposite( parent, 2 );

        createInfoGroup( top );

        return top;
    }

    protected Group createDefaultGroup( Composite parent, String text, int columns )
    {
        GridLayout gl = new GridLayout( columns, false );

        Group group = new Group( parent, SWT.NONE );
        group.setText( text );
        group.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        group.setLayout( gl );

        return group;
    }

    @Override
    public boolean performOk()
    {
        final String selectedRuntimeName = this.runtimeCombo.getText();

        if( !CoreUtil.isNullOrEmpty( selectedRuntimeName ) )
        {
            final org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime =
                RuntimeManager.getRuntime( selectedRuntimeName );

            if( runtime != null )
            {
                final IFacetedProject fProject = ProjectUtil.getFacetedProject( getProject() );

                final org.eclipse.wst.common.project.facet.core.runtime.IRuntime primaryRuntime =
                    fProject.getPrimaryRuntime();

                if( !runtime.equals( primaryRuntime ) )
                {
                    Job job = new WorkspaceJob("Setting targeted runtime for project.") //$NON-NLS-1$
                    {
                        @Override
                        public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                        {
                            IStatus retval = Status.OK_STATUS;

                            try
                            {
                                fProject.setTargetedRuntimes( Collections.singleton( runtime ), monitor );
                                fProject.setPrimaryRuntime( runtime, monitor );
                            }
                            catch( Exception e )
                            {
                                retval = ProjectUIPlugin.createErrorStatus( "Could not set targeted runtime", e ); //$NON-NLS-1$
                            }

                            return retval;
                        }
                    };

                    job.schedule();
                }
            }
            else
            {
                return false;
            }
        }

        return true;
    }

    protected void createInfoGroup( Composite parent )
    {
        new Label( parent, SWT.LEFT ).setText( Msgs.liferayPluginTypeLabel );

        final Text pluginTypeLabel = new Text( parent, SWT.READ_ONLY | SWT.BORDER );
        pluginTypeLabel.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );

        final IProjectFacet liferayFacet = ProjectUtil.getLiferayFacet( getFacetedProject() );

        if( liferayFacet != null )
        {
            pluginTypeLabel.setText( liferayFacet.getLabel() );
        }

        new Label( parent, SWT.LEFT ).setText( Msgs.liferayRuntimeLabel );

        this.runtimeCombo = new Combo( parent, SWT.DROP_DOWN | SWT.READ_ONLY );
        this.runtimeCombo.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );

        String currentRuntimeName = null;

        try
        {
            ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( getProject() );

            currentRuntimeName = liferayRuntime.getRuntime().getName();
        }
        catch( Exception e )
        {
            ProjectUIPlugin.logError( "Could not determine liferay runtime", e ); //$NON-NLS-1$
        }

        final List<String> runtimeNames = new ArrayList<String>();
        int selectionIndex = -1;

        for( IRuntime runtime : ServerCore.getRuntimes() )
        {
            if( ServerUtil.isLiferayRuntime( runtime ) )
            {
                runtimeNames.add( runtime.getName() );

                if( runtime.getName().equals( currentRuntimeName ) )
                {
                    selectionIndex = runtimeNames.size() - 1;
                }
            }
        }

        if( runtimeNames.size() == 0 )
        {
            runtimeNames.add( "No Liferay runtimes available." ); //$NON-NLS-1$
        }

        this.runtimeCombo.setItems( runtimeNames.toArray( new String[0] ) );

        if( selectionIndex > -1 )
        {
            this.runtimeCombo.select( selectionIndex );
        }
    }

    protected IFacetedProject getFacetedProject()
    {
        IFacetedProject retval = null;

        IProject project = getProject();

        if( project != null )
        {
            retval = ProjectUtil.getFacetedProject( project );
        }

        return retval;
    }

    protected IProject getProject()
    {
        IAdaptable adaptable = getElement();

        IProject project = (IProject) adaptable.getAdapter( IProject.class );
        return project;
    }

    private static class Msgs extends NLS
    {
        public static String liferayPluginTypeLabel;
        public static String liferayRuntimeLabel;

        static
        {
            initializeMessages( LiferayProjectPropertyPage.class.getName(), Msgs.class );
        }
    }
}
