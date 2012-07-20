/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

/**
 * @author Gregory Amerson
 */
public class NameValueObject
{

    private String name;
    private Object value;

    public NameValueObject( String name, Object value )
    {
        this.name = name;
        this.value = value;
    }

    public NameValueObject()
    {
    }

    public String getName()
    {
        return this.name;
    }

    public void setName( String n )
    {
        this.name = n;
    }

    public Object getValue()
    {
        return this.value;
    }

    public void setValue( Object v )
    {
        this.value = v;
    }
}
