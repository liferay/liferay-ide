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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Andy Wu
 */
public class ImportLiferayWorkspaceOpMethods
{

    public static final Status execute( final ImportLiferayWorkspaceOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Importing Liferay Workspace project...", 100 );

        Status retval = null;

        try
        {
            op.setProjectProvider( op.getBuildType().content() );

            final NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp> provider =
                op.getProjectProvider().content( true );

            String location = op.getWorkspaceLocation().content().toOSString();

            LiferayWorkspaceUtil.clearWorkspace( location );

            boolean isInitBundle = op.getProvisionLiferayBundle().content();
            boolean isHasBundlesDir = op.getHasBundlesDir().content();
            String bundleUrl = op.getBundleUrl().content( false );

            final IStatus importStatus;

            if( isInitBundle && !isHasBundlesDir )
            {
                importStatus = provider.importProject( location, monitor, true, bundleUrl );
            }
            else
            {
                importStatus = provider.importProject( location, monitor, false, null );
            }

            retval = StatusBridge.create( importStatus );

            if( !retval.ok() || retval.exception() != null )
            {
                return retval;
            }

            if( isInitBundle || isHasBundlesDir )
            {
                String serverRuntimeName = op.getServerName().content();
                IPath bundlesLocation = null;

                if( op.getBuildType().content().equals( "gradle-liferay-workspace" ) )
                {
                    bundlesLocation = LiferayWorkspaceUtil.getHomeLocation( location );
                }
                else
                {
                    bundlesLocation = new Path( location ).append( "bundles" );
                }

                if( bundlesLocation != null && bundlesLocation.toFile().exists() )
                {
                    ServerUtil.addPortalRuntimeAndServer( serverRuntimeName, bundlesLocation, monitor );
                }
            }
        }
        catch( Exception e )
        {
            final String msg = "import Liferay Workspace project error";

            ProjectCore.logError( msg, e );

            retval = Status.createErrorStatus( msg, e );
        }

        return retval;
    }
}
