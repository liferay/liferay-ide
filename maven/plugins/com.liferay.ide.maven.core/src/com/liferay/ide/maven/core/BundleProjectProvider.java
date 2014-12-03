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

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.ILiferayProjectProvider;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;


/**
 * @author Gregory Amerson
 */
public class BundleProjectProvider extends AbstractLiferayProjectProvider implements ILiferayProjectProvider
{

    public BundleProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    public ILiferayProject provide( Object type )
    {
        if( type instanceof IProject )
        {
            final IProject project = (IProject) type;

            try
            {
                if( MavenUtil.isMavenProject( project ) && project.getName().startsWith( "sample.bundle" ) )
                {
                    return new BundleProject( project );
                }
            }
            catch( CoreException e )
            {
                LiferayMavenCore.logError(
                    "Unable to create ILiferayProject from maven bundle project " + project.getName(), e ); //$NON-NLS-1$
            }
        }

        return null;
    }

}
