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
import com.liferay.ide.functional.swtbot.page.Dialog;
import com.liferay.ide.functional.swtbot.page.Tree;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Li Lu
 * @author Terry Jia
 */
public class AddAndRemoveDialog extends Dialog {

	public AddAndRemoveDialog(SWTBot bot) {
		super(bot, ADD_AND_REMOVE, CANCEL, FINISH);
	}

	public void clickAddBtn() {
		getAddBtn().click();
	}

	public Button getAddAllBtn() {
		return new Button(getShell().bot(), ADD_ALL);
	}

	public Button getAddBtn() {
		return new Button(getShell().bot(), ADD_WITH_BRACKET);
	}

	public Tree getAvailables() {
		return new Tree(getShell().bot(), 0);
	}

	public Tree getConfigureds() {
		return new Tree(getShell().bot(), 1);
	}

	public Button getRemoveAllBtn() {
		return new Button(getShell().bot(), REMOVE_ALL);
	}

	public Button getRemoveBtn() {
		return new Button(getShell().bot(), REMOVE_PROJECT);
	}

}