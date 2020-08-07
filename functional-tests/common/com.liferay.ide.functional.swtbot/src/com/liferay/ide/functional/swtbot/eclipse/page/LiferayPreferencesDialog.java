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

package com.liferay.ide.functional.swtbot.eclipse.page;

import com.liferay.ide.functional.swtbot.page.Button;
import com.liferay.ide.functional.swtbot.page.CLabel;
import com.liferay.ide.functional.swtbot.page.Dialog;
import com.liferay.ide.functional.swtbot.page.Label;
import com.liferay.ide.functional.swtbot.page.Tree;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author Rui Wang
 */
public class LiferayPreferencesDialog extends Dialog {

	public LiferayPreferencesDialog(SWTBot bot) {
		super(bot);

		_liferayPreferences = new Tree(bot);
	}

	public Label getClearAllDoNotShowAgainSettingsAndShowAllHiddenDialogsAgain() {
		return new Label(bot, CLEAR_ALL_DO_NOT_SHOW_AGAIN_SETTINGS_AND_SHOW_ALL_HIDDEN_DIALOGS_AGAIN);
	}

	public Button getClearBtn() {
		return new Button(getShell().bot(), CLEAR);
	}

	public CLabel getLiferay() {
		return new CLabel(bot, LIFERAY);
	}

	public CLabel getMessageDialogs() {
		return new CLabel(bot, MESSAGE_DIALOGS);
	}

	public SWTBotTreeItem getTreeItem(String items) {
		return _liferayPreferences.getTreeItem(items);
	}

	private Tree _liferayPreferences;

}