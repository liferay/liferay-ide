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
import com.liferay.ide.functional.swtbot.page.Text;
import com.liferay.ide.functional.swtbot.page.ToolbarButtonWithTooltip;
import com.liferay.ide.functional.swtbot.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Li Lu
 * @author Ying Xu
 */
public class LiferayProjectFromExistSourceWizard extends Wizard {

	public LiferayProjectFromExistSourceWizard(SWTBot bot) {
		super(bot, 2);
	}

	public ToolbarButtonWithTooltip getBrowseSdkDirectoryBtn() {
		return new ToolbarButtonWithTooltip(bot, BROWSE);
	}

	public Button getDeselectAllBtn() {
		return new Button(getShell().bot(), DESELECT_ALL);
	}

	public Button getRefreshBtn() {
		return new Button(getShell().bot(), REFRESH);
	}

	public Text getSdkDirectory() {
		return new Text(getShell().bot(), SDK_DIRECTORY);
	}

	public Text getSdkVersion() {
		return new Text(getShell().bot(), SDK_VERSION);
	}

	public Button getSelectAllBtn() {
		return new Button(getShell().bot(), SELECT_ALL);
	}

}