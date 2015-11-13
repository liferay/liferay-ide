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

package com.liferay.ide.project.ui.dialog;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Andy Wu
 */
public class CustomProjectSelectionDialog extends JavaProjectSelectionDialog
{

    private List<IProject> projects;

    public CustomProjectSelectionDialog( Shell shell )
    {
        super( shell );
    }

    @Override
    protected boolean checkProject( IJavaProject javaProject )
    {
        if( projects == null )
        {
            return false;
        }

        IProject project = javaProject.getProject();

        if( projects.contains( project ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setProjects( List<IProject> projects )
    {
        this.projects = projects;
    }
}
