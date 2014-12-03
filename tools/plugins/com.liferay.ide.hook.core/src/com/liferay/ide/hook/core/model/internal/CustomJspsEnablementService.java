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
package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.hook.core.model.CustomJspDir;
import com.liferay.ide.hook.core.model.Hook;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.EnablementService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;


/**
 * @author Gregory Amerson
 */
public class CustomJspsEnablementService extends EnablementService
{

    @Override
    protected void initEnablementService()
    {
        final Listener listener = new FilteredListener<PropertyEvent>()
        {
            protected void handleTypedEvent( PropertyEvent event )
            {
                refresh();
            };
        };

        hook().property( Hook.PROP_CUSTOM_JSP_DIR ).attach( listener, CustomJspDir.PROP_VALUE.name() );
    }

    @Override
    protected Boolean compute()
    {
        boolean enablement = true;

        final CustomJspDir customJspDir = hook().getCustomJspDir().content();

        if( customJspDir != null )
        {
            final IProject project = hook().adapt( IProject.class );
            final Path customJspDirPath = customJspDir.getValue().content( true );

            if( project != null && customJspDirPath != null )
            {
                final IFolder defaultWebappDir = LiferayCore.create( project ).getDefaultDocrootFolder();

                if( defaultWebappDir != null && defaultWebappDir.exists() )
                {
                    final IFolder customJspFolder = defaultWebappDir.getFolder( PathBridge.create( customJspDirPath ) );

                    enablement = customJspFolder.exists();
                }
            }
        }

        return enablement;
    }

    private Hook hook()
    {
        return context( Hook.class );
    }

}
