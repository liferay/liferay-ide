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

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * An abstract base class for custom iterators that only requires to implement a single {@link #computeNextElement()} method.
 *
 * @author Eike Stepper
 */
public abstract class AbstractIterator<T> implements Iterator<T>
{
  /**
   * The token to be used in {@link #computeNextElement()} to indicate the end of the iteration.
   */
  protected static final Object END_OF_DATA = new Object();

  private boolean nextComputed;

  private T next;

  public AbstractIterator()
  {
  }

  public final boolean hasNext()
  {
    if (nextComputed)
    {
      return true;
    }

    Object object = computeNextElement();
    nextComputed = true;

    if (object == END_OF_DATA)
    {
      return false;
    }

    @SuppressWarnings("unchecked")
    T cast = (T)object;
    next = cast;
    return true;
  }

  public final T next()
  {
    if (!hasNext())
    {
      throw new NoSuchElementException();
    }

    nextComputed = false;
    return next;
  }

  public void remove()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the next iteration element, or {@link #END_OF_DATA} if the end of the iteration has been reached.
   */
  protected abstract Object computeNextElement();

  @SuppressWarnings("unchecked")
  public static <T> ListIterator<T> empty()
  {
    return (ListIterator<T>)EmptyIterator.INSTANCE;
  }

  /**
   * @author Eike Stepper
   */
  private static final class EmptyIterator implements ListIterator<Object>
  {
    private static final ListIterator<Object> INSTANCE = new EmptyIterator();

    public boolean hasNext()
    {
      return false;
    }

    public Object next()
    {
      throw new NoSuchElementException();
    }

    public boolean hasPrevious()
    {
      return false;
    }

    public Object previous()
    {
      throw new NoSuchElementException();
    }

    public int nextIndex()
    {
      throw new NoSuchElementException();
    }

    public int previousIndex()
    {
      throw new NoSuchElementException();
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }

    public void set(Object e)
    {
      throw new UnsupportedOperationException();
    }

    public void add(Object e)
    {
      throw new UnsupportedOperationException();
    }
  }
}
