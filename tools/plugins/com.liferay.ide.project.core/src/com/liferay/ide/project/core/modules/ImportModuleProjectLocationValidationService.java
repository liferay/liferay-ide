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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.ProjectImportUtil;

import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Andy Wu
 */
public class ImportModuleProjectLocationValidationService extends ValidationService
{

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        Path path = op().getLocation().content();

        if( path != null && !path.isEmpty() )
        {
            String location = path.toOSString();

            retval = StatusBridge.create( ProjectImportUtil.validatePath( location ) );

            if( !retval.ok() )
            {
                return retval;
            }

            retval = StatusBridge.create( ImportLiferayModuleProjectOpMethods.getBuildType( location ) );

            if( retval.severity() == Status.Severity.ERROR )
            {
                return retval;
            }

            String projectName = path.lastSegment();

            if( CoreUtil.getProject( projectName ).exists() )
            {
                retval = Status.createErrorStatus( "A project with that name already exists." );

                return retval;
            }
        }

        return retval;
    }

    private ImportLiferayModuleProjectOp op()
    {
        return context( ImportLiferayModuleProjectOp.class );
    }

}
