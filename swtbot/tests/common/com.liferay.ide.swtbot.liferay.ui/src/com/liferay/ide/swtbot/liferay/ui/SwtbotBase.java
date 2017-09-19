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

package com.liferay.ide.swtbot.liferay.ui;

import com.liferay.ide.swtbot.liferay.ui.action.DialogAction;
import com.liferay.ide.swtbot.liferay.ui.action.EnvAction;
import com.liferay.ide.swtbot.liferay.ui.action.ViewAction;
import com.liferay.ide.swtbot.liferay.ui.action.WizardAction;
import com.liferay.ide.swtbot.liferay.ui.page.LiferayIDE;
import com.liferay.ide.swtbot.liferay.ui.util.CSVReader;
import com.liferay.ide.swtbot.liferay.ui.util.ValidationMsg;
import com.liferay.ide.swtbot.ui.Keys;
import com.liferay.ide.swtbot.ui.UI;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferenceConstants;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import org.junit.AfterClass;
import org.junit.Assert;
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
	public static DialogAction dialogAction;
	public static EnvAction envAction;
	public static boolean hasAddedProject = false;
	public static LiferayIDE ide;
	public static ViewAction viewAction;
	public static WizardAction wizardAction;

	@AfterClass
	public static void afterClass() {
		viewAction.deleteProject("init-project");
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();

		ide = new LiferayIDE(bot);

		wizardAction = new WizardAction(bot);
		viewAction = new ViewAction(bot);
		dialogAction = new DialogAction(bot);
		envAction = new EnvAction(bot);

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

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle("init-project");

		wizardAction.finishToWait();
	}

	public boolean addedProjects() {
		ide.showPackageExporerView();

		return ide.hasProjects();
	}

	public void openFile(String path) throws Exception {
		Display.getDefault().syncExec(
			new Runnable() {

				public void run() {
					try {
						File fileToOpen = new File(path);

						if (fileToOpen.exists() && fileToOpen.isFile()) {
							IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
							IWorkbenchPage page = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getPages()[0];

							IDE.openInternalEditorOnFileStore(page, fileStore);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}

			});
	}

	protected static ValidationMsg[] getValidationMsgs(File csv) {
		Assert.assertTrue(csv.exists());

		String[][] msgs = CSVReader.readCSV(csv);

		ValidationMsg[] validationMsgs = new ValidationMsg[msgs.length];

		for (int i = 0; i < msgs.length; i++) {
			validationMsgs[i] = new ValidationMsg();

			String[] columns = msgs[i];

			for (int t = 0; t < columns.length; t++) {
				if (t == 0) {
					validationMsgs[i].setInput(columns[t]);
				}
				else if (t == 1) {
					validationMsgs[i].setExpect(columns[t]);
				}
			}
		}

		return validationMsgs;
	}

	protected static void sleep(long millis) {
		bot.sleep(millis);
	}

	protected void sleep() {
		sleep(_DEFAULT_SLEEP_MILLIS);
	}

	protected static String eclipseWorkspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();

	private static final long _DEFAULT_SLEEP_MILLIS = 1000;

}