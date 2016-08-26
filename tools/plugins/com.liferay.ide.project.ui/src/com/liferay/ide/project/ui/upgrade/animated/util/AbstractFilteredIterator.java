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

/**
 * An abstract base class for custom iterators that {@link #isValid(Object) filter} the elements of a delegate iterator.
 *
 * @author Eike Stepper
 */
public abstract class AbstractFilteredIterator<T> extends AbstractIterator<T>
{
  private Iterator<T> delegate;

  public AbstractFilteredIterator(Iterator<T> delegate)
  {
    this.delegate = delegate;
  }

  @Override
  protected Object computeNextElement()
  {
    while (delegate.hasNext())
    {
      T element = delegate.next();
      if (isValid(element))
      {
        return element;
      }
    }

    return END_OF_DATA;
  }

  protected abstract boolean isValid(T element);
}
