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

import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class UseSdkLocationListener extends ProjectNameListener
{

    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        final NewLiferayPluginProjectOp op = op( event );

        if( op.getUseDefaultLocation().content( true ) )
        {
            if( op.getUseSdkLocation().content() ||
                op.getProjectProvider().service( ProjectProviderValidationService.class ).validation().ok() )
            {
                super.handleTypedEvent( event );
            }
        }
    }
}
