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

package com.liferay.ide.project.core.modules.fragment;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Joye Luo
 */
public class ModuleFragmentProjectArtifactVersionDefaultValueService extends DefaultValueService
{

    private Listener listener;

    @Override
    protected String compute()
    {
        String data = null;

        final Path location = op().getLocation().content();

        if( location != null )
        {
            final NewModuleFragmentOp op = op();
            final String parentProjectLocation = location.toOSString();
            final IPath parentProjectOsPath = org.eclipse.core.runtime.Path.fromOSString( parentProjectLocation );
            final String projectName = op().getProjectName().content();

            data = NewModuleFragmentOpMethods.getMavenParentPomVersion( op, projectName, parentProjectOsPath );
        }

        if( data == null )
        {
            data = "1.0.0-SNAPSHOT";
        }

        return data;
    }

    @Override
    public void dispose()
    {
        if( this.listener != null && op() != null && !op().disposed() )
        {
            op().getProjectName().detach( this.listener );
            op().getProjectName().attach( this.listener );

            this.listener = null;
        }

        super.dispose();
    }

    @Override
    protected void initDefaultValueService()
    {
        super.initDefaultValueService();

        this.listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().getLocation().attach( this.listener );
        op().getProjectName().attach( this.listener );
    }

    private NewModuleFragmentOp op()
    {
        return context( NewModuleFragmentOp.class );
    }

}
