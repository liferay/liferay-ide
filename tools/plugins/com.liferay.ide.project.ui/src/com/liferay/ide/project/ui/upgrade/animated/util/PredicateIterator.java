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
 * @author Eike Stepper
 */
public class PredicateIterator<T> extends AbstractFilteredIterator<T>
{
  private final Predicate<? super T> predicate;

  public PredicateIterator(Iterator<T> delegate, Predicate<? super T> predicate)
  {
    super(delegate);
    this.predicate = predicate;
  }

  public PredicateIterator(Predicate<? super T> predicate, Iterator<T> delegate)
  {
    super(delegate);
    this.predicate = predicate;
  }

  public Predicate<? super T> getPredicate()
  {
    return predicate;
  }

  @Override
  protected boolean isValid(T element)
  {
    return predicate.apply(element);
  }
}
