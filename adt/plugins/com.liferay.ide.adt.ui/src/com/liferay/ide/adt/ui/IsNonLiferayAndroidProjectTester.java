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
 *******************************************************************************/

package com.liferay.ide.adt.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;

import com.liferay.ide.adt.core.ADTUtil;

/**
 * @author Kuo Zhang
 */

public class IsNonLiferayAndroidProjectTester extends PropertyTester
{

    @Override
    public boolean test( Object receiver, String property, Object[] args, Object expectedValue )
    {
        if( receiver instanceof IProject )
        {
            return ADTUtil.isAndroidProject( (IProject) receiver ) &&
                !ADTUtil.hasLiferayMobileSdkLibraries( (IProject) receiver );
        }

        return false;
    }

}
