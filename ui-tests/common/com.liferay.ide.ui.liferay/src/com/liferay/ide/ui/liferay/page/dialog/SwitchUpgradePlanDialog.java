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

package com.liferay.ide.ui.liferay.page.dialog;

import com.liferay.ide.ui.swtbot.page.Button;
import com.liferay.ide.ui.swtbot.page.Dialog;
import com.liferay.ide.ui.swtbot.page.Table;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Lily Li
 */
public class SwitchUpgradePlanDialog extends Dialog {

	public SwitchUpgradePlanDialog(SWTBot bot) {
		super(bot);

		_closeBtn = new Button(bot, CLOSE);
		_removePlanBtn = new Button(bot, REMOVE_PLAN);
		_startPlanBtn = new Button(bot, START_PLAN);
		_upgradePlanTable = new Table(bot);
	}

	public void clickClose() {
		getCloseBtn().click();
	}

	public void clickRemovePlan() {
		removePlanBtn().click();
	}

	public void clickStartPlan() {
		startPlanBtn().click();
	}

	public void doubleClick(String upgradePlanName) {
		_upgradePlanTable.doubleClick(upgradePlanRow(upgradePlanName));
	}

	public Button getCloseBtn() {
		return _closeBtn;
	}

	public Button removePlanBtn() {
		return _removePlanBtn;
	}

	public void select(String upgradePlanName) {
		_upgradePlanTable.select(upgradePlanRow(upgradePlanName));
	}

	public Button startPlanBtn() {
		return _startPlanBtn;
	}

	public String[] upgradePlanMessage(String upgradePlanName) {
		String[] upgradePlanMessage = new String[_upgradePlanTable.columnCount() - 1];

		for (int i = 1; i < _upgradePlanTable.columnCount(); i++) {
			upgradePlanMessage[i - 1] = _upgradePlanTable.getText(upgradePlanRow(upgradePlanName), i);
		}

		return upgradePlanMessage;
	}

	public int upgradePlanRow(String itemText) {
		return _upgradePlanTable.searchText(itemText);
	}

	private Button _closeBtn;
	private Button _removePlanBtn;
	private Button _startPlanBtn;
	private Table _upgradePlanTable;

}