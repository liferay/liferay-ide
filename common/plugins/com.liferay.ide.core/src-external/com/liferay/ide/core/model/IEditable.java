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

import java.io.PrintWriter;

/**
 * Models that implement this interface indicate that
 * they can be changed. When a model is changed,
 * it becomes 'dirty'. This state can either be reset
 * (in case of a 'false alarm' or naturally set to
 * false as a result of saving the changes.
 * Models that implement this interface are expected
 * to be able to save in ASCII file format
 * (e.g. XML).
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 2.0
 */
public interface IEditable {
	/**
	 * Tests whether the model marked as editable can be
	 * edited. Even though a model is generally editable,
	 * it can me marked as read-only because some condition
	 * prevents it from changing state (for example,
	 * the underlying resource is locked). While 
	 * read-only models can never be changed, editable
	 * models can go in and out editable state during
	 * their life cycle.
	 * 
	 * @return <code>true</code> if model can be modified, <code>false</code>
	 * otherwise.
	 */
	public boolean isEditable();

	/**
	 * Tests whether the model has been changed from the last clean
	 * state.
	 * @return <code>true</code> if the model has been changed and need saving
	 */
	public boolean isDirty();

	/**
	 * Saves the model into the provided writer.
	 * The assumption is that the model can be
	 * persisted in an ASCII output stream (for example, an XML file).
	 * This method should clear the 'dirty' flag when
	 * done.
	 *
	 * @param writer an object that should be used to
	 * write ASCII representation of the model
	 */
	public void save(PrintWriter writer);

	/**
	 * Sets the dirty flag of the model. This method is
	 * normally not intended to be used outside the model. 
	 * Most often, a dirty model should be saved to clear the flag.
	 *
	 * @param dirty a new value for the 'dirty' flag
	 */
	void setDirty(boolean dirty);
}
