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

package com.liferay.ide.hook.core.model.internal;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;

import com.liferay.ide.hook.core.model.CustomJspDir;
import com.liferay.ide.hook.core.model.Hook;

/**
 * @author Kuo Zhang
 */
public class CustomJspDirListener extends FilteredListener<PropertyContentEvent>
{

    // IDE-1132, Listen the change of Property CustomJspDir, and refresh the Property CustomJsps.
    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        final Property prop = event.property();

        if( CustomJspDir.PROP_VALUE.equals( prop.definition() ) )
        {
            prop.element().nearest( Hook.class ).property( Hook.PROP_CUSTOM_JSPS ).refresh();
        }
    }

}
