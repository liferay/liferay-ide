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

package com.liferay.ide.functional.layouttpl.tests;

import com.liferay.ide.functional.layouttpl.deploy.base.DeployLayoutTemplateModuleGradleTomcat7xBase;
import com.liferay.ide.functional.liferay.support.server.LiferaryWorkspaceTomcat71Support;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceGradle71Support;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceSupport;
import com.liferay.ide.functional.liferay.util.RuleUtil;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Ying Xu
 */
public class DeployLayoutTemplateModuleGradleTomcat71Tests extends DeployLayoutTemplateModuleGradleTomcat7xBase {

	public static LiferayWorkspaceGradle71Support workspace = new LiferayWorkspaceGradle71Support(bot);
	public static LiferaryWorkspaceTomcat71Support server = new LiferaryWorkspaceTomcat71Support(bot, workspace);

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat71RunningLiferayWorkspaceRuleChain(bot, workspace, server);

	@Test
	public void deployLayoutTemplate() {
		super.deployLayoutTemplate();
	}

	@Override
	protected LiferayWorkspaceSupport getLiferayWorkspace() {
		return workspace;
	}

	@Override
	protected String getServerName() {
		return server.getServerName();
	}

	@Override
	protected String getStartedLabel() {
		return server.getStartedLabel();
	}

}