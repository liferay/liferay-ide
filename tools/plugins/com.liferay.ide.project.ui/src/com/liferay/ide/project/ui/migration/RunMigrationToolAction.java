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
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Andy Wu
 */
public class RunMigrationToolAction extends Action
{
    private Shell shell ;

    public RunMigrationToolAction( String text, Shell shell )
    {
        super( text );
        this.shell = shell;

        setImageDescriptor(
            ProjectUI.getDefault().getImageRegistry().getDescriptor( ProjectUI.MIGRATION_TASKS_IMAGE_ID ) );
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
                final ISelection selection = new StructuredSelection( javaProject.getProject() );

                try
                {
                    UIUtil.executeCommand( "com.liferay.ide.project.ui.migrateProject", selection );
                }
                catch( ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e )
                {
                    ProjectUI.createErrorStatus( "Error in migrate command", e );
                }
            }
        }
    }

}
