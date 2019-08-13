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
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public abstract class AbstractWidget extends BasePageObject {

	public AbstractWidget(SWTBot bot) {
		super(bot);
	}

	public AbstractWidget(SWTBot bot, int index) {
		super(bot, index);
	}

	public AbstractWidget(SWTBot bot, String label) {
		super(bot, label);
	}

	public AbstractWidget(SWTBot bot, String label, int index) {
		super(bot, label, index);
	}

	public void contextMenu(String menu) {
		SWTBotMenu botMenu = getWidget().contextMenu(menu);

		botMenu.click();
	}

	public String getLabel() {
		return label;
	}

	public String getText() {
		return getWidget().getText();
	}

	public boolean isActive() {
		return getWidget().isActive();
	}

	public boolean isEnabled() {
		return getWidget().isEnabled();
	}

	public boolean isVisible() {
		return getWidget().isVisible();
	}

	public void setFocus() {
		getWidget().setFocus();
	}

	protected abstract AbstractSWTBot<?> getWidget();

}