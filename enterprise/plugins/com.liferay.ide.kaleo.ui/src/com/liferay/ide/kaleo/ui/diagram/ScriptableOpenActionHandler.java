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

import com.liferay.ide.kaleo.core.model.Scriptable;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.ui.IKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.forms.DetailSectionPart;
import org.eclipse.sapphire.ui.forms.FormPart;

/**
 * @author Gregory Amerson
 */
public class ScriptableOpenActionHandler extends SapphireActionHandler {

	@Override
	protected Object run(Presentation context) {
		try {
			Scriptable scriptable = scriptable(context);

			if (scriptable != null) {
				IKaleoEditorHelper kaleoEditorHelper = KaleoUI.getKaleoEditorHelper(
					scriptable.getScriptLanguage().text(true));

				kaleoEditorHelper.openEditor(context.part(), scriptable, Scriptable.PROP_SCRIPT);
			}
		}
		catch (Exception e) {
			KaleoUI.logError("Could not open script editor.", e);
		}

		return null;
	}

	protected Scriptable scriptable(Presentation context) {
		Scriptable retval = null;

		ISapphirePart part = context.part();

		if (part instanceof DetailSectionPart) {
			DetailSectionPart pageBook = part.nearest(DetailSectionPart.class);

			FormPart currentPage = pageBook.getCurrentPage();

			Element element = currentPage.getLocalModelElement();

			retval = element.nearest(Scriptable.class);
		}
		else {
			Element modelElement = context.part().getLocalModelElement();

			if (modelElement instanceof Task) {
				Task task = modelElement.nearest(Task.class);

				ElementHandle<Scriptable> scriptable = task.getScriptedAssignment();

				retval = scriptable.content(false);
			}
			else {
				retval = modelElement.nearest(Scriptable.class);
			}
		}

		return retval;
	}

}