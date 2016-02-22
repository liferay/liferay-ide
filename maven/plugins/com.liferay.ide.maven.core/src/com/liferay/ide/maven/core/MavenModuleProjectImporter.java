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

package com.liferay.ide.maven.core;

import com.liferay.ide.core.AbstractLiferayProjectImporter;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Andy Wu
 */
public class MavenModuleProjectImporter extends AbstractLiferayProjectImporter
{

    @Override
    public IStatus canImport( String location )
    {
        IStatus retval = null;

        File pom = new File( location, "pom.xml" );

        if( pom.exists() )
        {
            retval = Status.OK_STATUS;
        }

        return retval;
    }

    @Override
    public void importProject( String location, IProgressMonitor monitor ) throws CoreException
    {
        try
        {
            MavenUtil.importProject( location, monitor );
        }
        catch( InterruptedException e )
        {
        }
    }

}
