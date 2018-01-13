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

package com.liferay.ide.kaleo.ui.editor;

import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.helpers.KaleoPaletteHelper;

import org.eclipse.gef.ui.views.palette.PalettePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.editors.text.TextEditor;

/**
 * @author Gregory Amerson
 */
public class WorkflowTextEditor extends TextEditor {

	public static final String ID = "com.liferay.ide.kaleo.ui.editor.text";

	public WorkflowTextEditor() {
		ImageDescriptor entryImage = KaleoUI.imageDescriptorFromPlugin(KaleoUI.PLUGIN_ID, "icons/e16/text_16x16.gif");

		_paletteHelper = new KaleoPaletteHelper(this, KaleoUI.getDefault(), "palette/text", entryImage);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class required) {
		if (required == PalettePage.class) {
			return _paletteHelper.createPalettePage();
		}

		return super.getAdapter(required);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	private KaleoPaletteHelper _paletteHelper;

}