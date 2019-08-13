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

package com.liferay.ide.functional.jsf.tests;

import com.liferay.ide.functional.jsf.deploy.base.DeployJsfGradleTomcat7xBase;
import com.liferay.ide.functional.liferay.support.server.PureTomcatDxpSupport;
import com.liferay.ide.functional.liferay.support.server.ServerSupport;
import com.liferay.ide.functional.liferay.util.RuleUtil;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Lily Li
 * @author Ashley Yuan
 */
public class DeployJsfGradleTomcatDxpTests extends DeployJsfGradleTomcat7xBase {

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat7xRunningRuleChain(bot, getServer());

	public static ServerSupport getServer() {
		if (PureTomcatDxpSupport.isNot(server)) {
			server = new PureTomcatDxpSupport(bot);
		}

		return server;
	}

	@Test
	public void deployICEFaces() {
		super.deployICEFaces();
	}

	@Test
	public void deployJSFStandard() {
		super.deployJSFStandard();
	}

	@Test
	public void deployLiferayFacesAlloy() {
		super.deployLiferayFacesAlloy();
	}

	@Test
	public void deployPrimeFaces() {
		super.deployPrimeFaces();
	}

	@Test
	public void deployRichFaces() {
		super.deployRichFaces();
	}

}