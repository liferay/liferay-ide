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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ComboBox extends AbstractWidget {

	public ComboBox(SWTWorkbenchBot bot, String label) {
		super(bot, label);
	}

	public String[] items() {
		return getWidget().items();
	}

	public void setSelection(String value) {
		getWidget().setSelection(value);
	}

	public void setText(String text) {
		getWidget().setText(text);
	}

	@Override
	protected SWTBotCombo getWidget() {
		return bot.comboBoxWithLabel(label);
	}

}