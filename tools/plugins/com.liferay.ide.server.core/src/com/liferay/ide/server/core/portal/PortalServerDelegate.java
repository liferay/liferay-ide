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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.model.ServerDelegate;


/**
 * @author Gregory Amerson
 */
public class PortalServerDelegate extends ServerDelegate implements PortalServer
{

    public PortalServerDelegate()
    {
        super();
    }

    public String[] getMemoryArgs()
    {
        String[] retval = null;

        final String args = getAttribute( PROPERTY_MEMORY_ARGS, PortalServerConstants.DEFAULT_MEMORY_ARGS );

        if( !CoreUtil.isNullOrEmpty( args ) )
        {
            retval = args.split( "\n" );
        }

        return retval;
    }

    @Override
    public IStatus canModifyModules( IModule[] add, IModule[] remove )
    {
        IStatus retval = Status.OK_STATUS;

        if( ! CoreUtil.isNullOrEmpty( add ) )
        {
            for( IModule module : add )
            {
                if( ! "liferay.bundle".equals( module.getModuleType().getId() ) )
                {
                    retval =
                        LiferayServerCore.error( "Unable to add module with type " +
                            module.getModuleType().getName() );
                    break;
                }
            }
        }

        return retval;
    }

    @Override
    public IModule[] getChildModules( IModule[] module )
    {
        IModule[] retval = null;

        if( module != null )
        {
            final IModuleType moduleType = module[0].getModuleType();

            if( module.length == 1 && moduleType != null && "liferay.bundle".equals( moduleType.getId() ) )
            {
                retval = new IModule[0];
            }
        }

        return retval;
    }

    @Override
    public IModule[] getRootModules( IModule module ) throws CoreException
    {
        final IStatus status = canModifyModules(new IModule[] { module }, null);

        if (status == null || !status.isOK())
        {
            throw new CoreException(status);
        }

        return new IModule[] { module };
    }

    @Override
    public void modifyModules( IModule[] add, IModule[] remove, IProgressMonitor monitor ) throws CoreException
    {
        System.out.println();
    }

}
