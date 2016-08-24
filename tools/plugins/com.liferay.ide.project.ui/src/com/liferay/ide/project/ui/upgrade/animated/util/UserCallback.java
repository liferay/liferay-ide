/*
 * Copyright (c) 2014, 2015 Eike Stepper (Berlin, Germany) and others.
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
public class UserCallback
{
  public void execInUI(boolean async, Runnable runnable)
  {
    throw new UnsupportedOperationException();
  }

  public boolean runInProgressDialog(boolean async, IRunnable runnable)
  {
    throw new UnsupportedOperationException();
  }

  public void information(boolean async, String message)
  {
    throw new UnsupportedOperationException();
  }

  public boolean question(String message)
  {
    throw new UnsupportedOperationException();
  }
}
