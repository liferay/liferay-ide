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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.wst.server.core.IRuntime;
import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 */

public class LeastVersionRuntimeValidationService extends ValidationService
{

    @Override
    protected void initValidationService()
    {
        super.initValidationService();
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final Value<?> value = (Value<?>) context( Element.class ).property( context( Property.class ).definition() );

        final String runtimeName = value.content().toString();

        final IRuntime runtime = ServerUtil.getRuntime( runtimeName );

        if( runtime == null )
        {
            retval = Status.createErrorStatus( "Liferay runtime must be configured." ); //$NON-NLS-1$
        }
        else
        {
            ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime(runtime);
            Version runtimeVersion = new Version( liferayRuntime.getPortalVersion() );
            if (CoreUtil.compareVersions( runtimeVersion , ILiferayConstants.V620 ) < 0 )
            {
                retval = Status.createErrorStatus( "Liferay runtime must be great than 6.2.0." ); //$NON-NLS-1$
            }
            else
            {
                retval = StatusBridge.create( runtime.validate( new NullProgressMonitor() ) );
            }

        }

        return retval;
    }

}
