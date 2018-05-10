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
public class WorkflowXmlEditor extends Editor {

	public WorkflowXmlEditor(SWTWorkbenchBot bot) {
		super(bot);

		_diagram = new CTabItem(bot, DIAGRAM);
		_source = new CTabItem(bot, SOURCE);
	}

	public WorkflowXmlEditor(SWTWorkbenchBot bot, String editorName) {
		super(bot, editorName);

		_diagram = new CTabItem(bot, DIAGRAM);
		_source = new CTabItem(bot, SOURCE);
	}

	public CTabItem getDiagramTab() {
		return _diagram;
	}

	public CTabItem getSourceTab() {
		return _source;
	}

	private CTabItem _diagram;
	private CTabItem _source;

}