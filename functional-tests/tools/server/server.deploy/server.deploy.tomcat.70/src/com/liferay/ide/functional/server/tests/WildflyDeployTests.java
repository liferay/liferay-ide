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

import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceGradle70Support;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceSupport;
import com.liferay.ide.functional.server.deploy.base.Wildfly7xDeployBase;

import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Rui Wang
 */
public class WildflyDeployTests extends Wildfly7xDeployBase {

	@ClassRule
	public static LiferayWorkspaceGradle70Support liferayWorkspace = new LiferayWorkspaceGradle70Support(bot);

	@Test
	public void deployModule() {
		super.deployModule();
	}

	@Test
	public void deployWar() {
		super.deployWar();
	}

	@Override
	protected LiferayWorkspaceSupport getLiferayWorkspace() {
		return liferayWorkspace;
	}

}