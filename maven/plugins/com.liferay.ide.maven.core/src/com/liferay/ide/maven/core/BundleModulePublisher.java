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

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.ModulePublisher;
import com.liferay.ide.server.core.portal.PortalRuntime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;


/**
 * @author Gregory Amerson
 */
public class BundleModulePublisher implements ModulePublisher
{

    private final IBundleProject bundleProject;

    public BundleModulePublisher( IBundleProject project )
    {
        if( project == null )
        {
            throw new IllegalArgumentException( "bundle project cannot be null" );
        }

        this.bundleProject = project;
    }

    public IStatus remove( IServer server, IModule module ) throws CoreException
    {
        IStatus retval = null;

        final String symbolicName = this.bundleProject.getSymbolicName();

        final PortalRuntime runtime = (PortalRuntime) server.getRuntime().loadAdapter( PortalRuntime.class, null );

        final IPath modulesPath = runtime.getPortalBundle().getModulesPath();

        final List<File> moduleFiles = new ArrayList<File>();

        // TODO this may not always match
        findFilesInPath( modulesPath.toFile(), symbolicName, moduleFiles );

        final IPath deployPath = runtime.getPortalBundle().getAutoDeployPath();
        findFilesInPath( deployPath.toFile(), symbolicName, moduleFiles );

        if( moduleFiles.size() > 0 )
        {
            // TODO convert to multi-statuses

            for( File moduleFile : moduleFiles )
            {
                if( moduleFile.delete() )
                {
                    retval = Status.OK_STATUS;
                }
                else
                {
                    retval = LiferayServerCore.error( "Could not delete module file " + moduleFile.getName() );
                }
            }
        }

        if( retval == null )
        {
            LiferayServerCore.logInfo( "No module to remove " + module.getName() );

            retval = Status.OK_STATUS;
        }

        return retval;
    }

    private void findFilesInPath( final File dir, final String pattern, List<File> retval  )
    {
        if( dir.exists() && dir.isDirectory() )
        {
            final File[] files = dir.listFiles();

            for( File f : files )
            {
                if( f.isDirectory() )
                {
                    findFilesInPath( f, pattern, retval );
                }
                else if( f.getName().contains( pattern ) )
                {
                    retval.add( f );
                }
            }
        }
    }

}
