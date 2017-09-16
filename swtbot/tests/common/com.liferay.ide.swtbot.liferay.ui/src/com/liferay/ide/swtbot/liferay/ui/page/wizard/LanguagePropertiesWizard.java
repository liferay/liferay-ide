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

package com.liferay.ide.swtbot.liferay.ui.page.wizard;

import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.Table;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Vicky Wang
 */
public class LanguagePropertiesWizard extends Wizard {

	public LanguagePropertiesWizard(SWTWorkbenchBot bot) {
		super(bot, NEW_LIFERAY_HOOK, 0);

		_contentFolder = new Text(bot, CONTENT_FOLDER);
		_languagePropertyFiles = new Table(bot, LANGUAGE_PROPERTY_FILES);
		_browseBtn = new Button(bot, BROWSE_WITH_DOT);
		_addBtn = new Button(bot, ADD_WITH_DOT);
		_editBtn = new Button(bot, EDIT_WITH_DOT);
		_removeBtn = new Button(bot, REMOVE_WITH_DOT);
	}

	public Button getAddBtn() {
		return _addBtn;
	}

	public Button getBrowseBtn() {
		return _browseBtn;
	}

	public Text getContentFolder() {
		return _contentFolder;
	}

	public Button getEditBtn() {
		return _editBtn;
	}

	public Table getLanguagePropertyFiles() {
		return _languagePropertyFiles;
	}

	public Button getRemoveBtn() {
		return _removeBtn;
	}

	private Button _addBtn;
	private Button _browseBtn;
	private Text _contentFolder;
	private Button _editBtn;
	private Table _languagePropertyFiles;
	private Button _removeBtn;

}