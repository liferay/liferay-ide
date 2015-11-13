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
package com.liferay.ide.project.core.util;

import java.io.File;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author Lovett Li
 */
public class UpgradeAssistantSettingsUtil
{

    public static boolean JavaObjectToJSONFile( String path, Object obj ) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        if( new File(path).exists() )
        {
            mapper.writeValue( new File( path + "/liferayPortalSetup.json" ), obj );
        }
        else
        {
            return false;
        }
        return true;
    }

    public static <T> T JSONFileToJavaObject( String path, Class<T> T ) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        T t;
        if( new File(path).exists() )
        {
            t = mapper.readValue( new File( path + "/liferayPortalSetup.json" ), T );
        }
        else
        {
            return null;
        }
        return t;
    }

}
