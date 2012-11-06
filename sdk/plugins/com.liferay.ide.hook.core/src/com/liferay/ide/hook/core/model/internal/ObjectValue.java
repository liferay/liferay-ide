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
public class ObjectValue<T>
{

    private T value;

    public ObjectValue( T value )
    {
        this.value = value;
    }

    public ObjectValue()
    {
    }

    public T getValue()
    {
        return this.value;
    }

    public void setValue( T v )
    {
        this.value = v;
    }
}
