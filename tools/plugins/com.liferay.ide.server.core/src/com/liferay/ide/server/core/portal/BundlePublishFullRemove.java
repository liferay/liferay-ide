/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All riproghts reserved.
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
import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;


/**
 * @author Gregory Amerson
 */
public class BundlePublishFullRemove implements PublishOp
{
    private final IServer server;
    private final PortalRuntime portalRuntime;

    public BundlePublishFullRemove( IServer s )
    {
        this.server = s;
        this.portalRuntime = (PortalRuntime) this.server.getRuntime().loadAdapter( PortalRuntime.class, null );

        if( this.portalRuntime == null )
        {
            throw new IllegalArgumentException( "Could not get portal runtime from server " + s.getName() );
        }
    }

    public IStatus publish( IModule module, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = null;

        final IProject project = module.getProject();
        final ILiferayProject lrProject = LiferayCore.create( project );

        if( lrProject != null )
        {
            final ModulePublisher publisher = lrProject.adapt( ModulePublisher.class );

            if( publisher != null )
            {
                retval = publisher.remove( this.server, module );
            }
            else
            {
                retval = LiferayServerCore.error( "Could not get module publisher for project " + project.getName() );
            }
        }
        else
        {
            retval =
                LiferayServerCore.error( "Could not get liferay project to remove module " + module.getName() );
        }

        return retval;
    }

}
