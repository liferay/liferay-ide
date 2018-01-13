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

package com.liferay.ide.kaleo.ui.diagram;

import com.liferay.ide.kaleo.core.model.Notification;
import com.liferay.ide.kaleo.ui.IKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.forms.DetailSectionPart;
import org.eclipse.sapphire.ui.forms.FormPart;

/**
 * @author Gregory Amerson
 */
public class TemplateOpenActionHandler extends SapphireActionHandler {

	protected Notification notification(Presentation context) {
		ISapphirePart part = context.part();

		if (part instanceof DetailSectionPart) {
			DetailSectionPart pageBook = part.nearest(DetailSectionPart.class);

			FormPart currentPage = pageBook.getCurrentPage();

			Element element = currentPage.getLocalModelElement();

			return element.nearest(Notification.class);
		}

		SapphirePart spPart = context.part();

		Element element = spPart.getLocalModelElement();

		return element.nearest(Notification.class);
	}

	@Override
	protected Object run(Presentation context) {
		try {
			Notification notification = notification(context);

			IKaleoEditorHelper kaleoEditorHelper = KaleoUI.getKaleoEditorHelper(
				notification.getTemplateLanguage().text(true));

			kaleoEditorHelper.openEditor(context.part(), notification, Notification.PROP_TEMPLATE);
		}
		catch (Exception e) {
			KaleoUI.logError("Could not open template editor.", e);
		}

		return null;
	}

}