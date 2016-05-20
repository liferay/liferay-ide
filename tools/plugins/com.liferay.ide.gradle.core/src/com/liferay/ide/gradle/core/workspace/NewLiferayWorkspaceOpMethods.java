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

package com.liferay.ide.gradle.core.workspace;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.LiferayWorkspaceProjectProvider;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Andy Wu
 */
public class NewLiferayWorkspaceOpMethods
{

    public static final Status execute( final NewLiferayWorkspaceOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay Workspace project...", 100 ); //$NON-NLS-1$

        Status retval = null;

        try
        {
            final Path projectLocation = op.getLocation().content();

            Path workspaceLocation = PathBridge.create( CoreUtil.getWorkspaceRoot().getLocation() );

            if( op.getUseDefaultLocation().content() )
            {
                updateLocation( op, workspaceLocation );
            }

            LiferayWorkspaceProjectProvider provider =
                (LiferayWorkspaceProjectProvider) LiferayCore.getProvider( "liferay-workspace" );

            IStatus createStatus = provider.createNewProject( op, monitor );

            retval = StatusBridge.create( createStatus );

            if( !retval.ok() )
            {
                return retval;
            }

            String location = projectLocation.toOSString();

            boolean isInitBundle = op.getProvisionLiferayBundle().content();

            IStatus importStatus = null;

            if( isInitBundle )
            {
                importStatus = provider.importProject( location, monitor, "initBundle" );
            }
            else
            {
                importStatus = provider.importProject( location, monitor, null );
            }

            retval = StatusBridge.create( importStatus );

            if( !retval.ok() )
            {
                return retval;
            }

            if( isInitBundle )
            {
                String serverRuntimeName = op.getServerName().content();

                ImportLiferayWorkspaceOpMethods.addPortalRuntimeAndServer( serverRuntimeName, location, monitor );
            }
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay Workspace project."; //$NON-NLS-1$

            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg , e );
        }

        return retval;
    }

    public static void updateLocation( final NewLiferayWorkspaceOp op, final Path baseLocation )
    {
        final String projectName = op.getWorkspaceName().content();

        if( baseLocation == null || projectName == null)
        {
            return;
        }

        final Path newLocation = baseLocation.append( projectName );

        op.setLocation( newLocation );
    }

}
