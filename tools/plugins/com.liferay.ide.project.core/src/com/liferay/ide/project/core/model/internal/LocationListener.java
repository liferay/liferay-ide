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

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.ValuePropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class LocationListener extends FilteredListener<ValuePropertyContentEvent>
{

    protected NewLiferayPluginProjectOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( NewLiferayPluginProjectOp.class );
    }

    @Override
    protected void handleTypedEvent( ValuePropertyContentEvent event )
    {
        NewLiferayPluginProjectOp op = op(event);

        final boolean useDefaultLocation = op.getUseDefaultLocation().content( true );

        if ( !useDefaultLocation)
        {
            final String afterValue = event.after();

            final String beforeValue = event.before();
            
            if ( beforeValue == null && afterValue != null )
            {
                NewLiferayPluginProjectOpMethods.updateLocation( op, new Path(afterValue) );
            }
        }
    }
}
