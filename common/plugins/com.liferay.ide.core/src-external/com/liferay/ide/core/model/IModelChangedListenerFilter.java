/*******************************************************************************
 *  Copyright (c) 2000, 2008 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.core.model;

/**
 * This filted is to be used when listeners are copied from
 * model to model. It allows some listeners to be skipped in
 * the process.
 */
public interface IModelChangedListenerFilter {
	/**
	 * Tests if the listener should be accepted.
	 * @param listener the listener to test
	 * @return <code>true</code> if the listener should pass
	 * the filter, <code>false</code> otherwise.
	 */
	public boolean accept(IModelChangedListener listener);

}
