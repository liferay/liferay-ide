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
 * @author Lily Li
 */
public class AddRepositoryDialog extends Dialog {

	public AddRepositoryDialog(SWTBot bot) {
		super(bot);

		_addBtn = new Button(bot, ADD);
		_confirmBtn = new Button(bot, OK);
		_getLocation = new Text(bot, LOCATION_WITH_COLON);
	}

	public Button addBtn() {
		return _addBtn;
	}

	public void clickAddBtn() {
		addBtn().click();
	}

	public void clickConfirmBtn() {
		confirmBtn().click();
	}

	public Button confirmBtn() {
		return _confirmBtn;
	}

	public Text getLocation() {
		return _getLocation;
	}

	public void setLocation(String location) {
		getLocation().setText(location);
	}

	private Button _addBtn;
	private Button _confirmBtn;
	private Text _getLocation;

}