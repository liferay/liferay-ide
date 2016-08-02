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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.dialog.JavaProjectSelectionDialog;

/**
 * @author Terry Jia
 */
public class OpenJavaProjectSelectionDialogAction extends Action
{

    private Shell shell;

    public OpenJavaProjectSelectionDialogAction( String text, Shell shell )
    {
        super( text );
        this.shell = shell;

        setImageDescriptor(
            ProjectUI.getDefault().getImageRegistry().getDescriptor( ProjectUI.MIGRATION_TASKS_IMAGE_ID ) );
    }

    protected ISelection getSelectionProjects()
    {
        final JavaProjectSelectionDialog dialog = new JavaProjectSelectionDialog( shell );

        if( dialog.open() == Window.OK )
        {
            final Object[] selectedProjects = dialog.getResult();

            if( selectedProjects != null )
            {
                List<IProject> projects = new ArrayList<>();

                for( Object project : selectedProjects )
                {
                    if( project instanceof IJavaProject )
                    {
                        IJavaProject p = (IJavaProject) project;
                        projects.add( p.getProject() );
                    }
                }

                return new StructuredSelection( projects.toArray( new IProject[0] ) );
            }
        }

        return null;
    }

}
