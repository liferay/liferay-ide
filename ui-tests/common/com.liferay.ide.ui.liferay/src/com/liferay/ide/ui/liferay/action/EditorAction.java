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

package com.liferay.ide.ui.liferay.action;

import com.liferay.ide.ui.liferay.UIAction;
import com.liferay.ide.ui.liferay.page.editor.PomXmlEditor;
import com.liferay.ide.ui.liferay.page.editor.ServiceXmlEditor;
import com.liferay.ide.ui.swtbot.page.Editor;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class EditorAction extends UIAction {

	public EditorAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	public void close() {
		_editor.close();
	}

	public String getContent() {
		return _editor.getText();
	}

	public void save() {
		_editor.save();
	}

	public void setText(String text) {
		_editor.setText(text);
	}

	public void switchTabDiagramServiceXml() {
		_serviceXmlEditor.getDiagramTab();

		ide.sleep(2000);
	}

	public void switchTabOverViewServiceXml() {
		_serviceXmlEditor.getOverviewTab().click();

		ide.sleep(2000);
	}

	public void switchTabPomXml() {
		_pomXmlEditor.getPomXml().click();
	}

	public void switchTabSourceServiceXml() {
		_serviceXmlEditor.getSourceTab().click();

		ide.sleep(2000);
	}

	private final Editor _editor = new Editor(bot);
	private final PomXmlEditor _pomXmlEditor = new PomXmlEditor(bot);
	private final ServiceXmlEditor _serviceXmlEditor = new ServiceXmlEditor(bot);

}