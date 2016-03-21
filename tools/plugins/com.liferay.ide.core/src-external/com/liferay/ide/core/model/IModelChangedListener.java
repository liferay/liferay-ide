/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.core.model;

/**
 * Classes that need to be notified on model
 * changes should implement this interface
 * and add themselves as listeners to
 * the model they want to listen to.
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @since 2.0
 */
public interface IModelChangedListener {
	/**
	 * Called when there is a change in the model
	 * this listener is registered with.
	 *
	 * @param event a change event that describes
	 * the kind of the model change
	 */
	public void modelChanged(IModelChangedEvent event);
}
