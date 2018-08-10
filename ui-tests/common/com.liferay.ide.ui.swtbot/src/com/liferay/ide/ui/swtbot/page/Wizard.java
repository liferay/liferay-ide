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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class Wizard extends CancelableShell {

	public Wizard(SWTBot bot) {
		super(bot);
	}

	public Wizard(SWTBot bot, int validationMsgIndex) {
		super(bot);

		_validationMsgIndex = validationMsgIndex;
	}

	public void back() {
		clickBtn(backBtn());
	}

	public Button backBtn() {
		return new Button(bot, _backBtnLabel);
	}

	public void finish() {
		clickBtn(finishBtn());
	}

	public Button finishBtn() {
		return new Button(bot, _finishBtnLabel);
	}

	public String getValidationMsg() {
		return getValidationMsg(_validationMsgIndex);
	}

	public String getValidationMsg(int validationMsgIndex) {
		if (validationMsgIndex < 0) {
			log.error("Validation Msg Index error");

			return null;
		}

		SWTBotText text = bot.text(validationMsgIndex);

		String s = text.getText();

		return s.trim();
	}

	public void next() {
		clickBtn(nextBtn());
	}

	public Button nextBtn() {
		return new Button(bot, _nextBtnLabel);
	}

	private String _backBtnLabel = BACK_WITH_LEFT_BRACKET;
	private String _finishBtnLabel = FINISH;
	private String _nextBtnLabel = NEXT_WITH_BRACKET;
	private int _validationMsgIndex = -1;

}