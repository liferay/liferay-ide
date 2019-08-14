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

package com.liferay.ide.functional.liferay.page.wizard;

import com.liferay.ide.functional.swtbot.page.Button;
import com.liferay.ide.functional.swtbot.page.Table;
import com.liferay.ide.functional.swtbot.page.Text;
import com.liferay.ide.functional.swtbot.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 */
public class LanguagePropertiesWizard extends Wizard {

	public LanguagePropertiesWizard(SWTBot bot) {
		super(bot, 0);
	}

	public Button getAddBtn() {
		return new Button(getShell().bot(), ADD_WITH_DOT);
	}

	public Button getBrowseBtn() {
		return new Button(getShell().bot(), BROWSE_WITH_DOT);
	}

	public Text getContentFolder() {
		return new Text(getShell().bot(), CONTENT_FOLDER);
	}

	public Button getEditBtn() {
		return new Button(getShell().bot(), EDIT_WITH_DOT);
	}

	public Table getLanguagePropertyFiles() {
		return new Table(getShell().bot(), LANGUAGE_PROPERTY_FILES);
	}

	public Button getRemoveBtn() {
		return new Button(getShell().bot(), REMOVE_WITH_DOT);
	}

}