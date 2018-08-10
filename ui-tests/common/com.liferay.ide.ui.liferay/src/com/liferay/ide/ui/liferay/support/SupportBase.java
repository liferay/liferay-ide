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

package com.liferay.ide.ui.liferay.support;

import com.liferay.ide.ui.liferay.action.BrowserAction;
import com.liferay.ide.ui.liferay.action.DialogAction;
import com.liferay.ide.ui.liferay.action.EditorAction;
import com.liferay.ide.ui.liferay.action.EnvAction;
import com.liferay.ide.ui.liferay.action.JobAction;
import com.liferay.ide.ui.liferay.action.ViewAction;
import com.liferay.ide.ui.liferay.action.WizardAction;
import com.liferay.ide.ui.liferay.page.LiferayIDE;
import com.liferay.ide.ui.swtbot.page.Perspective;
import com.liferay.ide.ui.swtbot.page.View;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferenceConstants;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;

import org.junit.rules.ExternalResource;

/**
 * @author Terry Jia
 */
public class SupportBase extends ExternalResource {

	public SupportBase(SWTWorkbenchBot bot) {
		ide = LiferayIDE.getInstance(bot);

		dialogAction = DialogAction.getInstance(bot);
		envAction = EnvAction.getInstance(bot);
		editorAction = EditorAction.getInstance(bot);
		wizardAction = WizardAction.getInstance(bot);
		viewAction = ViewAction.getInstance(bot);
		jobAction = JobAction.getInstance(bot);
		browserAction = BrowserAction.getInstance(bot);
	}

	@Override
	public void after() {
		super.after();

		timestamp = 0;
	}

	public void before() {
		try {
			long origin = SWTBotPreferences.TIMEOUT;

			SWTBotPreferences.TIMEOUT = 1000;

			View welcomeView = ide.getWelcomeView();

			welcomeView.close();

			SWTBotPreferences.TIMEOUT = origin;
		}
		catch (Exception e) {
		}

		Perspective liferayWsPerspective = ide.getLiferayWorkspacePerspective();

		liferayWsPerspective.activate();

		SWTBotPreferences.TIMEOUT = 30 * 1000;

		System.setProperty(SWTBotPreferenceConstants.KEY_TIMEOUT, "30000");
		System.setProperty(SWTBotPreferenceConstants.KEY_DEFAULT_POLL_DELAY, "5000");

		SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";

		String tt = String.valueOf(System.currentTimeMillis());

		try {
			timestamp = Long.parseLong(tt.substring(6));
		}
		catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}

	public BrowserAction browserAction;
	public DialogAction dialogAction;
	public EditorAction editorAction;
	public EnvAction envAction;
	public LiferayIDE ide;
	public JobAction jobAction;
	public ViewAction viewAction;
	public WizardAction wizardAction;

	protected long timestamp = 0;

}