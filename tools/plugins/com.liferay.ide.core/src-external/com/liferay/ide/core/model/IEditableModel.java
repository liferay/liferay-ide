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

/**
 * @author Gregory Amerson
 */
public interface IEditableModel extends IEditable {

	/**
	 * Saves the editable model using the mechanism suitable for the concrete model
	 * implementation. It is responsible for wrapping the
	 * <code>IEditable.save(PrintWriter)</code> operation and providing the print
	 * writer.
	 */
	public void save();

}