/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.service.core.util;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.UpgradeProjectHandler;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.service.core.ServiceCore;
import com.liferay.ide.service.core.job.BuildServiceJob;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Simon Jiang
 */
public class UpgradeServiceBuildProcess extends UpgradeProjectHandler
{
    @Override
    public Status execute( Object... objects )
    {
        Status retval = Status.createOkStatus();
        try
        {
            final IProject project = ( IProject )objects[0];
            final IProgressMonitor monitor =  ( IProgressMonitor )objects[1];
            final int perUnit = ( ( Integer )objects[2] ).intValue();

            int worked = 0;

            final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 25 );
            submon.subTask( "Execute service rebuild" );

            List<IFile> files = new ArrayList<IFile>();
            files.addAll( new SearchFilesVisitor().searchFiles( project, ILiferayConstants.LIFERAY_SERVICE_BUILDER_XML_FILE ) );

            worked = worked + perUnit;
            submon.worked( worked );

            for( IFile servicesFile : files )
            {
                BuildServiceJob job = ServiceCore.createBuildServiceJob( servicesFile );
                job.schedule();
            }

            worked = worked + perUnit;
            submon.worked( worked );
        }
        catch( Exception e )
        {
            final IStatus error =
                ServiceCore.createErrorStatus( "Unable to run service build task for " + project.getName(), e );
            ServiceCore.logError( "Unable to run service build task for " + project.getName(), e );

            retval = StatusBridge.create( error );
        }
        return retval;
    }
}
