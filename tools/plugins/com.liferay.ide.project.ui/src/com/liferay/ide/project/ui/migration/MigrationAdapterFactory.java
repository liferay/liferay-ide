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
package com.liferay.ide.project.ui.migration;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.ui.ProjectUI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.eclipse.ui.model.IWorkbenchAdapter;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( { "rawtypes", "unchecked" } )
public class MigrationAdapterFactory implements IAdapterFactory, IWorkbenchAdapter
{
    private static final Object instance = new MigrationAdapterFactory();

    @Override
    public Object getAdapter( Object adaptableObject, Class adapterType )
    {
        return instance;
    }

    @Override
    public Class[] getAdapterList()
    {
        return new Class[] { MPNode.class, MPTree.class };
    }

    @Override
    public Object[] getChildren( Object o )
    {
        return null;
    }

    @Override
    public ImageDescriptor getImageDescriptor( Object element )
    {
        if( element instanceof MPTree )
        {
            return ProjectUI.getDefault().getImageRegistry().getDescriptor( ProjectUI.MIGRATION_TASKS_IMAGE_ID );
        }
        else if( element instanceof MPNode )
        {
            final MPNode node = (MPNode) element;
            final IResource resource = CoreUtil.getWorkspaceRoot().findMember( node.incrementalPath );

            if( resource != null && resource.exists() && resource instanceof IProject )
            {
                return ImageDescriptor.createFromImage( PlatformUI.getWorkbench().getSharedImages().getImage(
                    SharedImages.IMG_OBJ_PROJECT ) );
            }
            else
            {
                return ImageDescriptor.createFromImage( PlatformUI.getWorkbench().getSharedImages().getImage(
                    ISharedImages.IMG_OBJ_FOLDER ) );
            }
        }

        return null;
    }

    @Override
    public String getLabel( Object element )
    {
        if( element instanceof MPTree )
        {
            return "Code Problems";
        }
        else if( element instanceof MPNode )
        {
            MPNode node = (MPNode) element;

            String label = node.data;

            if( label.startsWith( "/" ) )
            {
                label = label.substring( 1 );
            }

            return label;
        }

        return null;
    }

    @Override
    public Object getParent( Object o )
    {
        return null;
    }


}
