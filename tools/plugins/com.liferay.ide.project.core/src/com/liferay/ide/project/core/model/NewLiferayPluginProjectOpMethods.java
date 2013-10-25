/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.LiferayProjectCore;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;


/**
 * @author Gregory Amerson
 */
public class NewLiferayPluginProjectOpMethods
{

    public static final Status execute( final NewLiferayPluginProjectOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay plugin project", 100 ); //$NON-NLS-1$

        Status retval = null;

        try
        {
            final ILiferayProjectProvider projectProvider = op.getProjectProvider().content( true );

            final IStatus status = projectProvider.createNewProject( op, monitor );

            retval = StatusBridge.create( status );
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay plugin project."; //$NON-NLS-1$
            LiferayProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " Please see Eclipse error log for more details.", e );
        }

        return retval;
    }

    public static void updateLocation( final NewLiferayPluginProjectOp op, final Path baseLocation )
    {
        final String projectName = op.getProjectName().content();

        String suffix = null;

        if( "ant".equals( op.getProjectProvider().content( true ).getShortName() ) ) //$NON-NLS-1$
        {
            suffix = getPluginTypeSuffix( op.getPluginType().content( true ) );

            if( suffix != null )
            {
                // check if project name already contains suffix
                if( projectName.endsWith( suffix ) )
                {
                    suffix = null;
                }
            }
        }

        final String dirName = projectName + ( suffix == null ? StringPool.EMPTY : suffix );
        final Path newLocation = baseLocation.append( dirName );

        op.setLocation( newLocation );
    }

    public static String getPluginTypeSuffix( final PluginType pluginType )
    {
        String suffix = null;

        switch ( pluginType )
        {
            case portlet:
                suffix = "-portlet"; //$NON-NLS-1$
                break;
            case ext:
                suffix = "-ext"; //$NON-NLS-1$
                break;
            case hook:
                suffix = "-hook"; //$NON-NLS-1$
                break;
            case layouttpl:
                suffix = "-layouttpl"; //$NON-NLS-1$
                break;
            case theme:
                suffix = "-theme"; //$NON-NLS-1$
                break;
        }

        return suffix;
    }

}
