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

package com.liferay.ide.ui.fragment.tests;

import com.liferay.ide.ui.fragment.base.NewFragmentWizardLiferayWorkspaceGradleBase;
import com.liferay.ide.ui.liferay.support.server.PureTomcat71Support;
import com.liferay.ide.ui.liferay.util.RuleUtil;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Terry Jia
 */
public class NewFragmentWizardLiferayWorkspaceGragleTomcat71Tests extends NewFragmentWizardLiferayWorkspaceGradleBase {

	public static PureTomcat71Support tomcat = new PureTomcat71Support(bot);

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat7xRuleChain(bot, tomcat);

	@Test
	public void createFragmentChangeModulesDir() {
		super.createFragmentChangeModulesDir();
	}

	@Test
	public void createFragmentWithJsp() {
		super.createFragmentWithJsp();
	}

	@Test
	public void createFragmentWithJspf() {
		super.createFragmentWithJspf();
	}

	@Test
	public void createFragmentWithoutFiles() {
		super.createFragmentWithoutFiles();
	}

	@Test
	public void createFragmentWithPortletProperites() {
		super.createFragmentWithPortletProperites();
	}

	@Ignore("ignore as unstable, need more research")
	@Test
	public void createFragmentWithResourceAction() {
		super.createFragmentWithResourceAction();
	}

	@Test
	public void createFragmentWithWholeFiles() {
		super.createFragmentWithWholeFiles();
	}

}