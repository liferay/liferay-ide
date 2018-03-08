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

import com.liferay.ide.ui.fragment.tests.base.NewFragmentWizardGradleBase;
import com.liferay.ide.ui.liferay.support.server.PureTomcat70Support;
import com.liferay.ide.ui.liferay.support.server.Tomcat7xSupport;
import com.liferay.ide.ui.liferay.util.RuleUtil;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Vicky Wang
 * @author Sunny Shi
 * @author Lily Li
 * @author Ying Xu
 */
public class NewFragmentWizardGradleTomcat70Tests extends NewFragmentWizardGradleBase {

	public static PureTomcat70Support tomcat = new PureTomcat70Support(bot);

	@ClassRule
	public static RuleChain chain = RuleUtil.getRuleChain(tomcat, new Tomcat7xSupport(bot, tomcat));

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

	@Test
	public void createFragmentWithResourceAction() {
		super.createFragmentWithResourceAction();
	}

	@Test
	public void createFragmentWithWholeFiles() {
		super.createFragmentWithWholeFiles();
	}

}