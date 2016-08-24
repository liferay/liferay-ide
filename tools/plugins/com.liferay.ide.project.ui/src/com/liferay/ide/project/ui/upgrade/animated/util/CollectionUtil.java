/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package com.liferay.ide.project.ui.upgrade.animated.util;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class CollectionUtil
{
  private CollectionUtil()
  {
  }

  public static <K, V> Set<V> getSet(Map<K, Set<V>> map, K key)
  {
    Set<V> set = map.get(key);
    if (set == null)
    {
      set = new LinkedHashSet<V>();
      map.put(key, set);
    }

    return set;
  }

  public static <K, V> boolean add(Map<K, Set<V>> map, K key, V value)
  {
    Set<V> set = getSet(map, key);
    return set.add(value);
  }

  public static <K, V> boolean addAll(Map<K, Set<V>> map, K key, Collection<? extends V> values)
  {
    Set<V> set = getSet(map, key);
    return set.addAll(values);
  }

  public static <K, V> boolean addAll(Map<K, Set<V>> map, Collection<? extends K> keys, V value)
  {
    boolean result = false;
    for (K key : keys)
    {
      if (add(map, key, value))
      {
        result = true;
      }
    }

    return result;
  }

  public static <K, V> boolean addAll(Map<K, Set<V>> map, Map<? extends K, ? extends Collection<? extends V>> map2)
  {
    boolean result = false;
    for (Map.Entry<? extends K, ? extends Collection<? extends V>> entry : map2.entrySet())
    {
      if (addAll(map, entry.getKey(), entry.getValue()))
      {
        result = true;
      }
    }

    return result;
  }

  public static <K, V> boolean putAll(Map<K, Set<V>> map, Map<? extends K, ? extends V> map2)
  {
    boolean result = false;
    for (Map.Entry<? extends K, ? extends V> entry : map2.entrySet())
    {
      if (add(map, entry.getKey(), entry.getValue()))
      {
        result = true;
      }
    }

    return result;
  }
}
