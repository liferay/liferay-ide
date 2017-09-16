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

package com.liferay.ide.swtbot.ui.eclipse.page;

import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Table;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class ServerRuntimeEnvironmentsPreferencesDialog extends Dialog {

	public ServerRuntimeEnvironmentsPreferencesDialog(SWTWorkbenchBot bot) {
		super(bot);

		_runtimes = new Table(bot, SERVER_RUNTIEME_ENVIRONMENTS);
		_addBtn = new Button(bot, ADD_WITH_DOT);
		_editBtn = new Button(bot, EDIT_WITH_DOT);
		_removeBtn = new Button(bot, REMOVE);
	}

	public Button getAddBtn() {
		return _addBtn;
	}

	public Button getEditBtn() {
		return _editBtn;
	}

	public Button getRemoveBtn() {
		return _removeBtn;
	}

	public Table getRuntimes() {
		return _runtimes;
	}

	private Button _addBtn;
	private Button _editBtn;
	private Button _removeBtn;
	private Table _runtimes;

}