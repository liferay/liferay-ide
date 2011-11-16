/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.hook.core.model.internal;

import com.liferay.ide.eclipse.hook.core.model.ICustomJsp;

import org.eclipse.sapphire.modeling.BindingImpl;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.ValueBindingImpl;


/**
 * @author Gregory Amerson
 */
public class CustomJspResource extends Resource
{

	private String value;

	public CustomJspResource( Resource parent )
	{
		super( parent );
	}

	public CustomJspResource( Resource parent, String customJsp )
	{
		super( parent );
		this.value = customJsp;
	}

	@Override
	protected BindingImpl createBinding( final ModelProperty property )
	{
		if ( property == ICustomJsp.PROP_VALUE )
		{
			ValueBindingImpl binding = new ValueBindingImpl()
			{

				@Override
				public String read()
				{
					return CustomJspResource.this.value;
				}

				@Override
				public void write( final String value )
				{
					CustomJspResource.this.value = value;
					CustomJspResource.this.parent().element().notifyPropertyChangeListeners( property );
				}
			};

			binding.init( element(), ICustomJsp.PROP_VALUE, null );

			return binding;
		}

		return null;
	}

}
