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

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class IOExceptionWithCause extends IOException
{
  private static final long serialVersionUID = 1L;

  public IOExceptionWithCause()
  {
  }

  public IOExceptionWithCause(String message)
  {
    super(message);
  }

  public IOExceptionWithCause(Throwable cause)
  {
    super(cause.getMessage());
    initCause(cause);
  }

  public IOExceptionWithCause(String message, Throwable cause)
  {
    super(message);
    initCause(cause);
  }
}
