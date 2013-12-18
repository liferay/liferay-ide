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
package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.services.DefaultValueService;
import org.eclipse.sapphire.services.DefaultValueServiceData;

/**
 * @author Tao Tao
 */
public class ArtifactVersionDefaultValueService extends DefaultValueService
{
    @Override
    protected DefaultValueServiceData compute()
    {
        String data = null;

        final Path location = op().getLocation().content();

        if( location != null )
        {
            final NewLiferayPluginProjectOp op = op();
            final String parentProjectLocation = location.toOSString();
            final IPath parentProjectOsPath = org.eclipse.core.runtime.Path.fromOSString( parentProjectLocation );
            final String projectName = op().getProjectName().content();

            data = NewLiferayPluginProjectOpMethods.getMavenParentPomVersion( op, projectName, parentProjectOsPath );
        }

        if( data == null )
        {
            data = "1.0.0-SNAPSHOT";
        }

        return new DefaultValueServiceData( data );
    }

    protected void initDefaultValueService()
    {
        super.initDefaultValueService();

        final Listener listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().getLocation().attach( listener );
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }
}
