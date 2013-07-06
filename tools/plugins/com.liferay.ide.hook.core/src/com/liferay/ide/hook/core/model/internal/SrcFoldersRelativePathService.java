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
 * Contributors:

 ******************************************************************************/

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.hook.core.model.Hook;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.services.RelativePathService;

/**
 * @author Gregory Amerson
 */
public class SrcFoldersRelativePathService extends RelativePathService
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.services.RelativePathService#roots()
     */
    @Override
    public List<Path> roots()
    {
        List<Path> roots = new ArrayList<Path>();
        Element modelElement = context( Hook.class );
        IProject project = modelElement.adapt( IProject.class );
        IFolder[] folders = ProjectUtil.getSourceFolders( project );

        for( IFolder folder : folders )
        {
            roots.add( new Path( folder.getLocation().toPortableString() ) );
        }

        return roots;
    }
}
