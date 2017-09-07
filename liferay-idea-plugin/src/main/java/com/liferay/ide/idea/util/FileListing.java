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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.idea.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Greg Amerson
 * @author Terry Jia
 */
public class FileListing
{

    static public List<File> getFileListing( File aStartingDir ) throws FileNotFoundException
    {
        List<File> result = new ArrayList<File>();

        File[] filesAndDirs = aStartingDir.listFiles();

        List<File> filesDirs = Arrays.asList( filesAndDirs );

        for( File file : filesDirs )
        {
            result.add( file ); // always add, even if directory

            if( !file.isFile() )
            {
                // must be a directory
                // recursive call!
                List<File> deeperList = getFileListing( file );

                result.addAll( deeperList );
            }
        }

        return result;
    }

    /**
     * Recursively walk a directory tree and return a List of all Files found; the List is sorted using
     * File.compareTo().
     * 
     * @param aStartingDir
     *            is a valid directory, which can be read.
     */
    static public List<File> getFileListing( File aStartingDir, boolean sort ) throws FileNotFoundException
    {
        validateDirectory( aStartingDir );

        List<File> result = getFileListing( aStartingDir );

        if( sort )
        {
            Collections.sort( result );
        }

        return result;
    }

    /**
     * Directory is valid if it exists, does not represent a file, and can be read.
     */
    static private void validateDirectory( File aDirectory ) throws FileNotFoundException
    {
        if( aDirectory == null )
        {
            throw new IllegalArgumentException( "Directory should not be null." ); //$NON-NLS-1$
        }

        if( !aDirectory.exists() )
        {
            throw new FileNotFoundException( "Directory does not exist: " + aDirectory ); //$NON-NLS-1$
        }

        if( !aDirectory.isDirectory() )
        {
            throw new IllegalArgumentException( "Is not a directory: " + aDirectory ); //$NON-NLS-1$
        }

        if( !aDirectory.canRead() )
        {
            throw new IllegalArgumentException( "Directory cannot be read: " + aDirectory ); //$NON-NLS-1$
        }
    }

}
