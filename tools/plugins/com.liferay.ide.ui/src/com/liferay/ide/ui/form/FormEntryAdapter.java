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

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.events.HyperlinkEvent;

/**
 * @author Gregory Amerson
 */
public class FormEntryAdapter implements IFormEntryListener {

	public FormEntryAdapter(IContextPart contextPart) {
		this(contextPart, null);
	}

	public FormEntryAdapter(IContextPart contextPart, IActionBars actionBars) {
		_contextPart = contextPart;

		this.actionBars = actionBars;
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.newparts.IFormEntryListener#
	 * browseButtonSelected(org.eclipse.pde.internal.ui.newparts.FormEntry)
	 */
	public void browseButtonSelected(FormEntry entry) {
	}

	public void focusGained(FormEntry entry) {

		// ITextSelection selection = new TextSelection(1, 1);
		// contextPart.getPage().getEditor().getContributor().updateSelectableActions(selection);

	}

	/**
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.forms.events.HyperlinkListener#linkActivated(org.eclipse.
	 * ui.forms.events.HyperlinkEvent)
	 */
	public void linkActivated(HyperlinkEvent e) {
	}

	/**
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.forms.events.HyperlinkListener#linkEntered(org.eclipse.ui.
	 * forms.events.HyperlinkEvent)
	 */
	public void linkEntered(HyperlinkEvent e) {
		if (actionBars == null) {
			return;
		}

		IStatusLineManager mng = actionBars.getStatusLineManager();

		mng.setMessage(e.getLabel());
	}

	/**
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.forms.events.HyperlinkListener#linkExited(org.eclipse.ui.
	 * forms.events.HyperlinkEvent)
	 */
	public void linkExited(HyperlinkEvent e) {
		if (actionBars == null) {
			return;
		}

		IStatusLineManager mng = actionBars.getStatusLineManager();

		mng.setMessage(null);
	}

	public void selectionChanged(FormEntry entry) {

		// ITextSelection selection = new TextSelection(1, 1);
		// contextPart.getPage().getPDEEditor().getContributor().updateSelectableActions(selection);

	}

	/**
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.pde.internal.ui.newparts.IFormEntryListener#textDirty(org.
	 * eclipse.pde.internal.ui.newparts.FormEntry)
	 */
	public void textDirty(FormEntry entry) {
		_contextPart.fireSaveNeeded();
	}

	/**
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.pde.internal.ui.newparts.IFormEntryListener#textValueChanged(
	 * org.eclipse.pde.internal.ui.newparts.FormEntry)
	 */
	public void textValueChanged(FormEntry entry) {
	}

	protected IActionBars actionBars;

	private IContextPart _contextPart;

}