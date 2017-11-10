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

package com.liferay.ide.ui.editor;

import org.eclipse.core.resources.IFile;

/**
 * @author Gregory Amerson
 */
public interface IInputContextListener {

	/**
	 * Informs the listener that a new context has been added. This should
	 * result in a new source tab.
	 *
	 * @param context
	 */
	public void contextAdded(InputContext context);

	/**
	 * Informs the listener that the context has been removed. This should
	 * result in removing the source tab.
	 *
	 * @param context
	 */
	public void contextRemoved(InputContext context);

	/**
	 * Informs the listener that a monitored file has been added.
	 *
	 * @param monitoredFile
	 *            the file we were monitoring
	 */
	public void monitoredFileAdded(IFile monitoredFile);

	/**
	 * Informs the listener that a monitored file has been removed.
	 *
	 * @param monitoredFile
	 * @return <code>true</code> if it is OK to remove the associated context,
	 *         <code>false</code> otherwise.
	 */
	public boolean monitoredFileRemoved(IFile monitoredFile);

}