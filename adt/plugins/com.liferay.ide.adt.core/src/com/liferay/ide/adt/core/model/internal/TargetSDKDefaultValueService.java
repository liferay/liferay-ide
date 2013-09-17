/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.android.ide.eclipse.adt.internal.sdk.Sdk;
import com.android.sdklib.IAndroidTarget;
import com.liferay.ide.adt.core.ADTUtil;
import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.sapphire.services.DefaultValueService;
import org.eclipse.sapphire.services.DefaultValueServiceData;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class TargetSDKDefaultValueService extends DefaultValueService
{

    @Override
    protected DefaultValueServiceData compute()
    {
        Sdk currentSdk = Sdk.getCurrent();

        if( currentSdk != null )
        {
            IAndroidTarget[] targets = currentSdk.getTargets();

            if( ! CoreUtil.isNullOrEmpty( targets ) )
            {
                return new DefaultValueServiceData( ADTUtil.getTargetLabel( targets[0] ) );
            }
        }

        return new DefaultValueServiceData( "" );
    }

}
