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
import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Table;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Vicky Wang
 */
public class CreateCustomJSPsWizard extends Wizard {

	public CreateCustomJSPsWizard(SWTWorkbenchBot bot) {
		super(bot, 3);

		_selectedProjects = new Text(bot, SELECTED_PROJECT);
		_webRootFolder = new Text(bot, WEB_ROOT_FOLDER);
		_customJSPfolder = new Text(bot, CUSTOM_JSP_FOLDER);
		_jspFilesToOverride = new Table(bot, JSP_FILES_TO_OVERRIDE);
		_browseBtn = new Button(bot, BROWSE_WITH_DOT);
		_addFromLiferayBtn = new Button(bot, ADD_FROM_LIFERAY);
		_addBtn = new Button(bot, ADD_WITH_DOT);
		_editBtn = new Button(bot, EDIT_WITH_DOT);
		_removeBtn = new Button(bot, REMOVE_WITH_DOT);
		_disableJspSyntaxValidation = new CheckBox(bot, DISABLE_JSP_SYNTAX);
	}

	public Button getAddBtn() {
		return _addBtn;
	}

	public Button getAddFromLiferayBtn() {
		return _addFromLiferayBtn;
	}

	public Button getBrowseBtn() {
		return _browseBtn;
	}

	public Text getCustomJSPfolder() {
		return _customJSPfolder;
	}

	public CheckBox getDisableJspSyntaxValidation() {
		return _disableJspSyntaxValidation;
	}

	public Button getEditBtn() {
		return _editBtn;
	}

	public Table getJspFilesToOverride() {
		return _jspFilesToOverride;
	}

	public Button getRemoveBtn() {
		return _removeBtn;
	}

	public Text getSelectedProject() {
		return _selectedProjects;
	}

	public Text getWebRootFolder() {
		return _webRootFolder;
	}

	private Button _addBtn;
	private Button _addFromLiferayBtn;
	private Button _browseBtn;
	private Text _customJSPfolder;
	private CheckBox _disableJspSyntaxValidation;
	private Button _editBtn;
	private Table _jspFilesToOverride;
	private Button _removeBtn;
	private Text _selectedProjects;
	private Text _webRootFolder;

}