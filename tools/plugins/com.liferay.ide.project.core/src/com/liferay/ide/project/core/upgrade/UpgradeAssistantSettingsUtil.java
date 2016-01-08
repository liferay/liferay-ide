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
package com.liferay.ide.project.core.upgrade;

import com.liferay.ide.project.core.ProjectCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.core.runtime.IPath;

/**
 * @author Lovett Li
 * @author Terry Jia
 */
public class UpgradeAssistantSettingsUtil
{
    private static final IPath storageLocation = ProjectCore.getDefault().getStateLocation();

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public static Object[] getAllObjectFromStore( Class clazz ) throws IOException
    {
        final ObjectMapper mapper = new ObjectMapper();

        final String pattern = clazz.getSimpleName() + ".*\\.json";

        final File[] files = storageLocation.toFile().listFiles( new FilenameFilter()
        {

            @Override
            public boolean accept( File dir, String name )
            {
                return( name.matches( pattern ) );
            }
        } );

        try
        {
            if( files != null && files.length > 0 )
            {
                Object[] o = new Object[files.length];

                for( int i = 0; i < files.length; i++ )
                {
                    o[i] = mapper.readValue( files[i], clazz );
                }

                return o;
            }
        }
        catch( FileNotFoundException e )
        {
        }

        return null;
    }

    public static <T> T getObjectFromStore( Class<T> clazz ) throws IOException
    {
        final ObjectMapper mapper = new ObjectMapper();

        try
        {
            return mapper.readValue( storageLocation.append( clazz.getSimpleName() + ".json" ).toFile(), clazz );
        }
        catch( FileNotFoundException e )
        {
        }

        return null;
    }

    public static <T> void setObjectToStore( Class<T> clazz, String suffix, T object ) throws IOException
    {
        final ObjectMapper mapper = new ObjectMapper();

        final File storageFile = storageLocation.append( clazz.getSimpleName() + "-" + suffix + ".json" ).toFile();

        mapper.writeValue( storageFile, object );
    }

    public static <T> void setObjectToStore( Class<T> clazz, T object ) throws IOException
    {
        final ObjectMapper mapper = new ObjectMapper();

        final File storageFile = storageLocation.append( clazz.getSimpleName() + ".json" ).toFile();

        mapper.writeValue( storageFile, object );
    }

}
