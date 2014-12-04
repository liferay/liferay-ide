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
 *******************************************************************************/

package com.liferay.ide.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * @author Kuo Zhang
 */
public class ReflectionUtil
{

    public static Field getDeclaredFieldIncludingSuperClasses( Class<?> clazz, String fieldlName )
    {
        try
        {
            Field field = clazz.getDeclaredField( fieldlName );

            return field;
        }
        catch( NoSuchFieldException e )
        {
            if( clazz.getSuperclass() != null )
            {
                return getDeclaredFieldIncludingSuperClasses( clazz.getSuperclass(), fieldlName );
            }
            else
            {
                return null;
            }
        }
        catch( Exception e )
        {
            return null;
        }
    }

    public static Method getDeclaredMethodIncludingSuperClasses( Class<?> clazz, String methodName, Class<?>... parameterTypes )
    {
        try
        {
            Method method = clazz.getDeclaredMethod( methodName, parameterTypes );

            return method;
        }
        catch( NoSuchMethodException e )
        {
            if( clazz.getSuperclass() != null )
            {
                return getDeclaredMethodIncludingSuperClasses( clazz.getSuperclass(), methodName, parameterTypes );
            }
            else
            {
                return null;
            }
        }
        catch( Exception e )
        {
            return null;
        }
    }
}
