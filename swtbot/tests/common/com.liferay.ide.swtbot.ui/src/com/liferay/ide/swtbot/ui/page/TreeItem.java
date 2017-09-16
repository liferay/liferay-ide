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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author Li Lu
 */
public class TreeItem extends AbstractWidget {

	public TreeItem(SWTWorkbenchBot bot) {
		super(bot);
	}

	public TreeItem(SWTWorkbenchBot bot, Tree tree, String... nodeText) {
		super(bot);

		_tree = tree;
		this.nodeText = nodeText;
	}

	public void collapse() {
		getWidget().collapse();
	}

	public void doAction(String... actions) {
		SWTBotMenu menu = getWidget().contextMenu(actions[0]);

		SWTBotMenu goalMenu = menu.click();

		for (int i = 1; i < actions.length; i++) {
			goalMenu = goalMenu.menu(actions[i]).click();
		}
	}

	public void doubleClick() {
		getWidget().doubleClick();
	}

	public void doubleClick(String node) {
		SWTBotTreeItem treeItem = getWidget().getNode(node);

		treeItem.doubleClick();
	}

	public void expand() {
		getWidget().expand();
	}

	public void expandAll(SWTBotTreeItem... node) {
		SWTBotTreeItem treeItem = getWidget().expand();

		SWTBotTreeItem[] subnodes = treeItem.getItems();

		if (subnodes != null) {
			for (SWTBotTreeItem subnode : subnodes) {
				if (subnode.getText().contains("JRE")) {
					continue;
				}

				treeItem = subnode.expand();

				expandAll(subnode);
			}
		}
	}

	public void expandNode(String... nodes) {
		getWidget().expandNode(nodes);
	}

	public String[] getAllItems() {
		expand();

		SWTBotTreeItem[] items = getWidget().getItems();

		String[] nodes = new String[items.length];

		for (int i = 0; i < items.length; i++) {
			nodes[i] = items[i].getText();
		}

		return nodes;
	}

	public TreeItem getTreeItem(String... items) {
		String[] fullNodeText = new String[nodeText.length + items.length];

		System.arraycopy(nodeText, 0, fullNodeText, 0, nodeText.length);
		System.arraycopy(items, 0, fullNodeText, nodeText.length, items.length);

		return new TreeItem(bot, _tree, fullNodeText);
	}

	public boolean isEnabled() {
		return getWidget().isEnabled();
	}

	public boolean isExpanded() {
		return getWidget().isExpanded();
	}

	public boolean isSelected() {
		return getWidget().isSelected();
	}

	public boolean isVisible() {
		return getWidget().isVisible();
	}

	public void select() {
		getWidget().select();
	}

	public void selectMulty(String... items) {
		getWidget().select(items);
	}

	public void selectTreeItem(String... items) {
		SWTBotTreeItem treeItem = getWidget();

		for (int i = 0; i < nodeText.length; i++) {
			treeItem.expand();

			treeItem = treeItem.getNode(nodeText[i]);
		}

		treeItem.select();
	}

	@Override
	protected SWTBotTreeItem getWidget() {
		SWTBotTreeItem treeItem = null;

		if (nodeText != null) {
			treeItem = _tree.getWidget().getTreeItem(nodeText[0]);
		}

		for (int i = 1; i < nodeText.length; i++) {
			treeItem.expand();

			treeItem = treeItem.getNode(nodeText[i]);
		}

		return treeItem;
	}

	protected String[] nodeText;

	private Tree _tree;

}