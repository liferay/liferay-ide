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

import java.io.PrintWriter;

/**
 * @author Gregory Amerson
 */
public interface IEditable {

	/**
	 * Tests whether the model has been changed from the last clean state.
	 *
	 * @return <code>true</code> if the model has been changed and need saving
	 */
	public boolean isDirty();

	/**
	 * Tests whether the model marked as editable can be edited. Even though a model
	 * is generally editable, it can me marked as read-only because some condition
	 * prevents it from changing state (for example, the underlying resource is
	 * locked). While read-only models can never be changed, editable models can go
	 * in and out editable state during their life cycle.
	 *
	 * @return <code>true</code> if model can be modified, <code>false</code>
	 *         otherwise.
	 */
	public boolean isEditable();

	/**
	 * Saves the model into the provided writer. The assumption is that the model
	 * can be persisted in an ASCII output stream (for example, an XML file). This
	 * method should clear the 'dirty' flag when done.
	 *
	 * @param writer
	 *            an object that should be used to write ASCII representation of the
	 *            model
	 */
	public void save(PrintWriter writer);

	/**
	 * Sets the dirty flag of the model. This method is normally not intended to be
	 * used outside the model. Most often, a dirty model should be saved to clear
	 * the flag.
	 *
	 * @param dirty
	 *            a new value for the 'dirty' flag
	 */
	public void setDirty(boolean dirty);

}