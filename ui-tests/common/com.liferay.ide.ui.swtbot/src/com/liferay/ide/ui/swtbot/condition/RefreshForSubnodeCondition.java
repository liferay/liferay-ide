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

package com.liferay.ide.ui.swtbot.condition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author Terry Jia
 */
public class RefreshForSubnodeCondition implements ICondition {

	public RefreshForSubnodeCondition(SWTBotTreeItem parent, String subnode, String refreshText) {
		_parent = parent;
		_subnode = subnode;
		_refreshText = refreshText;
	}

	public String getFailureMessage() {
		return _parent.getText() + "'s sub node " + _subnode + " not found after refresh";
	}

	public void init(SWTBot bot) {
	}

	public boolean test() throws Exception {
		boolean found = false;

		for (String itemText : _parent.getNodes()) {
			if (itemText.equals(_subnode)) {
				found = true;

				break;
			}
		}

		if (!found) {
			SWTBotMenu parentContextMenu = _parent.contextMenu(_refreshText);

			SWTBotTreeItem parentSelect = _parent.select();

			parentContextMenu.click();

			_parent = parentSelect.expand();
		}

		return found;
	}

	private SWTBotTreeItem _parent;
	private String _refreshText;
	private String _subnode;

}