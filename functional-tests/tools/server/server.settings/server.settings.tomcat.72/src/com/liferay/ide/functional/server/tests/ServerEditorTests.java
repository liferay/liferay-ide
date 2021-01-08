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

package com.liferay.ide.functional.server.tests;

import com.liferay.ide.functional.liferay.support.server.PureTomcat72Support;
import com.liferay.ide.functional.liferay.util.RuleUtil;
import com.liferay.ide.functional.server.wizard.base.ServerTomcat7xBase;
import com.liferay.ide.functional.swtbot.util.StringPool;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Rui Wang
 */
public class ServerEditorTests extends ServerTomcat7xBase {

	public static PureTomcat72Support tomcat = new PureTomcat72Support(bot);

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat7xRuleChain(bot, tomcat);

	@Test
	public void checkInitialState() {
		viewAction.servers.openEditor(tomcat.getStoppedLabel());

		validationAction.assertTextEquals(tomcat.getServerName(), editorAction.server.getServerName());

		validationAction.assertTextEquals("localhost", editorAction.server.getHostName());

		validationAction.assertRadioFalse(editorAction.server.getDefaultLaunchSettings());

		validationAction.assertRadioTrue(editorAction.server.getCustomLaunchSettings());

		validationAction.assertTextEquals("-Xmx2560m", editorAction.server.getMemoryArgs());

		Assert.assertTrue(editorAction.server.getOpenLauchConfiguration(OPEN_LAUNCH_CONFIGURATION));

		validationAction.assertTextEquals(StringPool.BLANK, editorAction.server.getExternalProperties());

		validationAction.assertCheckedTrue(editorAction.server.getUseDeveloperMode());

		validationAction.assertTextEquals("test@liferay.com", editorAction.server.getUsername());

		validationAction.assertTextEquals(StringPool.BLANK, editorAction.server.getPassword());

		validationAction.assertTextEquals("8080", editorAction.server.getHttpPort());

		editorAction.close();
	}

	@Test
	public void checkLiferayLaunchTest() {
		viewAction.servers.openEditor(tomcat.getStoppedLabel());

		editorAction.server.setMemoryArgs("-Xmx5120m");

		editorAction.save();

		editorAction.close();

		jobAction.waitForNoRunningJobs();

		viewAction.servers.start(tomcat.getStoppedLabel());

		jobAction.waitForServerStarted(tomcat.getServerName());

		jobAction.waitForNoRunningJobs();

		jobAction.waitForConsoleContent(tomcat.getServerName(), "-Xmx5120m", M2);

		viewAction.servers.stop(tomcat.getStartedLabel());

		viewAction.servers.openEditor(tomcat.getStoppedLabel());

		editorAction.server.clickLiferayLaunchRestoreDefaults();

		editorAction.save();

		//validationAction.assertTextEquals("-Xmx2560m", editorAction.server.getMemoryArgs());

		editorAction.close();
	}

	@Test
	public void checkLiferayPortTest() {
		viewAction.servers.openEditor(tomcat.getStoppedLabel());

		editorAction.server.setHttpPort("9090");

		editorAction.save();

		editorAction.server.clickPortsRestoreDefaults();

		editorAction.close();
	}

}