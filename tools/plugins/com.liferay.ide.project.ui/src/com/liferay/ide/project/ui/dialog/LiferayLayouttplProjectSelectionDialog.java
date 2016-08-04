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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.widgets.Shell;

import com.liferay.ide.core.util.CoreUtil;

/**
 * @author Joye Luo
 */
public class LiferayLayouttplProjectSelectionDialog extends JavaProjectSelectionDialog
{

    public LiferayLayouttplProjectSelectionDialog( Shell shell )
    {
        super( shell );
    }

    @Override
    protected boolean checkProject( IJavaProject javaProject )
    {
        List<IProject> liferayLayouttplProjects = new ArrayList<IProject>();

        IProject[] projects = CoreUtil.getAllProjects();

        for( IProject project : projects )
        {
            IFile layouttplFile = project.getFile( "/docroot/WEB-INF/liferay-layout-templates.xml" );

            if( layouttplFile.exists() )
            {
                liferayLayouttplProjects.add( project );
            }
        }

        IProject project = javaProject.getProject();

        if( liferayLayouttplProjects.contains( project ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
