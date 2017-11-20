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

package com.liferay.ide.kaleo.ui.action;

import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.ui.ListSelectionService;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.forms.PropertyEditorActionHandler;

/**
 * @author Gregory Amerson
 */
public abstract class ListSelectionEditHandler extends PropertyEditorActionHandler {

	public abstract Object edit(Element element, Presentation context);

	@Override
	public void init(SapphireAction action, ActionHandlerDef def) {
		super.init(action, def);

		ImageData typeImage = typeImage();

		if (typeImage != null) {
			addImage(typeImage);
		}

		ListSelectionService selectionService = action.getPart().service(ListSelectionService.class);

		Listener selectionListener = new Listener() {

			@Override
			public void handle(Event event) {
				refreshEnablementState();
			}

		};

		if (selectionService != null) {
			selectionService.attach(selectionListener);
		}
	}

	@Override
	protected boolean computeEnablementState() {
		if (super.computeEnablementState() == true) {
			ListSelectionService selectionService = getSelectionService();

			if (selectionService != null) {
				List<Element> selection = selectionService.selection();

				if ((selection != null) && (selection.size() == 1)) {
					return true;
				}

				return false;
			}
		}

		return false;
	}

	protected ListSelectionService getSelectionService() {
		return getPart().service(ListSelectionService.class);
	}

	@Override
	protected Object run(Presentation context) {
		Object retval = null;

		ListSelectionService selectionService = getSelectionService();

		if (selectionService != null) {
			List<Element> selection = selectionService.selection();

			retval = edit(selection.get(0), context);
		}

		return retval;
	}

	protected abstract ImageData typeImage();

}