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

package com.liferay.ide.kaleo.ui.helpers;

import com.liferay.ide.kaleo.ui.AbstractKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.editor.ScriptPropertyEditorInput;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.editors.text.TextEditor;

/**
 * @author Gregory Amerson
 */
public class ColorScriptEditorHelper extends AbstractKaleoEditorHelper {

	@Override
	public IEditorPart createEditorPart(ScriptPropertyEditorInput editorInput, IEditorSite editorSite) {
		IEditorPart editorPart = null;

		try {
			editorPart = new TextEditor();

			editorPart.init(editorSite, editorInput);
		}
		catch (Exception e) {
			KaleoUI.logError("Could not create color syntax editor.", e);

			editorPart = super.createEditorPart(editorInput, editorSite);
		}

		return editorPart;
	}

}