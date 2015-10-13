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

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

import com.liferay.ide.project.core.util.ProjectUtil;

/**
 * A dialog for selecting a project to configure project specific settings for
 *
 * @author Andy Wu
 */
public class LiferayProjectSelectionDialog extends ProjectSelectionDialog
{
    /**
     * Constructor
     *
     * @param parentShell
     * @param projectsWithSpecifics
     */
    public LiferayProjectSelectionDialog( Shell parentShell, ViewerFilter filter )
    {
        super( parentShell , filter );

        setTitle( Msgs.projectSelection );
        setMessage( Msgs.selectProject );
    }

    private static class Msgs extends NLS
    {
        public static String projectSelection;
        public static String selectProject;

        static
        {
            initializeMessages( LiferayProjectSelectionDialog.class.getName(), Msgs.class );
        }
    }

    @Override
    protected boolean checkProject( IJavaProject project )
    {
        return ProjectUtil.isLiferayFacetedProject( project.getProject() );
    }

}
