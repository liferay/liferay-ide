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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionProvider;


/**
 * @author Gregory Amerson
 */
public class MarkUndoneAction extends TaskProblemAction
{

    public MarkUndoneAction()
    {
        this( new DummySelectionProvider() );
    }

    public MarkUndoneAction( ISelectionProvider provider )
    {
        super( provider, "Mark undone" );
    }

    @Override
    protected IStatus runWithMarker( TaskProblem taskProblem, IMarker marker )
    {
        IStatus retval = Status.OK_STATUS;

        try
        {
            if( marker.exists() )
            {
                marker.setAttribute( IMarker.SEVERITY, IMarker.SEVERITY_ERROR );
                marker.setAttribute( "migrationProblem.resolved", false );
            }

            taskProblem.setResolved( false );
        }
        catch( CoreException e )
        {
            retval = e.getStatus();
        }

        return retval;
    }

}
