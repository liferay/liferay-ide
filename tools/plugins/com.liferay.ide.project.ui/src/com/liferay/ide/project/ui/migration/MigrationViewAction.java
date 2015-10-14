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

import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.dialog.JavaProjectSelectionDialog;

import java.util.HashMap;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

/**
 * @author Andy Wu
 */
public class MigrationViewAction extends Action
{
    private Shell shell ;

    public MigrationViewAction( String text, Shell shell )
    {
        super( text );
        setImageDescriptor(
            ImageDescriptor.createFromURL( ProjectUI.getDefault().getBundle().getEntry( "/icons/e16/liferay.png" ) ) );
        this.shell = shell;
    }

    @Override
    public void run()
    {
        final JavaProjectSelectionDialog dialog = new JavaProjectSelectionDialog( shell );

        if( dialog.open() == Window.OK )
        {
            final Object[] selectedProjects = dialog.getResult();

            if( selectedProjects != null )
            {
                final IJavaProject javaProject = (IJavaProject) selectedProjects[0];
                final ICommandService commandService = PlatformUI.getWorkbench().getService( ICommandService.class );

                try
                {
                    final IEvaluationContext evaluationContext = new EvaluationContext( null, null );
                    final ISelection selection = new StructuredSelection( javaProject.getProject() );

                    evaluationContext.addVariable( ISources.ACTIVE_CURRENT_SELECTION_NAME, selection );

                    final ExecutionEvent executionEvent =
                        new ExecutionEvent( null, new HashMap<String, String>(), null, evaluationContext );

                    commandService.getCommand( "com.liferay.ide.project.ui.migrateProject" ).executeWithChecks(
                        executionEvent );
                }
                catch( ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e )
                {
                    ProjectUI.createErrorStatus( "Error in migrate command", e );
                }
            }
        }
    }

}
