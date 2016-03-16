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
package com.liferay.ide.gradle.core.workspace;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

import com.liferay.ide.core.util.CoreUtil;

/**
 * @author Andy Wu
 */
public class WorkspaceNameListener extends FilteredListener<PropertyContentEvent>
{
    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        updateLocation( op( event ) );
    }

    protected NewLiferayWorkspaceOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( NewLiferayWorkspaceOp.class );
    }

    public static void updateLocation( final NewLiferayWorkspaceOp op )
    {
        final String currentWorkspaceName = op.getWorkspaceName().content(true);

        if( currentWorkspaceName == null )
        {
            return;
        }

        final boolean useDefaultLocation = op.getUseDefaultLocation().content( true );

        if( useDefaultLocation )
        {
            Path newLocationBase = null;

            newLocationBase = PathBridge.create( CoreUtil.getWorkspaceRoot().getLocation() );

            if( newLocationBase != null )
            {
                NewLiferayWorkspaceOpMethods.updateLocation( op, newLocationBase );
            }
        }
    }
}
