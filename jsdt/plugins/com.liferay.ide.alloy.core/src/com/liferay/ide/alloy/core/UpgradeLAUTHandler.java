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
package com.liferay.ide.alloy.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.AbstractUpgradeProjectHandler;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Simon Jiang
 */
public class UpgradeLAUTHandler extends AbstractUpgradeProjectHandler
{
    @Override
    public Status execute( IProject project, String runtimeName, IProgressMonitor monitor, int perUnit )
    {
        Status retval = Status.createOkStatus();

        try
        {
            int worked = 0;

            final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 25 );
            submon.subTask( "Executing Liferay AUI Upgrade Tool" );

            final LautRunner lautRunner = AlloyCore.getLautRunner();

            if( lautRunner == null )
            {
                ProjectCore.logError( "Alloy Core Not set LautRunner", null ); //$NON-NLS-1$
            }
            else
            {
                worked = worked + perUnit;
                submon.worked( worked );

                lautRunner.exec( project, monitor );

                worked = worked + perUnit;
                submon.worked( worked );
            }

        }
        catch( Exception e )
        {
            final IStatus error =
                AlloyCore.createErrorStatus( "Unable to run LautTunner for " + project.getName(), e );
            AlloyCore.logError( e );
            retval = StatusBridge.create( error );
        }

        return retval;
    }
}
