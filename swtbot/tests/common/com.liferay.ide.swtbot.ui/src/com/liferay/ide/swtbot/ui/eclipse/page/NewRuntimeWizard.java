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

package com.liferay.ide.swtbot.ui.eclipse.page;

import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class NewRuntimeWizard extends Wizard {

	public NewRuntimeWizard(SWTWorkbenchBot bot) {
		super(bot, NEW_SERVER_RUNTIME_ENVIRONMENT, 3);

		_search = new Text(bot);
		_serverTypes = new Tree(bot);
	}

	public Text getSearch() {
		return _search;
	}

	public Tree getServerTypes() {
		return _serverTypes;
	}

	private Text _search;
	private Tree _serverTypes;

}