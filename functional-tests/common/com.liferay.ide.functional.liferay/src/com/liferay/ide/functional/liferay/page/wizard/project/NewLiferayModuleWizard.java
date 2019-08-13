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

package com.liferay.ide.functional.liferay.page.wizard.project;

import com.liferay.ide.functional.swtbot.page.ComboBox;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ying Xu
 * @author Sunny Shi
 * @author Ashley Yuan
 */
public class NewLiferayModuleWizard extends NewProjectWizard {

	public NewLiferayModuleWizard(SWTBot bot) {
		super(bot, 2);
	}

	public ComboBox getProjectTemplates() {
		return new ComboBox(getShell().bot(), PROJECT_TEMPLATE_NAME);
	}

}