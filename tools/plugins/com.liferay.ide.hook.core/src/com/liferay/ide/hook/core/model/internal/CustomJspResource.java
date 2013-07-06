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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.hook.core.model.CustomJsp;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyBinding;
import org.eclipse.sapphire.Resource;
import org.eclipse.sapphire.ValuePropertyBinding;

/**
 * @author Gregory Amerson
 */
public class CustomJspResource extends Resource
{

    private ObjectValue<String> value;

    public CustomJspResource( Resource parent )
    {
        super( parent );
    }

    public CustomJspResource( Resource parent, ObjectValue<String> customJsp )
    {
        super( parent );
        this.value = customJsp;
    }

    @Override
    protected PropertyBinding createBinding( final Property property )
    {
        if( property.definition() == CustomJsp.PROP_VALUE )
        {
            ValuePropertyBinding binding = new ValuePropertyBinding()
            {

                @Override
                public String read()
                {
                    return CustomJspResource.this.value.getValue();
                }

                @Override
                public void write( final String value )
                {
                    CustomJspResource.this.value.setValue( value );
                }
            };

            binding.init( property );

            return binding;
        }

        return null;
    }

    public ObjectValue<String> getCustomJsp()
    {
        return this.value;
    }

}
