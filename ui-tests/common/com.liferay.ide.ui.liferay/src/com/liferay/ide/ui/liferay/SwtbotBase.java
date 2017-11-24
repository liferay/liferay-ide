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
import com.liferay.ide.ui.liferay.action.ViewAction;
import com.liferay.ide.ui.liferay.action.WizardAction;
import com.liferay.ide.ui.liferay.page.LiferayIDE;
import com.liferay.ide.ui.swtbot.Keys;
import com.liferay.ide.ui.swtbot.UI;
import com.liferay.ide.ui.swtbot.util.CoreUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferenceConstants;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Vicky Wang
 * @author Ying Xu
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class SwtbotBase implements UI, Keys, Messages, FileConstants {

	public static SWTWorkbenchBot bot;
	public static BrowserAction browserAction;
	public static DialogAction dialogAction;
	public static EditorAction editorAction;
	public static EnvAction envAction;
	public static LiferayIDE ide;
	public static JobAction jobAction;
	public static ViewAction viewAction;
	public static WizardAction wizardAction;

	@AfterClass
	public static void afterClass() {
		envAction.resetTimestamp();

		String[] projectNames = viewAction.getProjectNames();

		if (projectNames.length > 0) {
			System.out.println(
				"The following projects are unable to be deleted, some error may happened, try by core's IProject:");

			for (String projectName : projectNames) {
				System.out.println(projectName);
			}

			for (String projectName : projectNames) {
				IProject project = CoreUtil.getProject(projectName);

				try {
					project.delete(true, new NullProgressMonitor());
				}
				catch (CoreException ce) {
				}
			}
		}
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();

		ide = new LiferayIDE(bot);

		dialogAction = new DialogAction(bot);
		envAction = new EnvAction(bot);
		editorAction = new EditorAction(bot);
		wizardAction = new WizardAction(bot);
		viewAction = new ViewAction(bot);
		jobAction = new JobAction(bot);
		browserAction = new BrowserAction(bot);

		try {
			long origin = SWTBotPreferences.TIMEOUT;

			SWTBotPreferences.TIMEOUT = 1000;

			ide.getWelcomeView().close();

			SWTBotPreferences.TIMEOUT = origin;
		}
		catch (Exception e) {
		}

		ide.getLiferayWorkspacePerspective().activate();

		SWTBotPreferences.TIMEOUT = 30 * 1000;

		System.setProperty(SWTBotPreferenceConstants.KEY_TIMEOUT, "30000");
		System.setProperty(SWTBotPreferenceConstants.KEY_DEFAULT_POLL_DELAY, "5000");

		SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
	}

}