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

import com.liferay.ide.adt.core.model.NewLiferayAndroidProjectOp;
import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;


/**
 * @author Gregory Amerson
 */
public class ProjectNameListener extends FilteredListener<PropertyContentEvent>
{

    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        updateProjectLocation( event );
    }

    protected NewLiferayAndroidProjectOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( NewLiferayAndroidProjectOp.class );
    }

    private void updateProjectLocation( PropertyContentEvent event )
    {
        final NewLiferayAndroidProjectOp op = op( event );
        final String currentProjectName = op.getProjectName().content();
        final boolean useDefaultLocation = op.getUseDefaultLocation().content( true );

        if( currentProjectName != null && useDefaultLocation )
        {
            final IPath path = CoreUtil.getWorkspaceRoot().getLocation().append( currentProjectName );

            try
            {
                final Path newLocation = PathBridge.create( path );
                op.setLocation( newLocation );
            }
            catch( Exception e )
            {
                // ignore errors
            }
        }
    }
}
