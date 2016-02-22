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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.AbstractLiferayProjectImporter;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Andy Wu
 */
public class GradleModuleProjectImporter extends AbstractLiferayProjectImporter
{

    @Override
    public IStatus canImport( String location )
    {
        IStatus retval = null;

        File file = new File( location );

        if( file.exists() )
        {
            if( findGradleFile( file ) )
            {
                retval = Status.OK_STATUS;
            }

            File parent = file.getParentFile();

            while( parent != null )
            {
                if( findGradleFile( parent ) )
                {
                    retval = new Status(
                        IStatus.WARNING, GradleCore.PLUGIN_ID,
                        "Location is not the root location of a multi-module project." );
                    break;
                }

                parent = parent.getParentFile();
            }
        }

        return retval;

    }

    private boolean findGradleFile( File file )
    {
        boolean retval = false;
        File[] childFiles = file.listFiles();

        for( File child : childFiles )
        {
            if( !child.isDirectory() && child.getName().endsWith( ".gradle" ) )
            {
                retval = true;
            }
        }

        return retval;
    }

    @Override
    public void importProject( String location, IProgressMonitor monitor ) throws CoreException
    {
        GradleUtil.importGradleProject( new File( location ), monitor );
    }

}
