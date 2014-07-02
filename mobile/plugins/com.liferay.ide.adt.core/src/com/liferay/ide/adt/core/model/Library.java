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
package com.liferay.ide.adt.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;


/**
 * @author Gregory Amerson
 */
public interface Library extends Element
{
    ElementType TYPE = new ElementType( Library.class );

    // *** Context ***

    ValueProperty PROP_CONTEXT = new ValueProperty( TYPE, "Context" );

    Value<String> getContext();
    void setContext( String value );


    // *** Entity ***

    ValueProperty PROP_ENTITY = new ValueProperty( TYPE, "Entity" );

    Value<String> getEntity();
    void setEntity( String value );

}
