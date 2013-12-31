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

import com.android.ide.eclipse.adt.AdtUtils;
import com.android.ide.eclipse.adt.internal.sdk.Sdk;
import com.android.sdklib.IAndroidTarget;

import org.eclipse.sapphire.services.DefaultValueService;
import org.eclipse.sapphire.services.DefaultValueServiceData;


/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class TargetSDKDefaultValueService extends DefaultValueService
{

    @Override
    protected DefaultValueServiceData compute()
    {
        String defaultData = "API 17: Android 4.2 (Jelly Bean)" ;

        try
        {
            Sdk currentSdk = Sdk.getCurrent();

            if( currentSdk != null )
            {
                IAndroidTarget[] targets = currentSdk.getTargets();

                // The default sdk target should be the one with the max api level.
                defaultData =  AdtUtils.getAndroidName( getMaxTargetApiLevel( targets ) );
            }
        }
        catch( Throwable th )
        {
        }

        return new DefaultValueServiceData( defaultData );
    }

    private int getMaxTargetApiLevel( IAndroidTarget[] targets )
    {
        int max = 1;

        for( IAndroidTarget target : targets )
        {
            if( target.getVersion().getApiLevel() > max )
            {
                max = target.getVersion().getApiLevel();
            }
        }

        return max;
    }

}
