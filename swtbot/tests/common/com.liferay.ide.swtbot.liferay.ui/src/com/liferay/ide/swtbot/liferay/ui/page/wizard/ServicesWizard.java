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

package com.liferay.ide.swtbot.liferay.ui.page.wizard;

import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Table;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Vicky Wang
 */
public class ServicesWizard extends Wizard {

	public ServicesWizard(SWTWorkbenchBot bot) {
		super(bot, 0);

		_addServiceDialog = new Dialog(bot, ADD_SERVICE);
		_addServiceWrapperDialog = new Dialog(bot, ADD_SERVICE_WRAPPER);
		_definePortalServices = new Table(bot, DEFINE_PORTAL_SERVICES_TO_EXTEND);
		_addBtn = new Button(bot, ADD_WITH_DOT);
		_editBtn = new Button(bot, EDIT_WITH_DOT);
		_removeBtn = new Button(bot, REMOVE_WITH_DOT);
	}

	public Button getAddBtn() {
		return _addBtn;
	}

	public Dialog getAddServiceDialog() {
		return _addServiceDialog;
	}

	public Dialog getAddServiceWrapperDialog() {
		return _addServiceWrapperDialog;
	}

	public Table getDefinePortalServices() {
		return _definePortalServices;
	}

	public Button getEditBtn() {
		return _editBtn;
	}

	public Button getRemoveBtn() {
		return _removeBtn;
	}

	private Button _addBtn;
	private Dialog _addServiceDialog;
	private Dialog _addServiceWrapperDialog;
	private Table _definePortalServices;
	private Button _editBtn;
	private Button _removeBtn;

}