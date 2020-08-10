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

package com.liferay.ide.functional.swtbot.eclipse.page;

import com.liferay.ide.functional.swtbot.page.ComboBox;
import com.liferay.ide.functional.swtbot.page.Dialog;
import com.liferay.ide.functional.swtbot.page.Label;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLink;

/**
 * @author Rui Wang
 */
public class KaleoWorkflowValidationPreferencesDialog extends Dialog {

	public KaleoWorkflowValidationPreferencesDialog(SWTBot bot) {
		super(bot);
	}

	public boolean getConfigureProjectSpecificSettings(String link) {
		SWTBotLink configureProjectSpecificSettings = bot.link(CONFIGURE_PROJECT_SPECIFIC_SETTINGS);

		return configureProjectSpecificSettings.isVisible();
	}

	public ComboBox getDefaultWorkflowValidationLogical() {
		return new ComboBox(bot, DEFAULT_WORKFLOW_VALIDATION_LOGICAL);
	}

	public Label getSelectTheSeverityLevelForTheFollowingValidationProblems() {
		return new Label(bot, SELECT_THE_SEVERITY_LEVEL_FOR_THE_FOLLOWING_VALIDATION_PROBLEMS);
	}

	public Label getWorkflowValidation() {
		return new Label(bot, WORKFLOW_VALIDATION);
	}

}