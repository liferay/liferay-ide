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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;


/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class AutoCorrectAction extends ProblemAction
{
    private ISelectionProvider _provider;

    public AutoCorrectAction( ISelectionProvider provider )
    {
        super( provider, "Correct automatically" );
        _provider = provider;
    }

    public IStatus runWithMarker( final Problem problem, IMarker marker)
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

                    new MarkDoneAction().run( problem, _provider );
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

        return Status.OK_STATUS;
    }

    @Override
    public void selectionChanged( IStructuredSelection selection )
    {
        if (selection.isEmpty())
        {
            setEnabled( false );
        }
        else
        {
            boolean selectionCompatible = true;

            Iterator<?> items = selection.iterator();

            while( items.hasNext() )
            {
                Object item = items.next();

                if( ! ( item instanceof Problem ) )
                {
                    selectionCompatible = false;
                    break;
                }

                Problem problem = (Problem) item;

                if( problem.autoCorrectContext == null )
                {
                    selectionCompatible = false;
                    break;
                }
            }

            setEnabled( selectionCompatible );
        }
    }

}
