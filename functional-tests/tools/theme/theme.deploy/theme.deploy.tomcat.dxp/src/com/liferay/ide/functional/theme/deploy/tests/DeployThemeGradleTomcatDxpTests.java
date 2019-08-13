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

package com.liferay.ide.functional.theme.deploy.tests;

import com.liferay.ide.functional.liferay.support.server.PureTomcatDxpSupport;
import com.liferay.ide.functional.liferay.support.server.ServerSupport;
import com.liferay.ide.functional.liferay.util.RuleUtil;
import com.liferay.ide.functional.theme.deploy.base.DeployThemeGradleTomcat7xBase;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Lily Li
 */
public class DeployThemeGradleTomcatDxpTests extends DeployThemeGradleTomcat7xBase {

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat7xRunningRuleChain(bot, getServer());

	public static ServerSupport getServer() {
		if (PureTomcatDxpSupport.isNot(server)) {
			server = new PureTomcatDxpSupport(bot);
		}

		return server;
	}

	@Test
	public void deployTheme() {
		super.deployTheme();
	}

	@Override
	protected String getVersion() {
		return "7.1";
	}

}