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

package com.liferay.ide.functional.swtbot.page;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

/**
 * @author Li Lu
 */
public class Table extends AbstractWidget {

	public Table(SWTBot bot) {
		super(bot);
	}

	public Table(SWTBot bot, int index) {
		super(bot, index);
	}

	public Table(SWTBot bot, String label) {
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

	public int columnCount() {
		return getWidget().columnCount();
	}

	public boolean containsItem(String item) {
		return getWidget().containsItem(item);
	}

	public void doubleClick(int row) {
		SWTBotTableItem tableItem = getWidget().getTableItem(row);

		tableItem.doubleClick();
	}

	public void doubleClick(int row, int column) {
		getWidget().doubleClick(row, column);
	}

	public String getItem(int row) {
		SWTBotTableItem tableItem = getWidget().getTableItem(row);

		return tableItem.toString();
	}

	public String getText(int row, int column) {
		SWTBotTableItem tableItem = getWidget().getTableItem(row);

		return tableItem.getText(column);
	}

	public int searchText(String itemText) {
		return getWidget().searchText(itemText);
	}

	public void select(int row) {
		getWidget().select(row);
	}

	public void setText(int index, String text) {
		SWTBotText content = bot.text(index);

		content.setText(text);
	}

	public int size() {
		return getWidget().rowCount();
	}

	protected SWTBotTable getWidget() {
		if (isLabelNull() && hasIndex()) {
			return bot.table(index);
		}
		else if (isLabelNull() && !hasIndex()) {
			return bot.table();
		}
		else {
			return bot.tableWithLabel(label, 0);
		}
	}

}