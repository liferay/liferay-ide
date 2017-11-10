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

package com.liferay.ide.ui.form;

import org.eclipse.ui.forms.events.IHyperlinkListener;

/**
 * @author Gregory Amerson
 */
public interface IFormEntryListener extends IHyperlinkListener {

	/**
	 * The user pressed the 'Browse' button for the entry.
	 *
	 * @param entry
	 */
	public void browseButtonSelected(FormEntry entry);

	/**
	 * The user clicked on the text control and focus was transfered to it.
	 *
	 * @param entry
	 */
	public void focusGained(FormEntry entry);

	public void selectionChanged(FormEntry entry);

	/**
	 * The user changed the text in the text control of the entry.
	 *
	 * @param entry
	 */
	public void textDirty(FormEntry entry);

	/**
	 * The value of the entry has been changed to be the text in the text
	 * control (as a result of 'commit' action).
	 *
	 * @param entry
	 */
	public void textValueChanged(FormEntry entry);

}