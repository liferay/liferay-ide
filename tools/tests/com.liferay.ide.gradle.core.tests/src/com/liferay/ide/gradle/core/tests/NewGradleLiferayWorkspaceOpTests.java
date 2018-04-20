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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.project.core.workspace.BaseLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;

import java.io.File;

import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Haoyi Sun
 */
public class NewGradleLiferayWorkspaceOpTests {

	@Before
	public void clearWorkspace() throws Exception {
		for (IProject project : CoreUtil.getAllProjects()) {
			project.delete(true, new NullProgressMonitor());
		}
	}

	@Test
	public void testNewGradleLiferayWorkspaceOpWithBundle71() throws Exception {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		String projectName = "test-liferay-workspace-gradle";

		IWorkspaceRoot workspaceRoot = CoreUtil.getWorkspaceRoot();

		IPath workspaceLocation = workspaceRoot.getLocation();

		op.setProjectProvider("gradle-liferay-workspace");
		op.setLocation(workspaceLocation.toPortableString());
		op.setWorkspaceName(projectName);
		op.setLiferayVersion("7.1");

		Status status = op.execute(new ProgressMonitor());

		Assert.assertNotNull(status);
		Assert.assertEquals("OK", status.message());

		IPath wsPath = workspaceLocation.append(projectName);

		String wsLocation = wsPath.toPortableString();

		File wsFile = new File(wsLocation);

		File propertiesFile = new File(wsFile, "gradle.properties");

		Assert.assertNotNull(propertiesFile);

		Properties prop = PropertiesUtil.loadProperties(propertiesFile);

		String wsBundleUrl = (String)prop.get("liferay.workspace.bundle.url");

		Assert.assertEquals(wsBundleUrl, BaseLiferayWorkspaceOp.LIFERAY_71_BUNDLE_URL);
	}

}