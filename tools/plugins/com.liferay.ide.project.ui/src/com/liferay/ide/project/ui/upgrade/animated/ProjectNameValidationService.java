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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Andy Wu
 * @author Simon Jiang
 */
public class ProjectNameValidationService extends ValidationService
{

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        String projectName = op().getProjectName().content();

        if( projectName == null )
        {
            return StatusBridge.create(
                ProjectCore.createErrorStatus( "This new upgrade sdk name should not be null." ) );
        }

        final IProject[] projects = CoreUtil.getAllProjects();

        for( IProject projetct : projects )
        {
            if( projetct.getName().equals( projectName ) )
            {
                return StatusBridge.create( ProjectCore.createErrorStatus( "This sdk project was already existed" ) );
            }
        }

        if( op().getSdkLocation() != null && op().getSdkLocation().content() != null )
        {
            IPath osPath = PathBridge.create( op().getSdkLocation().content() );

            if( osPath.segmentCount() > 1 )
            {
                IPath sdkParentPath = osPath.removeLastSegments( 1 );
                IPath newSdkPath = sdkParentPath.append( projectName );

                if( newSdkPath.toFile().exists() )
                {
                    return StatusBridge.create(
                        ProjectCore.createErrorStatus( "This sdk location was already existed in disk." ) );
                }
            }
        }

        return retval;
    }

    private LiferayUpgradeDataModel op()
    {
        return context( LiferayUpgradeDataModel.class );
    }
}
