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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class MigrateProjectHandler extends AbstractHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection( event );

        if( selection instanceof IStructuredSelection )
        {
            Object element = ( (IStructuredSelection) selection ).getFirstElement();

            IProject project = null;

            if( element instanceof IProject )
            {
                project = (IProject) element;
            }
            else if( element instanceof IAdaptable )
            {
                IAdaptable adaptable = (IAdaptable) element;

                project = (IProject) adaptable.getAdapter( IProject.class );
            }

            if( project != null )
            {
                final IPath location = project.getLocation();

                MigrationViewAction.migration( location );
            }
        }

        return null;
    }

}
