/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Gregory Amerson
 */
public class LocationListener extends ProjectNameListener
{
    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        final NewLiferayPluginProjectOp op = op( event );

        if( op.getUseDefaultLocation().content( true ) )
        {
            super.handleTypedEvent( event );
        }
        else
        {
            // if we have a sdk project and a custom location make sure the location has the correct suffix
            if( op.getProjectProvider().content( true ).getShortName().equals( "ant" ) ) //$NON-NLS-1$
            {
                if( ! op.getUseDefaultLocation().content( true ) )
                {
                    String suffix = null;

                    switch( op.getPluginType().content( true ) )
                    {
                        case portlet:
                        case servicebuilder:
                            suffix = "-portlet"; //$NON-NLS-1$
                            break;
                        case hook:
                            suffix = "-hook"; //$NON-NLS-1$
                            break;
                        case ext:
                            suffix = "-ext"; //$NON-NLS-1$
                            break;
                        case layouttpl:
                            suffix = "-layouttpl"; //$NON-NLS-1$
                            break;
                        case theme:
                            suffix = "-theme"; //$NON-NLS-1$
                            break;
                        case web:
                            suffix = "-web"; //$NON-NLS-1$
                            break;
                    }

                    final Path currentLocation = op.getLocation().content( true );

                    if( currentLocation != null && currentLocation.segmentCount() > 0 )
                    {
                        final String lastSegment = currentLocation.lastSegment();

                        if( ! lastSegment.endsWith( suffix ) )
                        {
                            NewLiferayPluginProjectOpMethods.updateLocation( op, currentLocation );
                        }
                    }
                }
            }
        }
    }
}
