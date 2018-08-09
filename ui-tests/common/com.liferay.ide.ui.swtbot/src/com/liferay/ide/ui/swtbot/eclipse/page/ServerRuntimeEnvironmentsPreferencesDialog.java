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

package com.liferay.ide.ui.swtbot.eclipse.page;

import com.liferay.ide.ui.swtbot.page.Button;
import com.liferay.ide.ui.swtbot.page.Dialog;
import com.liferay.ide.ui.swtbot.page.Table;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 */
public class ServerRuntimeEnvironmentsPreferencesDialog extends Dialog {

	public ServerRuntimeEnvironmentsPreferencesDialog(SWTBot bot) {
		super(bot, PREFERENCES);
	}

	public void clickAddBtn() {
		getAddBtn().click();
	}

	public void clickRemoveBtn() {
		getRemoveBtn().click();
	}
	
	public void clickRuntime(int row) {
		getRuntimes().click(row);
	}

	public void clickRuntime(String runtime) {
		getRuntimes().click(runtime);
	}
	
	public Button getAddBtn() {
		return new Button(getShell().bot(), ADD_WITH_DOT);
	}

	public Button getEditBtn() {
		return new Button(getShell().bot(), EDIT_WITH_DOT);
	}

	public Button getRemoveBtn() {
		return new Button(getShell().bot(), REMOVE);
	}
	
	public Table getRuntimes() {
		return new Table(getShell().bot(), SERVER_RUNTIEME_ENVIRONMENTS);
	}

}