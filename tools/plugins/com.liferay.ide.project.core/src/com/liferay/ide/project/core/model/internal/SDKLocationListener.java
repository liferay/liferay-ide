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
package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
public class SDKLocationListener extends FilteredListener<PropertyContentEvent>
{

    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        updateLocation( op( event ) );
    }

    protected NewLiferayPluginProjectOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( NewLiferayPluginProjectOp.class );
    }

    public static void updateLocation( final NewLiferayPluginProjectOp op )
    {
        Path newLocationBase = null;

        Path sdkLocation = op.getSdkLocation().content(true);

        if ( sdkLocation == null )
        {
            return;
        }

        SDK sdk = SDKUtil.createSDKFromLocation( PathBridge.create( sdkLocation ) );

        op.setImportProjectStatus( false );

        if( sdk != null )
        {
            switch ( op.getPluginType().content( true ) )
            {
                case portlet:
                case servicebuilder:
                    newLocationBase = sdkLocation.append( "portlets" ); //$NON-NLS-1$
                    break;
                case ext:
                    newLocationBase = sdkLocation.append( "ext" ); //$NON-NLS-1$
                    break;
                case hook:
                    newLocationBase = sdkLocation.append( "hooks" ); //$NON-NLS-1$
                    break;
                case layouttpl:
                    newLocationBase = sdkLocation.append( "layouttpl" ); //$NON-NLS-1$
                    break;
                case theme:
                    newLocationBase = sdkLocation.append( "themes" ); //$NON-NLS-1$
                    break;
                case web:
                    newLocationBase = sdkLocation.append( "webs" ); //$NON-NLS-1$
                    break;
            }
        }
        else
        {
            return;
        }

        if( newLocationBase != null )
        {
            NewLiferayPluginProjectOpMethods.updateLocation( op, newLocationBase );
        }

    }
}
