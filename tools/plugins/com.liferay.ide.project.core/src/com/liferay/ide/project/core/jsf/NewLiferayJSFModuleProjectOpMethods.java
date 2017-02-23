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
package com.liferay.ide.project.core.jsf;

import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Simon Jiang
 */
public class NewLiferayJSFModuleProjectOpMethods
{
    
    public static final Status execute( final NewLiferayJSFModuleProjectOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay module project (this process may take several minutes)", 100 ); //$NON-NLS-1$

        Status retval = null;

        try
        {
            final NewLiferayProjectProvider<NewLiferayJSFModuleProjectOp> projectProvider = op.getJsfProjectProvider().content( true );

            final IStatus status = projectProvider.createNewProject( op, monitor );

            retval = StatusBridge.create( status );

            if( retval.ok() )
            {
                updateBuildPrefs( op );
            }
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay module project."; //$NON-NLS-1$
            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " " + e.getMessage(), e );
        }

        return retval;
    }

    private static void updateBuildPrefs( final NewLiferayJSFModuleProjectOp op )
    {
        try
        {
            final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

            prefs.put( ProjectCore.PREF_DEFAULT_JSF_MODULE_PROJECT_BUILD_TYPE_OPTION, op.getJsfProjectProvider().text() );

            prefs.flush();
        }
        catch( Exception e )
        {
            final String msg = "Error updating default project build type."; //$NON-NLS-1$
            ProjectCore.logError( msg, e );
        }
    }
}
