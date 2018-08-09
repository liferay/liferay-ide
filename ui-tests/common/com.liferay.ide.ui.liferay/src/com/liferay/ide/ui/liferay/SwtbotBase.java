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

package com.liferay.ide.ui.liferay;

import com.liferay.ide.ui.liferay.action.BrowserAction;
import com.liferay.ide.ui.liferay.action.DialogAction;
import com.liferay.ide.ui.liferay.action.EditorAction;
import com.liferay.ide.ui.liferay.action.EnvAction;
import com.liferay.ide.ui.liferay.action.JobAction;
import com.liferay.ide.ui.liferay.action.ValidationAction;
import com.liferay.ide.ui.liferay.action.ViewAction;
import com.liferay.ide.ui.liferay.action.WizardAction;
import com.liferay.ide.ui.liferay.page.LiferayIDE;
import com.liferay.ide.ui.swtbot.Keys;
import com.liferay.ide.ui.swtbot.Times;
import com.liferay.ide.ui.swtbot.UI;
import com.liferay.ide.ui.swtbot.page.Perspective;
import com.liferay.ide.ui.swtbot.page.Shell;
import com.liferay.ide.ui.swtbot.page.View;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferenceConstants;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Vicky Wang
 * @author Ying Xu
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class SwtbotBase implements UI, Keys, Messages, FileConstants, Times {

	public static SWTWorkbenchBot bot = new SWTWorkbenchBot();
	public static BrowserAction browserAction;
	public static DialogAction dialogAction;
	public static EditorAction editorAction;
	public static EnvAction envAction;
	public static LiferayIDE ide;
	public static JobAction jobAction;
	public static ValidationAction validationAction;
	public static ViewAction viewAction;
	public static WizardAction wizardAction;

	@AfterClass
	public static void afterClass() {

		// envAction.resetTimestamp();

		// String[] projectNames = viewAction.getProjectNames();

		//

		// if (projectNames.length > 0) {
		// String msg = "The following projects are unable to be deleted, some error may
		// happened:";

		//

		// envAction.logWarn(msg);

		//

		// for (String projectName : projectNames) {
		// envAction.logWarn(projectName);
		// }
		// }

	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		ide = LiferayIDE.getInstance(bot);

		dialogAction = DialogAction.getInstance(bot);
		envAction = EnvAction.getInstance(bot);
		editorAction = EditorAction.getInstance(bot);
		wizardAction = WizardAction.getInstance(bot);
		viewAction = ViewAction.getInstance(bot);
		jobAction = JobAction.getInstance(bot);
		browserAction = BrowserAction.getInstance(bot);
		validationAction = ValidationAction.getInstance(bot);

		try {
			long origin = SWTBotPreferences.TIMEOUT;

			SWTBotPreferences.TIMEOUT = 1000;

			View welcomeView = ide.getWelcomeView();

			welcomeView.close();

			SWTBotPreferences.TIMEOUT = origin;
		}
		catch (Exception e) {
		}

		if (!envAction.isEclipse()) {
			Perspective liferayWsPerspective = ide.getLiferayWorkspacePerspective();

			liferayWsPerspective.activate();
		}

		SWTBotPreferences.TIMEOUT = 30 * 1000;

		System.setProperty(SWTBotPreferenceConstants.KEY_TIMEOUT, "30000");
		System.setProperty(SWTBotPreferenceConstants.KEY_DEFAULT_POLL_DELAY, "5000");

		SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";

		//ide.showErrorLogView();

		//viewAction.checkErrorLog();
	}

	@Before
	public void checkShell() {
		Shell shell = new Shell(bot);

		String title = shell.getLabel();

		Assert.assertEquals("Now under " + title + " but expect " + ide.getLabel(), ide.getLabel(), title);
	}

	@After
	public void closeFailsShell() {
		Shell shell = new Shell(bot);

		String title = shell.getLabel();

		if (!title.equals(ide.getLabel())) {
			_closeShell();
		}
	}

	private void _closeShell() {
		Shell shell = new Shell(bot);

		String title = shell.getLabel();

		dialogAction.cancel();

		String currentTitle = new Shell(bot).getLabel();

		Assert.assertNotEquals("Unable to close the shell " + title, title, currentTitle);

		if (!currentTitle.equals(ide.getLabel())) {
			_closeShell();
		}
	}

}