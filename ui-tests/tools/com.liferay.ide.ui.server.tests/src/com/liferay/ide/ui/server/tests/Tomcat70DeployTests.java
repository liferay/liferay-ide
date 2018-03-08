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

package com.liferay.ide.ui.server.tests;

import com.liferay.ide.ui.liferay.support.sdk.SdkSupport;
import com.liferay.ide.ui.liferay.support.server.PureTomcat70Support;
import com.liferay.ide.ui.liferay.support.server.ServerRunningSupport;
import com.liferay.ide.ui.liferay.support.server.ServerSupport;
import com.liferay.ide.ui.liferay.support.server.Tomcat7xSupport;
import com.liferay.ide.ui.liferay.util.RuleUtil;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Terry Jia
 */
public class Tomcat70DeployTests extends TomcatDeployBase {

	@ClassRule
	public static RuleChain chain = RuleUtil.getRuleChain(
		getServer(), new Tomcat7xSupport(bot, getServer()), new SdkSupport(bot, getServer()),
		new ServerRunningSupport(bot, getServer()));

	public static ServerSupport getServer() {
		if ((server == null) || !(server instanceof PureTomcat70Support)) {
			server = new PureTomcat70Support(bot);
		}

		return server;
	}

	@Test
	public void deployFragment() {
		super.deployFragment();
	}

	@Test
	public void deployModule() {
		super.deployModule();
	}

	@Test
	public void deployPluginPortlet() {
		super.deployPluginPortlet();
	}

	@Test
	public void deployWar() {
		super.deployWar();
	}

}