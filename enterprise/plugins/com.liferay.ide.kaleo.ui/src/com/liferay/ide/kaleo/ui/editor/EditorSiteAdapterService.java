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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.UniversalConversionService;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Gregory Amerson
 */
public class EditorSiteAdapterService extends UniversalConversionService {

	@Override
	public <A> A convert(Object object, Class<A> adapterType) {
		A retval = null;

		if (IEditorSite.class.equals(adapterType)) {
			SapphirePart sapphirePart = context(SapphirePart.class);

			Element localElement = sapphirePart.getLocalModelElement();

			ITextEditor editor = localElement.adapt(ITextEditor.class);

			IWorkbenchPartSite editorSite = editor.getSite();

			if (editorSite instanceof IEditorSite) {
				retval = adapterType.cast(editorSite);
			}
		}

		return retval;
	}

}