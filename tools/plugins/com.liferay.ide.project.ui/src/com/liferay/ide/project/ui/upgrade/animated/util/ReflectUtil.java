/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA) - bug 376620: switch on primitive types
 */
package com.liferay.ide.project.ui.upgrade.animated.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Various static helper methods for dealing with Java reflection.
 *
 * @author Eike Stepper
 */
public final class ReflectUtil
{
  private ReflectUtil()
  {
  }

  public static <T> Constructor<T> getConstructor(Class<T> c, Class<?>... parameterTypes)
  {
    try
    {
      Constructor<T> constructor = c.getDeclaredConstructor(parameterTypes);
      makeAccessible(constructor);
      return constructor;
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ReflectionException(ex);
    }
  }

  public static <T> T newInstance(Constructor<T> constructor, Object... arguments)
  {
    try
    {
      return constructor.newInstance(arguments);
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (InvocationTargetException ex)
    {
      Throwable cause = ex.getCause();
      if (cause != null && "org.eclipse.core.runtime.OperationCanceledException".equals(cause.getClass().getName()))
      {
        throw (RuntimeException)cause;
      }

      throw new ReflectionException(cause);
    }
    catch (Exception ex)
    {
      throw new ReflectionException(ex);
    }
  }

  public static Method getMethod(Object target, String methodName, Class<?>... parameterTypes)
  {
    return getMethod(target.getClass(), methodName, parameterTypes);
  }

  public static Method getMethod(Class<?> c, String methodName, Class<?>... parameterTypes)
  {
    try
    {
      try
      {
        Method method = c.getDeclaredMethod(methodName, parameterTypes);
        makeAccessible(method);
        return method;
      }
      catch (NoSuchMethodException ex)
      {
        Class<?> superclass = c.getSuperclass();
        if (superclass != null)
        {
          return getMethod(superclass, methodName, parameterTypes);
        }

        throw ex;
      }
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ReflectionException(ex);
    }
  }

  public static Object invokeMethod(Method method, Object target, Object... arguments)
  {
    try
    {
      return method.invoke(target, arguments);
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (InvocationTargetException ex)
    {
      Throwable cause = ex.getCause();
      if (cause != null && "org.eclipse.core.runtime.OperationCanceledException".equals(cause.getClass().getName()))
      {
        throw (RuntimeException)cause;
      }

      throw new ReflectionException(cause);
    }
    catch (Exception ex)
    {
      throw new ReflectionException(ex);
    }
  }

  public static Object invokeMethod(String methodName, Object target)
  {
    if (target instanceof Class)
    {
      return invokeMethod(getMethod((Class<?>)target, methodName), null);
    }

    return invokeMethod(getMethod(target.getClass(), methodName), target);
  }

  public static Field getField(Class<?> c, String fieldName)
  {
    try
    {
      try
      {
        Field field = c.getDeclaredField(fieldName);
        makeAccessible(field);
        return field;
      }
      catch (NoSuchFieldException ex)
      {
        Class<?> superclass = c.getSuperclass();
        if (superclass != null)
        {
          return getField(superclass, fieldName);
        }

        return null;
      }
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ReflectionException(ex);
    }
  }

  public static <T> T getValue(Field field, Object target)
  {
    try
    {
      @SuppressWarnings("unchecked")
      T value = (T)field.get(target);
      return value;
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ReflectionException(ex);
    }
  }

  public static <T> T getValue(String fieldName, Object target)
  {
    if (target instanceof Class)
    {
      return getValue(getField((Class<?>)target, fieldName), null);
    }

    return getValue(getField(target.getClass(), fieldName), target);
  }

  public static void setValue(String fieldName, Object target, Object value)
  {
    if (target instanceof Class)
    {
      setValue(getField((Class<?>)target, fieldName), null, value);
    }
    else
    {
      setValue(getField(target.getClass(), fieldName), target, value);
    }
  }

  public static void setValue(Field field, Object target, Object value)
  {
    setValue(field, target, value, false);
  }

  public static void setValue(Field field, Object target, Object value, boolean force)
  {
    try
    {
      if ((field.getModifiers() & Modifier.FINAL) != 0 && force)
      {
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        makeAccessible(modifiersField);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
      }

      Class<?> type = field.getType();
      if (type.isPrimitive())
      {
        if (boolean.class == type)
        {
          field.setBoolean(target, (Boolean)value);
        }
        else if (char.class == type)
        {
          field.setChar(target, (Character)value);
        }
        else if (byte.class == type)
        {
          field.setByte(target, (Byte)value);
        }
        else if (short.class == type)
        {
          field.setShort(target, (Short)value);
        }
        else if (int.class == type)
        {
          field.setInt(target, (Integer)value);
        }
        else if (long.class == type)
        {
          field.setLong(target, (Long)value);
        }
        else if (float.class == type)
        {
          field.setFloat(target, (Float)value);
        }
        else if (double.class == type)
        {
          field.setDouble(target, (Double)value);
        }
      }
      else
      {
        field.set(target, value);
      }
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ReflectionException(ex);
    }
  }

  private static <T> void makeAccessible(AccessibleObject accessibleObject)
  {
    if (!accessibleObject.isAccessible())
    {
      accessibleObject.setAccessible(true);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ReflectionException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    public ReflectionException()
    {
    }

    public ReflectionException(String message, Throwable cause)
    {
      super(message, cause);
    }

    public ReflectionException(String message)
    {
      super(message);
    }

    public ReflectionException(Throwable cause)
    {
      super(cause);
    }
  }
}
