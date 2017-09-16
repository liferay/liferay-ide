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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;

/**
 * @author Li Lu
 */
public class Table extends AbstractWidget {

	public Table(SWTWorkbenchBot bot) {
		super(bot);
	}

	public Table(SWTWorkbenchBot bot, int index) {
		super(bot, index);
	}

	public Table(SWTWorkbenchBot bot, String label) {
		super(bot, label);
	}

	public void click(int row) {
		SWTBotTableItem tableItem = getWidget().getTableItem(row);

		tableItem.click();
	}

	public void click(int row, int column) {
		getWidget().click(row, column);
	}

	public void click(String item) {
		SWTBotTableItem tableItem = getWidget().getTableItem(item);

		tableItem.click();
	}

	public boolean containsItem(String item) {
		return getWidget().containsItem(item);
	}

	public void doubleClick(int row, int column) {
		getWidget().doubleClick(row, column);
	}

	public void setText(int index, String text) {
		bot.text(index).setText(text);
	}

	protected SWTBotTable getWidget() {
		if (isLabelNull()) {
			return bot.table(index);
		}

		return bot.tableWithLabel(label, 0);
	}

}