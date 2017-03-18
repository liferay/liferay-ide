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

import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;

/**
 * @author Simon Jiang
 */

public abstract class ProviderValueServerConditon<T extends ExecutableElement> extends ServiceCondition
{

    @Override
    public boolean applicable( final ServiceContext context )
    {
        boolean retval = false;

        final ValueProperty prop = context.find( ValueProperty.class );

        ValueProperty property = getProperty( context );

        if( prop != null && ( prop.equals( property ) ) )
        {
            retval = true;
        }

        return retval;
    }

    protected abstract ValueProperty getProperty( final ServiceContext context );
}
