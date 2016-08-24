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

import org.eclipse.core.runtime.IProgressMonitor;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Eike Stepper
 */
public interface IRunnable
{
  public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException;
}
