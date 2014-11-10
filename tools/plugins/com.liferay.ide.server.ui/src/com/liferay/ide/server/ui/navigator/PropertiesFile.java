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
package com.liferay.ide.server.ui.navigator;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;


/**
 * @author Gregory Amerson
 */
public class PropertiesFile
{
    private final File file;

    public PropertiesFile( File f )
    {
        this.file = f;

        if( f == null )
        {
            throw new IllegalArgumentException( "file can not be null" );
        }
    }

    public IFileStore getFileStore()
    {
        return EFS.getLocalFileSystem().fromLocalFile( this.file );
    }

    public String getName()
    {
        return this.file.getName();
    }

    public String getPath()
    {
        return this.file.getPath();
    }

}
