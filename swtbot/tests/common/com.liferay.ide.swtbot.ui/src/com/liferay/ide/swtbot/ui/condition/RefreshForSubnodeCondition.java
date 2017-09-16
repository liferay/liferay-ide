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

package com.liferay.ide.swtbot.ui.condition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author Terry Jia
 */
public class RefreshForSubnodeCondition implements ICondition {

	public RefreshForSubnodeCondition(SWTBotTreeItem parentItem, String subnodeText, String refreshContextMenuText) {
		_itsParentItem = parentItem;

		_itsSubnodeText = subnodeText;

		_itsRefreshContextMenuText = refreshContextMenuText;
	}

	public String getFailureMessage() {
		return "sub node " + _itsSubnodeText + " not found after refresh";
	}

	public void init(SWTBot bot) {
	}

	public boolean test() throws Exception {
		boolean ret = false;

		for (String itemText : _itsParentItem.getNodes()) {
			if (itemText.equals(_itsSubnodeText)) {
				ret = true;

				break;
			}
		}

		if (!ret) {
			_itsParentItem.contextMenu(_itsRefreshContextMenuText).click();

			_itsParentItem = _itsParentItem.select().expand();
		}

		return ret;
	}

	private SWTBotTreeItem _itsParentItem;
	private String _itsRefreshContextMenuText;
	private String _itsSubnodeText;

}