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

/**
 * @author Andy Wu
 */
public class ImportWorkspaceLocationListener extends FilteredListener<PropertyContentEvent>
{

    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        Path path = op( event ).getWorkspaceLocation().content();

        if( LiferayWorkspaceUtil.hasBundlesDir( path.toOSString() ) )
        {
            op( event ).setHasBundlesDir( true );
            op( event ).setRunInitBundleCommand( true );
        }
        else
        {
            op( event ).setHasBundlesDir( false );
            op( event ).setRunInitBundleCommand( false );
        }
    }

    protected LiferayWorkspaceImportOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( LiferayWorkspaceImportOp.class );
    }
}
