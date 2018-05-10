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

package com.liferay.ide.ui.liferay.page.editor;

import com.liferay.ide.ui.swtbot.page.CTabItem;
import com.liferay.ide.ui.swtbot.page.Editor;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Haoyi Sun
 */
public class LayoutTplEditor extends Editor {

	public LayoutTplEditor(SWTWorkbenchBot bot) {
		super(bot);

		_preview = new CTabItem(bot, PREVIEW);
		_design = new CTabItem(bot, DESIGN);
		_source = new CTabItem(bot, SOURCE);
	}

	public LayoutTplEditor(SWTWorkbenchBot bot, String editorName) {
		super(bot, editorName);

		_preview = new CTabItem(bot, PREVIEW);
		_design = new CTabItem(bot, DESIGN);
		_source = new CTabItem(bot, SOURCE);
	}

	public CTabItem getDesignTab() {
		return _design;
	}

	public CTabItem getPreviewTab() {
		return _preview;
	}

	public CTabItem getSourceTab() {
		return _source;
	}

	private CTabItem _design;
	private CTabItem _preview;
	private CTabItem _source;

}