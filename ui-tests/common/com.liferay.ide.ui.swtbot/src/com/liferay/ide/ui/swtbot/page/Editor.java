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

package com.liferay.ide.ui.swtbot.page;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class Editor extends AbstractPart {

	public Editor(SWTWorkbenchBot bot) {
		super(bot);
	}

	public Editor(SWTWorkbenchBot bot, String label) {
		super(bot, label);
	}

	public void customizedText(String fileName, int line, int column, String text) {
		SWTBotEditor fileEditor = ((SWTWorkbenchBot)bot).editorByTitle(fileName);

		SWTBotEclipseEditor fileContent = fileEditor.toTextEditor();

		fileContent.insertText(line, column, text);
	}

	public String getLabel() {
		if (isLabelNull()) {
			SWTBotEditor botActiveEditor = ((SWTWorkbenchBot)bot).activeEditor();

			return botActiveEditor.getTitle();
		}

		return label;
	}

	public String getText() {
		SWTBotEclipseEditor botEditor = getPart().toTextEditor();

		return botEditor.getText();
	}

	public void save() {
		getPart().save();
	}

	public void setText(String text) {
		SWTBotEclipseEditor botEditor = getPart().toTextEditor();

		botEditor.setText(text);
	}

	protected SWTBotEditor getPart() {
		if (isLabelNull()) {
			return ((SWTWorkbenchBot)bot).activeEditor();
		}

		return ((SWTWorkbenchBot)bot).editorByTitle(label);
	}

}