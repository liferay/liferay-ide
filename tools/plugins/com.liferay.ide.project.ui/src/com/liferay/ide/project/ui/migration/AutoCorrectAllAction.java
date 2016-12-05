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

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.liferay.blade.api.AutoMigrateException;
import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.Problem;
import com.liferay.ide.project.core.upgrade.FileProblems;
import com.liferay.ide.project.core.upgrade.UpgradeProblems;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView;
import com.liferay.ide.ui.util.UIUtil;

/**
 * @author Terry Jia
 */
public class AutoCorrectAllAction extends Action
{

    List<ProblemsContainer> _problemsContainerList;

    public AutoCorrectAllAction( List<ProblemsContainer> problemsContainerList )
    {
        _problemsContainerList = problemsContainerList;
    }

    public void run()
    {
        final BundleContext context = FrameworkUtil.getBundle( AutoCorrectAction.class ).getBundleContext();

        WorkspaceJob job = new WorkspaceJob( "Auto correcting all of migration problem." )
        {

            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor )
            {
                IStatus retval = Status.OK_STATUS;

                try
                {
                    if( _problemsContainerList != null )
                    {
                        for( ProblemsContainer problemsContainer : _problemsContainerList )
                        {
                            for( UpgradeProblems upgradeProblems : problemsContainer.getProblemsArray() )
                            {
                                FileProblems[] FileProblemsArray = upgradeProblems.getProblems();

                                for( FileProblems fileProblems : FileProblemsArray )
                                {
                                    List<Problem> problems = fileProblems.getProblems();
                                    for( Problem problem : problems )
                                    {
                                        if( problem.autoCorrectContext != null )
                                        {
                                            String autoCorrectKey = null;

                                            final int filterKeyIndex = problem.autoCorrectContext.indexOf( ":" );

                                            if( filterKeyIndex > -1 )
                                            {
                                                autoCorrectKey = problem.autoCorrectContext.substring( 0, filterKeyIndex );
                                            }
                                            else
                                            {
                                                autoCorrectKey = problem.autoCorrectContext;
                                            }

                                            final Collection<ServiceReference<AutoMigrator>> refs =
                                                context.getServiceReferences(
                                                    AutoMigrator.class, "(auto.correct=" + autoCorrectKey + ")" );

                                            final IResource file = MigrationUtil.getIResourceFromProblem( problem );

                                            for( ServiceReference<AutoMigrator> ref : refs )
                                            {
                                                final AutoMigrator autoMigrator = context.getService( ref );
                                                int problemsCorrected = autoMigrator.correctProblems( problem.file, problems );

                                                if( problemsCorrected > 0 && file != null )
                                                {
                                                    IMarker problemMarker = file.findMarker( problem.markerId );

                                                    if( problemMarker != null && problemMarker.exists() )
                                                    {
                                                        problemMarker.delete();
                                                    }
                                                }
                                            }

                                            file.refreshLocal( IResource.DEPTH_ONE, monitor );
                                        }
                                    }
                                }
                            }
                        }
                    }

                    IViewPart view = UIUtil.findView( UpgradeView.ID );
                    new RunMigrationToolAction( "Run Migration Tool", view.getViewSite().getShell() ).run();
                }
                catch( InvalidSyntaxException e )
                {
                }
                catch( AutoMigrateException | CoreException e )
                {
                    return retval = ProjectUI.createErrorStatus( "Unable to auto correct problem", e );
                }

                return retval;
            }
        };

        job.schedule();
    }

}
