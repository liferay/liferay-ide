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
package com.liferay.ide.project.core.model;

import com.liferay.ide.project.core.util.ProjectImportUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;


/**
 * @author Simon Jiang
 */
public class SDKImportProjectOpMethods
{
    public static final Status execute( final SDKProjectImportOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Importing Liferay plugin project...", 100 );

        Status retval = Status.createOkStatus();

        try
        {
            final Path projectLocation = op.getLocation().content();

            if( projectLocation == null || projectLocation.isEmpty() )
            {
                throw new CoreException(
                    StatusBridge.create( Status.createErrorStatus( "Project can not be empty" ) ) );
            }

            final IProject project = ProjectImportUtil.importProject(
                PathBridge.create( projectLocation ), new NullProgressMonitor(), null );

            op.setFinalProjectName( project.getName() );
        }
        catch( CoreException e )
        {
            return StatusBridge.create( e.getStatus() );
        }

        return retval;
    }
}
