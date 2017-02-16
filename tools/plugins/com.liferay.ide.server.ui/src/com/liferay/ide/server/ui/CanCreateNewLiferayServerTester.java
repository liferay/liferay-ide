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

package com.liferay.ide.server.ui;

import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;

/**
 * @author Gregory Amerson
 */
public class CanCreateNewLiferayServerTester extends PropertyTester
{

    @Override
    public boolean test( final Object receiver, final String property, final Object[] args, final Object expectedValue )
    {
        if( receiver instanceof IFolder )
        {
            final IFolder folder = (IFolder) receiver;

            final IPath location = folder.getRawLocation();

            if( location != null && location.toFile().exists() )
            {
                return LiferayServerCore.isPortalBundlePath( location );
            }
        }

        return false;
    }

}
