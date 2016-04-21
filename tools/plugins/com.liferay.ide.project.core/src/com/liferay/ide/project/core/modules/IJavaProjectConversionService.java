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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.UniversalConversionService;

/**
 * @author Gregory Amerson
 */
public class IJavaProjectConversionService extends UniversalConversionService
{

    @Override
    public <T> T convert( Object object, Class<T> type )
    {
        if( object instanceof NewLiferayComponentOp && IJavaProject.class.equals( type ) )
        {
            final NewLiferayComponentOp op = (NewLiferayComponentOp) object;

            final String projectName = op.getProjectName().content( true );

            if( projectName != null )
            {
                final IProject project = CoreUtil.getProject( projectName );

                if( project != null && project.exists() )
                {
                    final IJavaProject javaProject = JavaCore.create( project );

                    if( javaProject != null )
                    {
                        return type.cast( javaProject );
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected void init()
    {
        super.init();

        FilteredListener<PropertyContentEvent> listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                broadcast();
            }
        };

        context( NewLiferayComponentOp.class ).attach( listener, NewLiferayComponentOp.PROP_PROJECT_NAME.name() );
    }
}
