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

import org.eclipse.text.edits.TextEdit;

/**
 * @author Gregory Amerson
 */
public interface IModelTextChangeListener extends IModelChangedListener {

	/**
	 * Get a human readable name for the given TextEdit for use in a refactoring
	 * preview, for instance.
	 *
	 * @param edit
	 *            the edit to get a name for
	 * @return the name associated to the given edit, or null if there is none
	 */
	public String getReadableName(TextEdit edit);

	public TextEdit[] getTextOperations();

}