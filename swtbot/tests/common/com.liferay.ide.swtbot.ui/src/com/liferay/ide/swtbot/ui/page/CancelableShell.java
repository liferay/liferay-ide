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

package com.liferay.ide.swtbot.ui.page;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class CancelableShell extends Shell {

	public CancelableShell(SWTWorkbenchBot bot) {
		super(bot);
	}

	public CancelableShell(SWTWorkbenchBot bot, String label) {
		super(bot, label);
	}

	public CancelableShell(SWTWorkbenchBot bot, String label, String cancelBtnLabel) {
		super(bot, label);

		_cancelBtnLabel = cancelBtnLabel;
	}

	public void cancel() {
		clickBtn(cancelBtn());
	}

	public Button cancelBtn() {
		return new Button(bot, _cancelBtnLabel);
	}

	private String _cancelBtnLabel = CANCEL;

}