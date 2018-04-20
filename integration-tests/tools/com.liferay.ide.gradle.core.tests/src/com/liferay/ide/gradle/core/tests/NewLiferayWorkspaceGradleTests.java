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

package com.liferay.ide.gradle.core.tests;

import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.test.core.base.support.LiferayWorkspaceSupport;
import com.liferay.ide.test.project.core.base.NewProjectOpBase;

import org.eclipse.core.resources.IProject;

import org.junit.Rule;
import org.junit.Test;

/**
 * @author Andy Wu
 * @author Joye Luo
 */
public class NewLiferayWorkspaceGradleTests extends NewProjectOpBase<NewLiferayWorkspaceOp> {

	@Test
	public void testWorkspace() throws Exception {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		op.setWorkspaceName(workspace.getName());
		op.setProjectProvider(provider());

		createAndBuild(op, workspace.getName());

		deleteProject(workspace.getName());
	}

	@Rule
	public LiferayWorkspaceSupport workspace = new LiferayWorkspaceSupport();

	@Override
	protected String provider() {
		return "gradle-liferay-workspace";
	}

	protected void verifyProjectFiles(IProject project) {
		assertFileExists(project.getFile("build.gradle"));
		assertFileNotExists(project.getFile("pom.xml"));
	}

}