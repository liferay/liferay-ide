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
package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaProjectSourceContainer;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaSourcePathComputer;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;


/**
 * @author Gregory Amerson
 */
public class PortalSourcePathComputerDelegate extends JavaSourcePathComputer
{
    public static final String ID = "";

    @Override
    public String getId()
    {
        return ID;
    }

    @Override
    public ISourceContainer[] computeSourceContainers(
        ILaunchConfiguration configuration, IProgressMonitor monitor ) throws CoreException
    {
        final List<ISourceContainer> containers = new ArrayList<ISourceContainer>();

        // lets use tomcat's source path computer if available
        final ISourcePathComputer webprojectComputer =
            DebugPlugin.getDefault().getLaunchManager().getSourcePathComputer(
                "com.liferay.ide.server.tomcat.portalSourcePathComputer" );

        if( webprojectComputer != null )
        {
            final ISourceContainer[] webcontainers =
                webprojectComputer.computeSourceContainers( configuration, monitor );

            if( !CoreUtil.isNullOrEmpty( webcontainers ) )
            {
                Collections.addAll( containers, webcontainers );
            }
        }
        else
        {
            final ISourceContainer[] defaultContainers = super.computeSourceContainers( configuration, monitor );

            if( !CoreUtil.isNullOrEmpty( defaultContainers ) )
            {
                Collections.addAll( containers, defaultContainers );
            }
        }

        collectPortalContainers( containers, configuration, monitor );

        return containers.toArray( new ISourceContainer[0] );
    }

    private void collectPortalContainers( List<ISourceContainer> collect,
        ILaunchConfiguration configuration, IProgressMonitor monitor ) throws CoreException
    {
        final IServer server = ServerUtil.getServer( configuration );

        final IModule[] modules = server.getModules();

        for( int i = 0; i < modules.length; i++ )
        {
            final IProject project = modules[i].getProject();

            ILiferayProject lrproject = LiferayCore.create( project );

            if( lrproject != null && lrproject.getSourceFolders() != null )
            {
                collect.add( new JavaProjectSourceContainer( JavaCore.create( project ) ) );
            }
        }
    }

}
