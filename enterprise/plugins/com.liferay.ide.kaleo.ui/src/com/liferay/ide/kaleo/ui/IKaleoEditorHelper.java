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

package com.liferay.ide.kaleo.ui;

import com.liferay.ide.kaleo.ui.editor.ScriptPropertyEditorInput;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;

/**
 * @author Gregory Amerson
 */
public interface IKaleoEditorHelper {

	public IEditorPart createEditorPart(ScriptPropertyEditorInput editorInput, IEditorSite editorSite);

	public String getContributorName();

	public String getEditorId();

	public String getFileExtension();

	public String getLanguageType();

	public void handleDropFromPalette(IEditorPart activeEditor);

	public void openEditor(ISapphirePart part, Element modelElement, ValueProperty valueProperty);

	public String EXTENSION_ID = "com.liferay.ide.kaleo.ui.editorHelpers";

	public String KALEO_TEMP_PREFIX = "._KALEO_TEMP_SCRIPT_";

}