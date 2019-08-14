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
public class AddServiceWrapperDialog extends Dialog {

	public AddServiceWrapperDialog(SWTBot bot) {
		super(bot);

		_serviceType = new Text(bot, SERVICE_TYPE);
		_implClass = new Text(bot, IMPL_CLASS);
		_selectServiceTypeBtn = new Button(bot, SELECT);
		_selectImplClassBtn = new Button(bot, SELECT);
		_newBtn = new Button(bot, NEW_WITH_DOT);
	}

	public Text getImplClass() {
		return _implClass;
	}

	public Button getNewBtn() {
		return _newBtn;
	}

	public Button getSelectImplClass(int index) {
		return _selectImplClassBtn;
	}

	public Button getSelectServiceType() {
		return _selectServiceTypeBtn;
	}

	public Text getServiceType() {
		return _serviceType;
	}

	private Text _implClass;
	private Button _newBtn;
	private Button _selectImplClassBtn;
	private Button _selectServiceTypeBtn;
	private Text _serviceType;

}