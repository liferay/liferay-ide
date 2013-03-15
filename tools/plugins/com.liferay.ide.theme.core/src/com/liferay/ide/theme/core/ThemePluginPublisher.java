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

package com.liferay.ide.theme.core;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.AbstractPluginPublisher;
import com.liferay.ide.server.core.ILiferayServerBehavior;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * @author Greg Amerson
 */
public class ThemePluginPublisher extends AbstractPluginPublisher
{

    public ThemePluginPublisher()
    {
        super();
    }

    public IStatus canPublishModule( IServer server, IModule module )
    {
        return Status.OK_STATUS;
    }

    public boolean prePublishModule(
        ServerBehaviourDelegate delegate, int kind, int deltaKind, IModule[] moduleTree, IModuleResourceDelta[] delta,
        IProgressMonitor monitor )
    {
        boolean publish = true;

        if( ( kind != IServer.PUBLISH_FULL && kind != IServer.PUBLISH_INCREMENTAL && kind != IServer.PUBLISH_AUTO ) ||
            moduleTree == null )
        {
            return publish;
        }

        if( deltaKind != ServerBehaviourDelegate.REMOVED )
        {
            try
            {
                addThemeModule( delegate, moduleTree[0] );
            }
            catch( Exception e )
            {
                ThemeCore.logError( "Unable to pre-publish module.", e ); //$NON-NLS-1$
            }
        }

        return publish;
    }

    protected void addThemeModule( ServerBehaviourDelegate delegate, IModule module ) throws CoreException
    {
        IProject project = module.getProject();

        // check to make sure they have a look-and-feel.xml file
        // IDE-110 IDE-648
        IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

        if( webappRoot != null )
        {
            for( IContainer container : webappRoot.getUnderlyingFolders() )
            {
                if( container != null && container.exists() )
                {
                    if( !( container.exists( new Path( "WEB-INF/" + ILiferayConstants.LIFERAY_LOOK_AND_FEEL_XML_FILE ) ) ) || //$NON-NLS-1$
                        !( container.exists( new Path( "css" ) ) ) ) //$NON-NLS-1$
                    {
                        ThemeCSSBuilder.cssBuild( project );
                        ( (ILiferayServerBehavior) delegate ).redeployModule( new IModule[] { module } );
                    }
                }
            }
        }
        else
        {
            ThemeCore.logError( "Could not add theme module: webappRoot not found" ); //$NON-NLS-1$
        }
    }
}
