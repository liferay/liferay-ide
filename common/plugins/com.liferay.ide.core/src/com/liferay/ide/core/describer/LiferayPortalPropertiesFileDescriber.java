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

package com.liferay.ide.core.describer;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;

/**
 * @author Gregory Amerson
 */
public class LiferayPortalPropertiesFileDescriber extends LiferayPropertiesFileDescriber
{
    private final Set<String> fileNames = new HashSet<String>();
    {
        fileNames.add( "portal.properties" );
        fileNames.add( "portal-ext.properties" );
        fileNames.add( "portal-setup-wizard.properties" );
        fileNames.add( "system-ext.properties" );
    }

    public LiferayPortalPropertiesFileDescriber()
    {
        super();
    }

    @Override
    protected boolean isPropertiesFile( Object file )
    {
        String fileName = null;

        if( file instanceof File )
        {
            fileName = ( (File) file).getName();
        }
        else if( file instanceof IFile )
        {
            fileName = ( (IFile) file).getName();
        }
        else if( file instanceof String )
        {
            fileName = new File( (String) file ).getName();
        }

        return fileNames.contains( fileName );
    }
}
