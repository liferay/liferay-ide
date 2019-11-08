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

package com.liferay.ide.functional.liferay.action;

import com.liferay.ide.functional.liferay.UIAction;
import com.liferay.ide.functional.liferay.page.editor.LayoutTplEditor;
import com.liferay.ide.functional.liferay.page.editor.PomXmlEditor;
import com.liferay.ide.functional.liferay.page.editor.ServerEditor;
import com.liferay.ide.functional.liferay.page.editor.ServiceXmlEditor;
import com.liferay.ide.functional.liferay.page.editor.WorkflowXmlEditor;
import com.liferay.ide.functional.swtbot.page.Editor;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Rui Wang
 */
public class EditorAction extends UIAction {

	public static EditorAction getInstance(SWTWorkbenchBot bot) {
		if (_editorAction == null) {
			_editorAction = new EditorAction(bot);
		}

		return _editorAction;
	}

	public void close() {
		_editor.close();
	}

	public void customizedText(String fileName, int line, int column, String text) {
		_editor.customizedText(fileName, line, column, text);
	}

	public String getContent() {
		return _editor.getText();
	}

	public void openContextMenu(String text) {
		_editor.contextMenu(text);
	}

	public void save() {
		_editor.save();
	}

	public void selectText(String text) {
		for (int i = 0; i < _editor.getLineCount(); i++) {
			List<String> lines = _editor.getLines();

			String line = lines.get(i);

			if (line.contains(text)) {
				int index = line.indexOf(text);

				_editor.selectRange(i, index, text.length());

				break;
			}
		}
	}

	public void setText(String text) {
		_editor.setText(text);
	}

	public KaleoWorkflowEditorAction kaleoWorkflow = new KaleoWorkflowEditorAction();
	public LayoutTplEditorAction layoutTpl = new LayoutTplEditorAction();
	public PomXmlEditorAction pomXml = new PomXmlEditorAction();
	public ServerEditorAction server = new ServerEditorAction();
	public ServiceXmlEditorAction serviceXml = new ServiceXmlEditorAction();

	public class KaleoWorkflowEditorAction {

		public void switchTabDiagram() {
			_workflowXmlEditor.clickDiagramTab();

			ide.sleep(2000);
		}

		public void switchTabSource() {
			_workflowXmlEditor.clickSourceTab();

			ide.sleep(2000);
		}

		private final WorkflowXmlEditor _workflowXmlEditor = new WorkflowXmlEditor(bot);

	}

	public class LayoutTplEditorAction {

		public void switchTabDesign() {
			_layoutTplEditor.clickDesignTab();

			ide.sleep(2000);
		}

		public void switchTabPreview() {
			_layoutTplEditor.clickPreviewTab();

			ide.sleep(2000);
		}

		public void switchTabSource() {
			_layoutTplEditor.clickSourceTab();

			ide.sleep(2000);
		}

		private final LayoutTplEditor _layoutTplEditor = new LayoutTplEditor(bot);

	}

	public class PomXmlEditorAction {

		public void switchTabPomXml() {
			_pomXmlEditor.clickPomXml();
		}

		private final PomXmlEditor _pomXmlEditor = new PomXmlEditor(bot);

	}

	public class ServerEditorAction {

		public void selectCustomLaunchSettings() {
			_serverEditor.clickCustomLaunchSettings();
		}

		public void selectDefaultLaunchSettings() {
			_serverEditor.clickDefalutLaunchSettings();
		}

		public void selectUseDeveloperMode() {
			_serverEditor.clickUseDeveloperMode();
		}

		public void setHttpPort(String port) {
			_serverEditor.setHttpPort(port);
		}

		public void setPassword(String password) {
			_serverEditor.setPassword(password);
		}

		private final ServerEditor _serverEditor = new ServerEditor(bot);

	}

	public class ServiceXmlEditorAction {

		public void switchTabDiagram() {
			_serviceXmlEditor.getDiagramTab();

			ide.sleep(2000);
		}

		public void switchTabOverview() {
			_serviceXmlEditor.clickOverviewTab();

			ide.sleep(2000);
		}

		public void switchTabSource() {
			_serviceXmlEditor.clickSourceTab();

			ide.sleep(2000);
		}

		private final ServiceXmlEditor _serviceXmlEditor = new ServiceXmlEditor(bot);

	}

	private EditorAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	private static EditorAction _editorAction;

	private final Editor _editor = new Editor(bot);

}