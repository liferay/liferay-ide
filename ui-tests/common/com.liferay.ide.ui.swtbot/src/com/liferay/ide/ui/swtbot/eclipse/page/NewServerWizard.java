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

package com.liferay.ide.ui.swtbot.eclipse.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.ui.swtbot.page.ComboBox;
import com.liferay.ide.ui.swtbot.page.Text;
import com.liferay.ide.ui.swtbot.page.Tree;
import com.liferay.ide.ui.swtbot.page.Wizard;

/**
 * @author Vicky Wang
 * @author Ying Xu
 * @author Simon Jiang
 */
public class NewServerWizard extends Wizard {

	public NewServerWizard(SWTBot bot) {
		super(bot, 3);
	}

	public Text getServerHostName() {
		return new Text(getShell().bot(), SERVERS_HOST_NAME);
	}

	public Text getServerName() {
		return new Text(getShell().bot(), SERVER_NAME);
	}

	public Tree getServerTypes() {
		return new Tree(getShell().bot());
	}

	public ComboBox getServerRuntimeTypes() {
		return new ComboBox(getShell().bot(), SERVER_RUNTIEME_ENVIRONMENT);
	}
}