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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.ide.IDE;


/**
 * @author Gregory Amerson
 */
public class OpenAction extends SelectionProviderAction implements IAction
{

    protected OpenAction( ISelectionProvider provider )
    {
        super( provider, "Open" );
    }

    @Override
    public void run()
    {
        final Object selection = getStructuredSelection().getFirstElement();

        if( selection instanceof IFile )
        {
            final IFile file = (IFile) selection;

            try
            {
                IDE.openEditor( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file );
            }
            catch( PartInitException e )
            {
            }
        }
        else if( selection instanceof TaskProblem )
        {
            TaskProblem taskProblem = (TaskProblem) selection;

            MigrationUtil.openEditor( taskProblem );
        }
    }

    @Override
    public void selectionChanged( IStructuredSelection selection )
    {
        final Object element = getStructuredSelection().getFirstElement();

        setEnabled( element instanceof IFile || element instanceof TaskProblem );
    }
}
