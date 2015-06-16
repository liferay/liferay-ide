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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;


/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Kuo Zhang
 * @author Simon Jiang
 */
public class ProjectNameListener extends FilteredListener<PropertyContentEvent>
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
        final String currentProjectName = op.getProjectName().content();

        if( currentProjectName == null )
        {
            return;
        }

        final boolean useDefaultLocation = op.getUseDefaultLocation().content( true );
        final String providerShortName = op.getProjectProvider().content( true ).getShortName();

        if( useDefaultLocation )
        {
            Path newLocationBase = null;

            if( providerShortName.equals( "ant" ) )
            {
                SDK sdk = null;

                try
                {
                    sdk = SDKUtil.getWorkspaceSDK();
                }
                catch( CoreException e )
                {
                }

                if( sdk != null )
                {
                    final Path sdkLocation = PathBridge.create( sdk.getLocation() );

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
            }
            else
            {
                newLocationBase = PathBridge.create( CoreUtil.getWorkspaceRoot().getLocation() );
            }

            if( newLocationBase != null )
            {
                NewLiferayPluginProjectOpMethods.updateLocation( op, newLocationBase );
            }
        }

    }
}
