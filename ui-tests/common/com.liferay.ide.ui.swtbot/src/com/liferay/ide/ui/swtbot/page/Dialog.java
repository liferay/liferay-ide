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

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class Dialog extends Shell {

	public Dialog(SWTBot bot) {
		super(bot);
	}

	public Dialog(SWTBot bot, String label) {
		super(bot, label);
	}

	public Dialog(SWTBot bot, String cancelBtnLabel, String confirmBtnLabel) {
		super(bot);

		_cancelBtnLabel = cancelBtnLabel;
		_confirmBtnLabel = confirmBtnLabel;
	}

	public Dialog(SWTBot bot, String label, String cancelBtnLabel, String confirmBtnLabel) {
		super(bot, label);

		_cancelBtnLabel = cancelBtnLabel;
		_confirmBtnLabel = confirmBtnLabel;
	}

	public void cancel() {
		clickBtn(cancelBtn());
	}

	public Button cancelBtn() {
		return new Button(bot, _cancelBtnLabel);
	}

	public void close() {
		clickBtn(closeBtn());
	}

	public Button closeBtn() {
		return new Button(bot, _closeBtnLabel);
	}

	public void confirm() {
		clickBtn(confirmBtn());
	}

	public void confirm(String confirmLabel) {
		clickBtn(new Button(bot, confirmLabel));
	}

	public Button confirmBtn() {
		return new Button(bot, _confirmBtnLabel);
	}

	private String _cancelBtnLabel = CANCEL;
	private String _closeBtnLabel = CLOSE;
	private String _confirmBtnLabel = OK;

}