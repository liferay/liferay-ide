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
 * Editable model is an editable object that can be saved. The classes
 * that implement this interface are responsible for calling the
 * method <code>save</code> of <code>IEditable</code> and supplying
 * the required <code>PrintWriter</code> object.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 2.0
 */
public interface IEditableModel extends IEditable {
	/**
	 * Saves the editable model using the mechanism suitable for the 
	 * concrete model implementation. It is responsible for 
	 * wrapping the <code>IEditable.save(PrintWriter)</code> operation
	 * and providing the print writer.
	 */
	void save();
}
