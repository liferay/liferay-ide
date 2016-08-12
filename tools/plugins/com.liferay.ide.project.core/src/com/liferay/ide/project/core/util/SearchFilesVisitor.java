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
package com.liferay.ide.project.core.util;

import com.liferay.ide.core.LiferayCore;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Simon Jiang
 */
public class SearchFilesVisitor implements IResourceProxyVisitor
{

    protected String searchFileName = null;
    protected List<IFile> resources = new ArrayList<IFile>();

    public boolean visit( IResourceProxy resourceProxy )
    {
        if( resourceProxy.getType() == IResource.FILE && resourceProxy.getName().equals( searchFileName ) )
        {
            IResource resource = resourceProxy.requestResource();

            if( resource.exists() )
            {
                resources.add( (IFile) resource );
            }
        }

        return true;
    }

    public List<IFile> searchFiles( IResource container, String searchFileName )
    {
        this.searchFileName = searchFileName;
        try
        {
            container.accept( this, IContainer.EXCLUDE_DERIVED );
        }
        catch( CoreException e )
        {
            LiferayCore.logError( e );
        }

        return resources;
    }
}