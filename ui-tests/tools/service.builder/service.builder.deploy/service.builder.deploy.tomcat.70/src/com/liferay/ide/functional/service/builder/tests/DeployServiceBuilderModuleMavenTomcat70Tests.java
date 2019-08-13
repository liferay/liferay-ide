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

package com.liferay.ide.functional.service.builder.tests;

import com.liferay.ide.functional.service.builder.deploy.base.DeployServiceBuilderModuleMavenTomcat7xBase;
import com.liferay.ide.ui.liferay.support.server.PureTomcat70Support;
import com.liferay.ide.ui.liferay.support.server.ServerSupport;
import com.liferay.ide.ui.liferay.util.RuleUtil;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Ying Xu
 */
public class DeployServiceBuilderModuleMavenTomcat70Tests extends DeployServiceBuilderModuleMavenTomcat7xBase {

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat7xRunningRuleChain(bot, getServer());

	public static ServerSupport getServer() {
		if (PureTomcat70Support.isNot(server)) {
			server = new PureTomcat70Support(bot);
		}

		return server;
	}

	@Ignore("ignore as unstable need more research")
	@Test
	public void deployServiceBuilder() {
		super.deployServiceBuilder();
	}

}