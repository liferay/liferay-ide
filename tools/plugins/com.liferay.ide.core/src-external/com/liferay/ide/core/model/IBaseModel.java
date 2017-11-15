/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.core.model;

import org.eclipse.core.runtime.IAdaptable;

/**
 * @author Gregory Amerson
 */
public interface IBaseModel extends IAdaptable {

	/**
	 * Releases all the data in this model and clears the state. A disposed model
	 * can be returned to the normal state by reloading.
	 */
	public void dispose();

	/**
	 * Tests if this model has been disposed. Disposed model cannot be used until it
	 * is loaded/reloaded.
	 *
	 * @return <code>true</code> if the model has been disposed
	 */
	public boolean isDisposed();

	/**
	 * Tests if this model can be modified. Modification of a model that is not
	 * editable will result in CoreException being thrown.
	 *
	 * @return <code>true</code> if this model can be modified
	 */
	public boolean isEditable();

	/**
	 * Tests if this model valid. When models are loaded from the file, they may
	 * pass the syntax error checking and load all the model objects. However, some
	 * of the objects may contain invalid values that make the model unusable.
	 *
	 * @return <code>true</code> only if the model can be safely used in all
	 *         computations.
	 */
	public boolean isValid();

}