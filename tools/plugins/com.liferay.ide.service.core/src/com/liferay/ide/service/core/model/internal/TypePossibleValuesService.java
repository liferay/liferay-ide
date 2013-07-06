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

package com.liferay.ide.service.core.model.internal;

import com.liferay.ide.service.core.model.ServiceBuilder6xx;

import java.util.Set;

import org.eclipse.sapphire.Version;
import org.eclipse.sapphire.services.PossibleValuesService;

/**
 * @author Cindy Li
 */
public class TypePossibleValuesService extends PossibleValuesService
{
    private static final String[] DEFAULT_TYPES = { "String", "long", "boolean", "int", "double", "Date", "Collection" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

    @Override
    protected void fillPossibleValues( Set<String> values )
    {
        for( int i = 0; i < DEFAULT_TYPES.length; i++ )
        {
            values.add( DEFAULT_TYPES[i] );
        }

        ServiceBuilder6xx serviceBuilder = context( ServiceBuilder6xx.class );

        if( serviceBuilder.getVersion().content( true ).compareTo( new Version( "6.2" ) ) >= 0 ) //$NON-NLS-1$
        {
            values.add( "Blob" ); //$NON-NLS-1$
        }
    }

}
