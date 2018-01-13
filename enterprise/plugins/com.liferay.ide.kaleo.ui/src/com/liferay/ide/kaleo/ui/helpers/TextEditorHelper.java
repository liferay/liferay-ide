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
import com.liferay.ide.kaleo.ui.editor.WorkflowTextEditor;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;

/**
 * @author Gregory Amerson
 */
public class TextEditorHelper extends AbstractKaleoEditorHelper {

	@Override
	public IEditorPart createEditorPart(ScriptPropertyEditorInput editorInput, IEditorSite editorSite) {
		IEditorPart editorPart = null;

		try {
			editorPart = new WorkflowTextEditor();

			editorPart.init(editorSite, editorInput);
		}
		catch (Exception e) {
			KaleoUI.logError("Could not create text editor.", e);

			editorPart = super.createEditorPart(editorInput, editorSite);
		}

		return editorPart;
	}

}