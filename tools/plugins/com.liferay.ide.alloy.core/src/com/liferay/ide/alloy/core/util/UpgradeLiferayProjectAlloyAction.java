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
package com.liferay.ide.alloy.core.util;

import com.liferay.ide.alloy.core.AlloyCore;
import com.liferay.ide.alloy.core.LautRunner;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.UpgradeLiferayProjectAction;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Simon Jiang
 */

public class UpgradeLiferayProjectAlloyAction extends UpgradeLiferayProjectAction
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
            submon.subTask( "Execute alloyUI upgrade tool" );

            final LautRunner lautRunner = AlloyCore.getLautRunner();

            if( lautRunner == null )
            {
                LiferayProjectCore.logError( "Alloy Core Not set LautRunner", null ); //$NON-NLS-1$
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
