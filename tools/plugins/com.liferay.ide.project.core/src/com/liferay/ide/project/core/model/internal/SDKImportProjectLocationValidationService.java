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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.model.SDKProjectImportOp;
import com.liferay.ide.project.core.util.SDKProjectUtil;

import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class SDKImportProjectLocationValidationService extends ValidationService
{
    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final Path currentProjectLocation = op().getLocation().content( true );

        if( currentProjectLocation != null && !currentProjectLocation.isEmpty())
        {
            final String currentPath = currentProjectLocation.toOSString();

            retval = StatusBridge.create( SDKProjectUtil.validateProjectPath( currentPath ) );
        }
//        else
//        {
//            retval = Status.createErrorStatus( "Location must be specified." ); //$NON-NLS-1$
//        }

        return retval;
    }

    private SDKProjectImportOp op()
    {
        return context( SDKProjectImportOp.class );
    }
}