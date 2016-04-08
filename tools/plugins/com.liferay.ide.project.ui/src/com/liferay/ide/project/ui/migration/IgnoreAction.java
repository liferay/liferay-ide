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

import com.liferay.blade.api.Problem;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionProvider;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class IgnoreAction extends ProblemAction
{

    public IgnoreAction()
    {
        this( new DummySelectionProvider() );
    }

    public IgnoreAction( ISelectionProvider provider )
    {
        super( provider, "Ignore" );
    }

    public void run( final Problem problem, final ISelectionProvider provider )
    {
        super.run( problem, provider );

        problem.setStatus( Problem.STATUS_IGNORE );

        MigrationUtil.updateMigrationProblemToStore( problem );
    }

    @Override
    protected IStatus runWithMarker( Problem problem, IMarker marker )
    {
        IStatus retval = Status.OK_STATUS;

        try
        {
            if ( marker.exists() )
            {
                marker.delete();
            }

            problem.setStatus( Problem.STATUS_IGNORE );
        }
        catch( CoreException e )
        {
            retval = e.getStatus();
        }

        refreshTableViewer();

        return retval;
    }
}
