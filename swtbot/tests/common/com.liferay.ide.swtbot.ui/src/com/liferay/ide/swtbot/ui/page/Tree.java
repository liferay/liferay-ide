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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author Li Lu
 * @author Ashley Yuan
 */
public class Tree extends AbstractWidget {

	public Tree(SWTWorkbenchBot bot) {
		super(bot);
	}

	public Tree(SWTWorkbenchBot bot, int index) {
		super(bot, index);
	}

	public TreeItem expandNode(String... nodes) {
		getWidget().expandNode(nodes);

		return new TreeItem(bot, this, nodes);
	}

	public String[] getAllItems() {
		SWTBotTreeItem[] items = getWidget().getAllItems();

		String[] nodes = new String[items.length];

		for (int i = 0; i < items.length; i++) {
			nodes[i] = items[i].getText();
		}

		return nodes;
	}

	public SWTBotTreeItem[] getAllTreeItems() {
		SWTBotTreeItem[] items = getWidget().getAllItems();

		return items;
	}

	public TreeItem getTreeItem(String nodeText) {
		return new TreeItem(bot, this, nodeText);
	}

	public boolean hasItems() {
		return getWidget().hasItems();
	}

	public boolean hasTreeItem(String... items) {
		try {
			SWTBotTreeItem treeItem = getWidget().getTreeItem(items[0]);

			for (int i = 1; i < items.length; i++) {
				treeItem.expand();
				treeItem = treeItem.getNode(items[i]).expand();
			}

			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	public void select(String... items) {
		getWidget().select(items);
	}

	public void selectTreeItem(String... items) {
		SWTBotTreeItem treeItem = getWidget().getTreeItem(items[0]);

		for (int i = 1; i < items.length; i++) {
			treeItem.expand();
			treeItem = treeItem.getNode(items[i]).expand();
		}

		treeItem.select();
	}

	public void unselect() {
		getWidget().unselect();
	}

	protected SWTBotTree getWidget() {
		if (hasIndex()) {
			return bot.tree(index);
		}

		if (!isLabelNull()) {
			return bot.treeWithLabel(label);
		}
		else {
			return bot.tree();
		}
	}

}