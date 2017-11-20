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

import com.liferay.ide.kaleo.core.model.Action;
import com.liferay.ide.kaleo.core.model.Notification;
import com.liferay.ide.kaleo.ui.IKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.ui.Presentation;

/**
 * @author Gregory Amerson
 */
public class EditNotificationHandler extends ListSelectionEditHandler {

	@Override
	public Object edit(Element modelElement, Presentation context) {
		Notification notification = modelElement.nearest(Notification.class);

		IKaleoEditorHelper kaleoEditorHelper = KaleoUI.getKaleoEditorHelper(
			notification.getTemplateLanguage().text(true));

		kaleoEditorHelper.openEditor(getPart(), notification, Notification.PROP_TEMPLATE);

		return null;
	}

	@Override
	protected ImageData typeImage() {
		return Action.TYPE.image();
	}

}