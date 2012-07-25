/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.hook.core.model.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.services.RelativePathService;

import com.liferay.ide.hook.core.model.Hook;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class DocrootRelativePathService extends RelativePathService
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.services.RelativePathService#roots()
     */
    @Override
    public List<Path> roots()
    {
        List<Path> roots = new ArrayList<Path>();
        IModelElement modelElement = context( Hook.class );
        IProject project = modelElement.adapt( IProject.class );
        IPath docRootPath = project.getLocation().append( "docroot" );
        roots.add( new Path( docRootPath.toPortableString() ) );
        return roots;
    }
}
