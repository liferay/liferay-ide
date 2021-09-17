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

import java.util.Arrays;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import org.junit.Assert;

/**
 * @author Li Lu
 * @author Ashley Yuan
 * @author Terry Jia
 */
public class Tree extends AbstractWidget {

	public Tree(SWTBot bot) {
		super(bot);
	}

	public Tree(SWTBot bot, int index) {
		super(bot, index);
	}

	public void contextMenu(boolean fuzzy, String menu, String... items) {
		SWTBotTreeItem item = null;

		StringBuffer sb = new StringBuffer();

		if (fuzzy) {
			SWTBotTreeItem[] treeItems = getWidget().getAllItems();

			for (int m = 0; m < items.length; m++) {
				String itemLabel = items[m];

				for (SWTBotTreeItem currentItem : treeItems) {
					currentItem.setFocus();

					sleep();

					String label = currentItem.getText();

					if (label.startsWith(itemLabel)) {
						item = currentItem;

						break;
					}
				}

				if (m != (items.length - 1)) {
					item.expand();

					treeItems = item.getItems();
				}
			}

			sb.append("Used fuzzy and expected tree is ");

			for (String treeItem : items) {
				sb.append(treeItem);

				sb.append(" ");
			}

			sb.append(" but current one is");

			for (SWTBotTreeItem treeItem : treeItems) {
				sb.append(treeItem.getText());

				sb.append(" ");
			}
		}
		else {
			int timeout = 60 * 1000;

			long current = System.currentTimeMillis();

			while (true) {
				if ((item != null) || (System.currentTimeMillis() > (current + timeout))) {
					break;
				}

				sleep();

				try {
					getWidget().setFocus();

					if (items.length > 1) {
						item = getWidget().expandNode(items);
					}
					else {
						item = getWidget().getTreeItem(items[0]);
					}
				}
				catch (Exception exception) {
				}
			}

			sb.append("Could not find expected tree node ");
			sb.append(items[items.length - 1]);
			sb.append(" after ");
			sb.append(timeout);
			sb.append(" millis, and the current tree is: ");

			for (SWTBotTreeItem treeItem : getWidget().getAllItems()) {
				sb.append(treeItem.getText());
				sb.append(" ");
			}
		}

		Assert.assertNotNull(sb.toString(), item);

		SWTBotMenu botMenu = item.contextMenu(menu);

		botMenu.click();
	}

	public void contextMenu(String menu, String... items) {
		contextMenu(false, menu, items);
	}

	public void doubleClick(String... items) {
		SWTBotTreeItem item = getWidget().expandNode(items);

		item.doubleClick();
	}

	public void expand(String... items) {
		getWidget().expandNode(items);
	}

	public String[] getItemLabels() {
		SWTBotTreeItem[] items = getWidget().getAllItems();

		String[] labels = new String[items.length];

		for (int i = 0; i < items.length; i++) {
			labels[i] = items[i].getText();
		}

		return labels;
	}

	public SWTBotTreeItem getTreeItem(String... items) {
		SWTBotTreeItem item = null;

		if (items.length > 1) {
			item = getWidget().expandNode(items);
		}
		else {
			item = getWidget().getTreeItem(items[0]);
		}

		return item;
	}

	public boolean isVisible(String... items) throws WidgetNotFoundException {
		int length = items.length;

		if (length == 1) {
			SWTBotTreeItem item = getWidget().getTreeItem(items[0]);

			return item.isVisible();
		}

		String[] parents = Arrays.copyOfRange(items, 0, length - 1);

		SWTBotTreeItem parent = getWidget().expandNode(parents);

		SWTBotTreeItem parentNode = parent.getNode(items[length - 1]);

		return parentNode.isVisible();
	}

	public boolean isVisibleStartsBy(String... items) throws WidgetNotFoundException {
		int length = items.length;

		if (length == 1) {
			SWTBotTreeItem[] treeItems = getWidget().getAllItems();

			for (SWTBotTreeItem item : treeItems) {
				String itemText = item.getText();

				if (itemText.startsWith(items[0]) && item.isVisible()) {
					return true;
				}
			}

			return false;
		}

		String[] parents = Arrays.copyOfRange(items, 0, length - 1);

		SWTBotTreeItem parent = getWidget().expandNode(parents);

		SWTBotTreeItem[] treeItems = parent.getItems();

		for (SWTBotTreeItem item : treeItems) {
			String itemText = item.getText();

			if (itemText.startsWith(items[length - 1]) && item.isVisible()) {
				return true;
			}
		}

		return false;
	}

	public void select(String... items) {
		getWidget().select(items);
	}

	public void selectTreeItem(String... items) {
		SWTBotTreeItem item = getWidget().getTreeItem(items[0]);

		for (int i = 1; i < items.length; i++) {
			item.expand();

			SWTBotTreeItem itemNode = item.getNode(items[i]);

			item = itemNode.expand();
		}

		item.select();
	}

	public int size() {
		return getWidget().columnCount();
	}

	protected SWTBotTree getWidget() {
		if (hasIndex()) {
			return bot.tree(index);
		}

		if (!isLabelNull()) {
			return bot.treeWithLabel(label);
		}

		return bot.tree();
	}

}