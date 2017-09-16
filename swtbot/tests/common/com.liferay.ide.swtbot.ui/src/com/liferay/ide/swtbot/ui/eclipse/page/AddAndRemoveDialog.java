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
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.TreeItem;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Li Lu
 * @author Terry Jia
 */
public class AddAndRemoveDialog extends Dialog {

	public AddAndRemoveDialog(SWTWorkbenchBot bot) {
		super(bot, ADD_AND_REMOVE, CANCEL, FINISH);

		_addBtn = new Button(bot, ADD_WITH_BRACKET);
		_addAllBtn = new Button(bot, ADD_ALL);
		_removeBtn = new Button(bot, REMOVE_PROJECT);
		_removeAllBtn = new Button(bot, REMOVE_ALL);
		_availables = new Tree(bot, 0);
		_configureds = new Tree(bot, 1);
	}

	public void add(String... projectItemNames) {
		for (String projectItemName : projectItemNames) {
			TreeItem projectTree = new TreeItem(bot, _availables, projectItemName);

			projectTree.select();

			_addBtn.click();
		}
	}

	public Button getAddAllBtn() {
		return _addAllBtn;
	}

	public Button getAddBtn() {
		return _addBtn;
	}

	public Tree getAvailables() {
		return _availables;
	}

	public Tree getConfigureds() {
		return _configureds;
	}

	public Button getRemoveAllBtn() {
		return _removeAllBtn;
	}

	public Button getRemoveBtn() {
		return _removeBtn;
	}

	public void remove(String... projectItemNames) {
		for (String projectItemName : projectItemNames) {
			TreeItem projectTree = new TreeItem(bot, _configureds, projectItemName);

			projectTree.select();

			_removeBtn.click();
		}
	}

	private Button _addAllBtn;
	private Button _addBtn;
	private Tree _availables;
	private Tree _configureds;
	private Button _removeAllBtn;
	private Button _removeBtn;

}