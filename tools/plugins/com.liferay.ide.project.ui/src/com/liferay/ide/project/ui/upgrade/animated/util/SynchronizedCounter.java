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

/**
 * @author Eike Stepper
 */
public final class SynchronizedCounter
{
  private int count;

  public synchronized void countUp()
  {
    ++count;
    notifyAll();
  }

  public synchronized void countDown()
  {
    --count;
    notifyAll();
  }

  public synchronized int getCount()
  {
    return count;
  }

  public synchronized boolean isZero()
  {
    return count == 0;
  }

  public synchronized boolean awaitZero()
  {
    while (count != 0)
    {
      try
      {
        wait();
      }
      catch (InterruptedException ex)
      {
        return false;
      }
    }

    return true;
  }

  public synchronized boolean awaitChange()
  {
    int oldCount = count;
    while (count == oldCount)
    {
      try
      {
        wait();
      }
      catch (InterruptedException ex)
      {
        return false;
      }
    }

    return true;
  }

  @Override
  public String toString()
  {
    return "Counter[" + count + "]";
  }
}
