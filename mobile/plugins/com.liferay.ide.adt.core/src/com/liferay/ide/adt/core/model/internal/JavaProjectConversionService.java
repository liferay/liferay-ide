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
package com.liferay.ide.adt.core.model.internal;

import com.liferay.ide.adt.core.model.GenerateCustomServicesOp;
import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.sapphire.UniversalConversionService;


/**
 * @author Gregory Amerson
 */
public class JavaProjectConversionService extends UniversalConversionService
{

    @Override
    public <T> T convert( Object object, Class<T> type )
    {
        T result = null;

        if( object instanceof GenerateCustomServicesOp && type.equals( IJavaProject.class ) )
        {
            final GenerateCustomServicesOp op = (GenerateCustomServicesOp) object;
            final String projectName = op.getProjectName().content();
            final IProject project = CoreUtil.getProject( projectName );

            result = type.cast( JavaCore.create( project ) );
        }

        return result;
    }

}
