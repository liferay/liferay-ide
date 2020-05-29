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

package com.liferay.ide.functional.portlet.tests;

import com.liferay.ide.functional.liferay.support.server.PureTomcat70Support;
import com.liferay.ide.functional.liferay.support.server.ServerSupport;
import com.liferay.ide.functional.liferay.util.RuleUtil;
import com.liferay.ide.functional.portlet.deploy.base.DeployPortletGradleTomcat7xBase;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Terry Jia
 * @author Rui Wang
 */
@Ignore("ignore because blade 3.10.0 does not support the creation of gradle standalone")
public class DeployPortletGradleTomcat70Tests extends DeployPortletGradleTomcat7xBase {

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat7xRunningRuleChain(bot, getServer());

	public static ServerSupport getServer() {
		if (PureTomcat70Support.isNot(server)) {
			server = new PureTomcat70Support(bot);
		}

		return server;
	}

	@Ignore("ignore because blade 3.10.0 remove freemarker")
	@Test
	public void deployFreemarkerPortlet() {
		super.deployFreemarkerPortlet();
	}

	@Override
	protected String getVersion() {
		return "7.0";
	}

}