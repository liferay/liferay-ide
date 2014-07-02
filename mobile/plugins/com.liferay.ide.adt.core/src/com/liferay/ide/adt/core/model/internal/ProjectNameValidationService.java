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
package com.liferay.ide.adt.core.model.internal;

import com.liferay.ide.adt.core.model.NewLiferayAndroidProjectOp;
import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;


/**
 * @author Gregory Amerson
 */
public class ProjectNameValidationService extends ValidationService
{

    private NewLiferayAndroidProjectOp op()
    {
        return context( NewLiferayAndroidProjectOp.class );
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final String currentProjectName = op().getProjectName().content();

        if( currentProjectName != null )
        {
            final IStatus nameStatus = CoreUtil.getWorkspace().validateName( currentProjectName, IResource.PROJECT );

            if( ! nameStatus.isOK() )
            {
                retval = StatusBridge.create( nameStatus );
            }
            else
            {
                //TODO check for overlapping existing project in workspace
//                IProject currentProject = CoreUtil.getWorkspaceRoot().getProject( currentProjectName );
//                Path currentLocation = op().getLocation().content( true );
//                final IStatus locationStatus= CoreUtil.getWorkspace().validateProjectLocation( currentProject, PathBridge.create( currentLocation ) );
//
//                if (!locationStatus.isOK())
//                {
//                    retval = StatusBridge.create( locationStatus );
//                }
            }
        }

        return retval;
    }

}
