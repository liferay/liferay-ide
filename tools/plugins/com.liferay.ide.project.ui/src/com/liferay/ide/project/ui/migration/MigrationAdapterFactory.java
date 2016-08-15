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

import com.liferay.ide.project.core.upgrade.MigrationProblems;
import com.liferay.ide.project.core.upgrade.FileProblems;
import com.liferay.ide.project.core.upgrade.UpgradeProblems;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * @author Gregory Amerson
 * @author Terry Jia
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
        return new Class[] { FileProblems.class, UpgradeProblems.class, ProblemsContainer.class };
    }

    @Override
    public Object[] getChildren( Object o )
    {
        return null;
    }

    @Override
    public ImageDescriptor getImageDescriptor( Object element )
    {
        if( element instanceof FileProblems )
        {
            return ImageDescriptor.createFromImage( PlatformUI.getWorkbench().getSharedImages().getImage(
                ISharedImages.IMG_OBJ_FILE ) );
        }
        else if( element instanceof ProblemsContainer )
        {
            return ImageDescriptor.createFromImage(
                PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_FOLDER ) );
        }
        else if( element instanceof UpgradeProblems )
        {
            return ImageDescriptor.createFromImage(
                PlatformUI.getWorkbench().getSharedImages().getImage(
                    org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT ) );
        }

        return null;
    }

    @Override
    public String getLabel( Object element )
    {
        if( element instanceof FileProblems )
        {
            FileProblems fp = (FileProblems) element;

            return fp.getFile().getName();
        }
        else if( element instanceof UpgradeProblems )
        {
            if( element instanceof MigrationProblems )
            {
                MigrationProblems cp = (MigrationProblems) element;

                return cp.getSuffix();
            }
            else
            {
                UpgradeProblems lp = (UpgradeProblems) element;

                return lp.getType();
            }
        }
        else if( element instanceof ProblemsContainer )
        {
            return ( (ProblemsContainer) element ).getType();
        }

        return null;
    }

    @Override
    public Object getParent( Object o )
    {
        return null;
    }

}
