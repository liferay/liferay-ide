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

import com.liferay.ide.core.model.IBaseModel;
import com.liferay.ide.ui.form.IDEFormEditor;

import org.eclipse.ui.IFileEditorInput;

/**
 * @author Gregory Amerson
 */
public class PluginPackageInputContextManager extends InputContextManager {

	public PluginPackageInputContextManager(IDEFormEditor editor) {
		super(editor);
	}

	@Override
	public IBaseModel getModel() {
		IFileEditorInput editorInput = (IFileEditorInput)this.editor.getEditorInput();

		InputContext context = findContext(editorInput.getFile());

		if (context != null) {
			return context.getModel();
		}

		return null;
	}

}