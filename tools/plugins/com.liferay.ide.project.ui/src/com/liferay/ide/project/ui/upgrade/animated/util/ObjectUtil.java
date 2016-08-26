/*
 * Copyright (c) 2014, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package com.liferay.ide.project.ui.upgrade.animated.util;

/**
 * Various static helper methods.
 *
 * @author Eike Stepper
 */
public final class ObjectUtil
{
  private ObjectUtil()
  {
  }

  public static boolean equals(Object o1, Object o2)
  {
    if (o1 == null)
    {
      return o2 == null;
    }

    return o1.equals(o2);
  }

  public static int hashCode(Object o)
  {
    if (o == null)
    {
      return 0;
    }

    return o.hashCode();
  }

  public static <TYPE> TYPE adapt(Object object, Class<TYPE> type)
  {
    if (object == null)
    {
      return null;
    }

    Object adapter = null;
    if (type.isInstance(object))
    {
      adapter = object;
    }
    else
    {
      try
      {
        adapter = AdaptableHelper.adapt(object, type);
        if (adapter == null)
        {
          adapter = AdapterManagerHelper.adapt(object, type);
        }
      }
      catch (Throwable ignore)
      {
      }
    }

    return type.cast(adapter);
  }

  /**
   * Nested class to factor out dependencies on org.eclipse.core.runtime
   *
   * @author Eike Stepper
   */
  private static final class AdaptableHelper
  {
    public static Object adapt(Object object, Class<?> type)
    {
      if (object instanceof org.eclipse.core.runtime.IAdaptable)
      {
        return ((org.eclipse.core.runtime.IAdaptable)object).getAdapter(type);
      }

      return null;
    }
  }

  /**
   * Nested class to factor out dependencies on org.eclipse.core.runtime
   *
   * @author Eike Stepper
   */
  private static final class AdapterManagerHelper
  {
    private static org.eclipse.core.runtime.IAdapterManager adapterManager = org.eclipse.core.runtime.Platform.getAdapterManager();

    public static Object adapt(Object object, Class<?> type)
    {
      if (adapterManager != null)
      {
        return adapterManager.getAdapter(object, type);
      }

      return null;
    }
  }
}
