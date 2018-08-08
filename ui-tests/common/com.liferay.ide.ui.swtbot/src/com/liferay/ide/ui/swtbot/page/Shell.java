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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class Shell extends BasePageObject {

	public Shell(SWTBot bot) {
		super(bot);
	}

	public Shell(SWTBot bot, String label) {
		super(bot, label);
	}

	public void clickBtn(Button btn) {
		btn.click();
	}

	public void close() {
		getShell().close();
	}

	public String getLabel() {
		if (isLabelNull()) {
			SWTBotShell botActiveShell = bot.activeShell();

			assert botActiveShell != null;

			return botActiveShell.getText();
		}

		return label;
	}

	protected SWTBotShell getShell() {
		return bot.shell(getLabel());
	}

}