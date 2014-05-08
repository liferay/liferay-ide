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
package com.liferay.ide.project.core.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.UpgradeLiferayProjectAction;

import java.util.Collections;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;

/**
 * @author Simon Jiang
 */

public class UpgradeLiferayProjectRuntimeAction extends UpgradeLiferayProjectAction
{

    @Override
    public Status execute( Object... objects )
    {
        Status retval = Status.createOkStatus();

        try
        {

            final IProject project = ( IProject )objects[0];
            final String runtimeName = ( String )objects[1];
            final IProgressMonitor monitor =  ( IProgressMonitor )objects[2];
            final int perUnit = ( ( Integer )objects[3] ).intValue();

            int worked = 0;
            final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 25 );
            submon.subTask( "Update project runtime" );

            final org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime =
                RuntimeManager.getRuntime( runtimeName );

            if( runtime != null )
            {
                worked = worked + perUnit;
                submon.worked( worked );

                if( runtime != null )
                {
                    final IFacetedProject fProject = ProjectUtil.getFacetedProject( project );

                    final org.eclipse.wst.common.project.facet.core.runtime.IRuntime primaryRuntime =
                        fProject.getPrimaryRuntime();

                    if( !runtime.equals( primaryRuntime ) )
                    {

                        worked = worked + perUnit;
                        submon.worked( worked );

                        fProject.setTargetedRuntimes( Collections.singleton( runtime ), monitor );

                        worked = worked + perUnit;
                        submon.worked( worked );

                        fProject.setPrimaryRuntime( runtime, monitor );
                        worked = worked + perUnit;
                        submon.worked( worked );
                    }

                }

            }

        }
        catch( Exception e )
        {
            final IStatus error =
                LiferayProjectCore.createErrorStatus( "Unable to upgrade target runtime for " + project.getName(), e );
            LiferayProjectCore.logError( error );

            retval = StatusBridge.create( error );
        }

        return retval;
    }


}
