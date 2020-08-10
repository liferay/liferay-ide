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

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Rui Wang
 */
public class UpgradeProblemsPreferencesDialog extends Dialog {

	public UpgradeProblemsPreferencesDialog(SWTBot bot) {
		super(bot);
	}

	public Button getRemoveBtn() {
		return new Button(getShell().bot(), REMOVE);
	}

	public CLabel getUpgradeProblemsForIgnoreBreakingChangeProblems() {
		return new CLabel(
			getShell().bot(),
			"These are ignored breaking change problems.\n" +
				"You can remove them if you want to show this type of problem next time.");
	}

}