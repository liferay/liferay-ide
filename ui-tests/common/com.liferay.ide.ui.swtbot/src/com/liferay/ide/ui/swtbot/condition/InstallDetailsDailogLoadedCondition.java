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

package com.liferay.ide.ui.swtbot.condition;

import com.liferay.ide.ui.swtbot.UI;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;

/**
 * @author Lily Li
 */
public class InstallDetailsDailogLoadedCondition implements ICondition, UI {

	public InstallDetailsDailogLoadedCondition(SWTBot bot) {
		_bot = bot;
	}

	@Override
	public String getFailureMessage() {
		return "Install Details Dailog is still loading";
	}

	@Override
	public void init(SWTBot bot) {
	}

	@Override
	public boolean test() throws Exception {
		SWTBotButton installDetailNextButton = _bot.button(NEXT_WITH_BRACKET);

		return installDetailNextButton.isEnabled();
	}

	private final SWTBot _bot;

}