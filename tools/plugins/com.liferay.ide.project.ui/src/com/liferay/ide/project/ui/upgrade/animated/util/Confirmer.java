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
public interface Confirmer
{
  public static final Confirmer ACCEPT = new Confirmer()
  {
    public Confirmation confirm(boolean defaultConfirmed, Object info)
    {
      return new Confirmation(true, false);
    }
  };

  public static final Confirmer DECLINE = new Confirmer()
  {
    public Confirmation confirm(boolean defaultConfirmed, Object info)
    {
      return new Confirmation(false, false);
    }
  };

  public static final Confirmer DEFAULT = new Confirmer()
  {
    public Confirmation confirm(boolean defaultConfirmed, Object info)
    {
      return new Confirmation(defaultConfirmed, false);
    }
  };

  public Confirmation confirm(boolean defaultConfirmed, Object info);

  /**
   * @author Eike Stepper
   */
  public final class Confirmation
  {
    private final boolean confirmed;

    private final boolean remember;

    public Confirmation(boolean confirmed, boolean remember)
    {
      this.confirmed = confirmed;
      this.remember = remember;
    }

    public boolean isConfirmed()
    {
      return confirmed;
    }

    public boolean isRemember()
    {
      return remember;
    }

    @Override
    public String toString()
    {
      return (confirmed ? "Accept" : "Decline") + (remember ? " (remember)" : "");
    }
  }
}
