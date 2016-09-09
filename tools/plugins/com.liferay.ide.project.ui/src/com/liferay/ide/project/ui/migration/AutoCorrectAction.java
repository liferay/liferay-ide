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
package com.liferay.ide.project.ui.migration;

import com.liferay.blade.api.AutoMigrateException;
import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.Problem;

import com.liferay.ide.project.ui.ProjectUI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;


/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class AutoCorrectAction extends SelectionProviderAction implements IAction
{

    public AutoCorrectAction( ISelectionProvider provider )
    {
        super( provider, "Correct automatically" );
    }

    public static void run( final Problem problem, final ISelectionProvider provider )
    {
        final IResource file = MigrationUtil.getIResourceFromProblem( problem );
        final BundleContext context = FrameworkUtil.getBundle( AutoCorrectAction.class ).getBundleContext();

        new WorkspaceJob( "Auto correcting migration problem.")
        {
            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor )
            {
                try
                {
                    final String autoCorrectKey =
                        problem.autoCorrectContext.substring( 0, problem.autoCorrectContext.indexOf( ":" ) );

                    final Collection<ServiceReference<AutoMigrator>> refs =
                        context.getServiceReferences( AutoMigrator.class, "(auto.correct=" + autoCorrectKey + ")" );

                    for( ServiceReference<AutoMigrator> ref : refs )
                    {
                        final List<Problem> problems = new ArrayList<>();
                        problems.add( problem );

                        final AutoMigrator autoMigrator = context.getService( ref );
                        autoMigrator.correctProblems( problem.file, problems );
                    }

                    file.refreshLocal( IResource.DEPTH_ONE, monitor );

                    new MarkDoneAction().run( problem, provider );
                }
                catch( InvalidSyntaxException e )
                {
                }
                catch( AutoMigrateException | CoreException e )
                {
                    return ProjectUI.createErrorStatus( "Unable to auto correct problem", e );
                }

                return Status.OK_STATUS;
            }
        }.schedule();
    }

    @Override
    public void run()
    {
        final Problem problem = MigrationUtil.getProblemFromSelection( getSelection() );

        run( problem, getSelectionProvider() );
    }

    @Override
    public void selectionChanged( IStructuredSelection selection )
    {
        Object element = selection.getFirstElement();

        if( element instanceof Problem && ( (Problem) element ).getAutoCorrectContext() != null )
        {
            //Temporarily code avoid auto correct action
            //Should update blade
            if( !( (Problem) element ).getTicket().equals( "LPS-61952" ) )
            {
                setEnabled( true );
                return;
            }
        }

        setEnabled( false );
    }

}
