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
package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Andy Wu
 */
public class WorkspaceUseDefaultLocationListener extends FilteredListener<PropertyContentEvent>
{
    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        final NewLiferayWorkspaceOp op = op( event );

        if( op.getUseDefaultLocation().content( true ) )
        {
            op.setLocation( PathBridge.create( CoreUtil.getWorkspaceRoot().getLocation() ) );
        }
    }

    protected NewLiferayWorkspaceOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( NewLiferayWorkspaceOp.class );
    }
}
