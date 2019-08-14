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
public class AddPortalPropertiesOverrideDialog extends Dialog {

	public AddPortalPropertiesOverrideDialog(SWTBot bot) {
		super(bot);

		_value = new Text(bot, VALUE);
		_property = new Text(bot, PROPERTY);
		_selectPropertyBtn = new Button(bot, SELECT);
	}

	public Text getProperty() {
		return _property;
	}

	public Button getSelectPropertyBtn() {
		return _selectPropertyBtn;
	}

	public Text getValue() {
		return _value;
	}

	private Text _property;
	private Button _selectPropertyBtn;
	private Text _value;

}