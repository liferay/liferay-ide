/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.project.core.tests.workspace;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.core.workspace.WorkspaceConstants;
import com.liferay.ide.project.core.tests.ProjectCoreBase;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOpMethods;

/**
 * @author Ethan Sun
 */
public class NewLiferayMavenWorkspaceOpTests extends ProjectCoreBase
{

    @BeforeClass
    public static void removeAllProjects() throws Exception
    {
        IProgressMonitor monitor = new NullProgressMonitor();

        for( IProject project : CoreUtil.getAllProjects() )
        {
            project.delete( true, monitor );

            assertFalse( project.exists() );
        }
    }

    @AfterClass
    public static void removeWorkspaceProjects() throws Exception {
		IProject mavenWorkspaceProject = CoreUtil.getProject( "test-liferay-maven-workspace-new" );

		mavenWorkspaceProject.delete(true, null);
    }

    @Test
    public void testNewLiferayMavenWorkspaceOp() throws Exception
    {
		String projectName = "test-liferay-maven-workspace-new";

		IPath workspaceLocation = CoreUtil.getWorkspaceRoot().getLocation();

		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();
		op.setProjectProvider("maven-liferay-workspace");
		op.setWorkspaceName(projectName);
		op.setUseDefaultLocation(false);
		op.setLocation(workspaceLocation.toPortableString());
		op.setLiferayVersion("7.1");
		op.setTargetPlatform("7.1.2");

		waitForBuildAndValidation();

		NewLiferayWorkspaceOpMethods.execute(op, ProgressMonitorBridge.create(new NullProgressMonitor()));

		waitForBuildAndValidation();

		String wsLocation = workspaceLocation.append(projectName).toPortableString();

		File wsFile = new File(wsLocation);

		assertTrue(wsFile.exists());

		assertTrue(LiferayWorkspaceUtil.isValidWorkspaceLocation(wsLocation));

		String bundleUrlProperty = LiferayWorkspaceUtil.getMavenProperty(wsLocation,
				WorkspaceConstants.BUNDLE_URL_PROPERTY, "");

		assertTrue(bundleUrlProperty.equals(
				"https://releases-cdn.liferay.com/portal/7.1.2-ga3/liferay-ce-portal-tomcat-7.1.2-ga3-20190107144105508.tar.gz"));
    }
}
