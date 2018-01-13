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

import org.eclipse.sapphire.ui.forms.PropertyEditorPart;
import org.eclipse.sapphire.ui.forms.swt.PropertyEditorPresentation;
import org.eclipse.sapphire.ui.forms.swt.PropertyEditorPresentationFactory;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;

/**
 * @author Gregory Amerson
 */
public class ColorScriptPropertyEditorRenderer extends ScriptPropertyEditorRenderer {

	public ColorScriptPropertyEditorRenderer(PropertyEditorPart part, SwtPresentation parent, Composite composite) {
		super(part, parent, composite);
	}

	public static final class Factory extends PropertyEditorPresentationFactory {

		@Override
		public PropertyEditorPresentation create(PropertyEditorPart part, SwtPresentation parent, Composite composite) {
			return new ColorScriptPropertyEditorRenderer(part, parent, composite);
		}

	}

	@Override
	protected IEditorPart createEditorPart(ScriptPropertyEditorInput editorInput, IEditorSite editorSite) {
		IEditorPart editorPart = null;

		try {
			editorPart = new TextEditor();

			editorPart.init(editorSite, editorInput);
		}
		catch (PartInitException e) {
			KaleoUI.logError("Could not initialize color editor", e);

			try {
				editorPart = new TextEditor();

				editorPart.init(editorSite, editorInput);
			}
			catch (PartInitException e1) {
			}
		}

		return editorPart;
	}

}