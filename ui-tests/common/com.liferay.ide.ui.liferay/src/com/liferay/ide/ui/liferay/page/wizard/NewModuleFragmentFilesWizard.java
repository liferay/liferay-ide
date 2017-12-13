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

package com.liferay.ide.ui.liferay.page.wizard;

import com.liferay.ide.ui.swtbot.page.ComboBox;
import com.liferay.ide.ui.swtbot.page.Text;
import com.liferay.ide.ui.swtbot.page.ToolbarButtonWithTooltip;
import com.liferay.ide.ui.swtbot.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ying Xu
 */
public class NewModuleFragmentFilesWizard extends Wizard {

	public NewModuleFragmentFilesWizard(SWTBot bot) {
		super(bot, 2);
	}

	public ToolbarButtonWithTooltip getAddOverrideFilesBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), ADD_FILES_FROM_OSGI_TO_OVERRIDE);
	}

	public ToolbarButtonWithTooltip getDeleteBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), DELETE);
	}

	public Text getHostOsgiBundle() {
		return new Text(getShell().bot(), HOST_OSGI_BUNDLE);
	}

	public ComboBox getLiferyRuntimes() {
		return new ComboBox(getShell().bot(), LIFERAY_RUNTIME_NAME);
	}

	public ToolbarButtonWithTooltip getNewRuntimeBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), NEW_LIFERAY_RUNTIME);
	}

	public Text getProjectName() {
		return new Text(getShell().bot(), PROJECT_NAME);
	}

}