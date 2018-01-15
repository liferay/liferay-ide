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

package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;

/**
 * @author Gregory Amerson
 */
public class MessageKeyHyperlink implements IHyperlink {

	public MessageKeyHyperlink(IRegion region, IFile file, String key, int offset, int length) {
		_region = region;
		_file = file;
		_key = key;
		_offset = offset;
		_length = length;
	}

	public IRegion getHyperlinkRegion() {
		return _region;
	}

	public String getHyperlinkText() {
		return "Open '" + _key + "' in " + this._file.getName();
	}

	public String getTypeLabel() {
		return null;
	}

	public void open() {
		try {
			IEditorPart editorPart = IDE.openEditor(UIUtil.getActivePage(), _file, true);

			if (editorPart instanceof AbstractTextEditor) {
				AbstractTextEditor editor = (AbstractTextEditor)editorPart;

				editor.selectAndReveal(_offset, _length);
			}
		}
		catch (PartInitException pie) {
			PortletUIPlugin.logError("Could not open properties file " + this._file.getName(), pie);
		}
	}

	private IFile _file;
	private String _key;
	private int _length;
	private int _offset;
	private IRegion _region;

}