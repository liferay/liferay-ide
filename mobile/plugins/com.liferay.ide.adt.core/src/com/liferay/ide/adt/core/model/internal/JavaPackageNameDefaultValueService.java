/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.adt.core.model.internal;

import com.liferay.ide.adt.core.ADTCore;
import com.liferay.ide.adt.core.model.MobileSDKLibrariesOp;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.sapphire.DefaultValueService;

/**
 * @author Kuo Zhang
 */
public class JavaPackageNameDefaultValueService extends DefaultValueService
{

    @Override
    protected String compute()
    {
        final IJavaProject project = op().adapt(IJavaProject.class);

        try
        {
            final IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();

            for (IPackageFragmentRoot root : roots)
            {
                if( root.getKind() != IPackageFragmentRoot.K_BINARY )
                {
                    for( IJavaElement element : root.getChildren() )
                    {
                        if( element instanceof IPackageFragment )
                        {
                           final IPackageFragment fragment = (IPackageFragment) element;

                           if( ! fragment.isDefaultPackage() && ! fragment.hasSubpackages() )
                           {
                               return fragment.getElementName();
                           }
                        }
                    }
                }
            }
        }
        catch( Exception e )
        {
            ADTCore.logError( e.getMessage(), e );
        }

        return null;
    }

    private MobileSDKLibrariesOp op()
    {
        return context( MobileSDKLibrariesOp.class );
    }

}
