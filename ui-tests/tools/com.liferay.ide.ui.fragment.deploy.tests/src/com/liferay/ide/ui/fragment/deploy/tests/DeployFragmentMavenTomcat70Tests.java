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

package com.liferay.ide.ui.fragment.deploy.tests;

import com.liferay.ide.ui.fragment.deploy.tests.base.FragmentTomcat7xMavenDeployBase;
import com.liferay.ide.ui.liferay.support.server.PureTomcat70Support;
import com.liferay.ide.ui.liferay.support.server.ServerSupport;
import com.liferay.ide.ui.liferay.util.RuleUtil;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Terry Jia
 */
public class DeployFragmentMavenTomcat70Tests extends FragmentTomcat7xMavenDeployBase {

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat7xRunningRuleChain(bot, getServer());

	public static ServerSupport getServer() {
		if ((server == null) || !(server instanceof PureTomcat70Support)) {
			server = new PureTomcat70Support(bot);
		}

		return server;
	}

	@Test
	public void deployFragmentWithJsp() {
		super.deployFragmentWithJsp();
	}

}
