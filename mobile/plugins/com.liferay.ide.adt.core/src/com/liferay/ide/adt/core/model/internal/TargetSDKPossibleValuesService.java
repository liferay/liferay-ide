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

import com.android.ide.eclipse.adt.AdtUtils;

import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class TargetSDKPossibleValuesService extends PossibleValuesService
{

    @Override
    protected void compute( Set<String> values )
    {
        try
        {
            for( String version : AdtUtils.getKnownVersions() )
            {
                values.add( version );
            }
        }
        catch( Throwable th )
        {
            values.add( "API 17: Android 4.2 (Jelly Bean)" );
        }
    }

    @Override
    public boolean ordered()
    {
        return true;
    }
}
