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
package com.liferay.ide.project.ui.upgrade.animated;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public class AnimatedShell<T> extends Shell
{
  private AnimatedCanvas canvas;

  private T result;

  public AnimatedShell()
  {
    init();
  }

  public AnimatedShell(Display display, int style)
  {
    super(display, style);
    init();
  }

  public AnimatedShell(Display display)
  {
    super(display);
    init();
  }

  public AnimatedShell(int style)
  {
    super(style);
    init();
  }

  public AnimatedShell(Shell parent, int style)
  {
    super(parent, style);
    init();
  }

  public AnimatedShell(Shell parent)
  {
    super(parent);
    init();
  }

  public final AnimatedCanvas getCanvas()
  {
    return canvas;
  }

  public T getResult()
  {
    return result;
  }

  public void setResult(T result)
  {
    this.result = result;
  }

  public final T openModal()
  {
    open();

    Display display = getDisplay();
    while (!isDisposed())
    {
      if (!display.readAndDispatch())
      {
        display.sleep();
      }
    }

    return result;
  }

  @Override
  protected void checkSubclass()
  {
    // Do nothing.
  }

  protected void init()
  {
    setLayout(new FillLayout());
    canvas = new AnimatedCanvas(this, SWT.NONE);
  }
}
