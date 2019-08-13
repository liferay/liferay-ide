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

package com.liferay.ide.functional.liferay.page.dialog;

import com.liferay.ide.functional.swtbot.page.Button;
import com.liferay.ide.functional.swtbot.page.Dialog;
import com.liferay.ide.functional.swtbot.page.Text;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 */
public class AddEventActionDialog extends Dialog {

	public AddEventActionDialog(SWTBot bot) {
		super(bot);

		_event = new Text(bot, EVENT);
		_eventActionClass = new Text(bot, CLASS);
		_selectEventBtn = new Button(bot, SELECT, 0);
		_selectClassBtn = new Button(bot, SELECT, 1);
		_newBtn = new Button(bot, NEW);
	}

	public Text getEvent() {
		return _event;
	}

	public Text getEventActionClass() {
		return _eventActionClass;
	}

	public Button getNewBtn() {
		return _newBtn;
	}

	public Button getSelectClassBtn() {
		return _selectClassBtn;
	}

	public Button getSelectEventBtn() {
		return _selectEventBtn;
	}

	private Text _event;
	private Text _eventActionClass;
	private Button _newBtn;
	private Button _selectClassBtn;
	private Button _selectEventBtn;

}