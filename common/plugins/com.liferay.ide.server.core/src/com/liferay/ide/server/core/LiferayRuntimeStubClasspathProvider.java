/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.server.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jst.server.core.RuntimeClasspathProviderDelegate;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Gregory Amerson
 */
public class LiferayRuntimeStubClasspathProvider extends RuntimeClasspathProviderDelegate
{
    private static final NullProgressMonitor npm = new NullProgressMonitor();
    
    protected RuntimeClasspathProviderDelegate stubDelegate = null;

    public LiferayRuntimeStubClasspathProvider()
    {
        super();
    }

    @Override
    public IClasspathEntry[] resolveClasspathContainer( IProject project, IRuntime runtime )
    {
        IClasspathEntry[] retval = null;

        if( stubDelegate == null )
        {
            LiferayRuntimeStubDelegate delegate =
                (LiferayRuntimeStubDelegate) runtime.loadAdapter( LiferayRuntimeStubDelegate.class, npm );

            String runtimeStubTypeId = delegate.getRuntimeStubTypeId();

            IConfigurationElement[] elements =
                Platform.getExtensionRegistry().getConfigurationElementsFor(
                    "org.eclipse.jst.server.core.runtimeClasspathProviders" ); //$NON-NLS-1$

            for( IConfigurationElement element : elements )
            {
                String runtimeTypeIds = element.getAttribute( "runtimeTypeIds" ); //$NON-NLS-1$
                if( runtimeTypeIds.contains( runtimeStubTypeId ) )
                {
                    try
                    {
                        stubDelegate = (RuntimeClasspathProviderDelegate) element.createExecutableExtension( "class" ); //$NON-NLS-1$
                        break;
                    }
                    catch( CoreException e )
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        if( stubDelegate != null )
        {
            retval = stubDelegate.resolveClasspathContainer( project, runtime );
        }

        return retval;
    }
}
