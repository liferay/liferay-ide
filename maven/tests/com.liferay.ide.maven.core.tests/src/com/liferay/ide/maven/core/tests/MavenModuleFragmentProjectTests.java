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

package com.liferay.ide.maven.core.tests;

import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.tests.TestUtil;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOp;
import com.liferay.ide.project.core.modules.fragment.OverrideFilePath;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOpMethods;
import com.liferay.ide.server.core.tests.ServerCoreBase;
import com.liferay.ide.server.util.ServerUtil;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Joye Luo
 */
public class MavenModuleFragmentProjectTests extends ServerCoreBase {

	@Override
	public void setupRuntime() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		extractRuntime(getLiferayRuntimeZip(), getLiferayRuntimeDir());
	}

	@Test
	public void testNewModuleFragmentProjectOpProject() throws Exception {
		NewModuleFragmentOp op = NewModuleFragmentOp.TYPE.instantiate();
		String runtimeName = "test-maven-liferay-workspace";
		NullProgressMonitor npm = new NullProgressMonitor();

		IRuntime runtime = ServerCore.findRuntime(runtimeName);

		if (runtime == null) {
			IRuntimeWorkingCopy runtimeWC = ServerCore.findRuntimeType(getRuntimeId()).createRuntime(runtimeName, npm);

			runtimeWC.setName(runtimeName);
			runtimeWC.setLocation(getLiferayRuntimeDir());

			runtime = runtimeWC.save(true, npm);
		}

		Assert.assertNotNull(runtime);

		List<String> bundles = ServerUtil.getModuleFileListFrom70Server(runtime);

		Assert.assertNotNull(bundles);

		for (String hostOsgiBundle : bundles) {
			if (hostOsgiBundle.contains("com.liferay.login.web")) {
				op.setProjectName("test-maven-module-fragment");
				op.setProjectProvider("maven-module-fragment");
				op.setLiferayRuntimeName(runtimeName);
				op.setHostOsgiBundle(hostOsgiBundle);

				OverrideFilePath file = op.getOverrideFiles().insert();

				file.setValue("META-INF/resources/login.jsp");

				IProject existedMavenProject = MavenTestUtil.create(op);

				Assert.assertNotNull(existedMavenProject);

				IFile pomFile = existedMavenProject.getFile("pom.xml");

				Assert.assertTrue(pomFile.exists());

				IFile overrideFile1 = existedMavenProject.getFile("src/main/resources/META-INF/resources/login.jsp");

				Assert.assertTrue(overrideFile1.exists());

				break;
			}
		}
	}

	@Test
	public void testNewModuleFragmentProjectOpProjectName() {
		NewModuleFragmentOp op = NewModuleFragmentOp.TYPE.instantiate();

		op.setProjectName("test-module-fragment");

		Status projectNameOkValidationStatus1 = op.getProjectName().validation();

		Assert.assertEquals("ok", projectNameOkValidationStatus1.message());

		op.setProjectName("#test-module-fragment");

		Status projectNameErrorValidationStatus = op.getProjectName().validation();

		Assert.assertEquals("The project name is invalid.", projectNameErrorValidationStatus.message());

		op.setProjectName("test_module_fragment");

		Status projectNameOkValidationStatus2 = op.getProjectName().validation();

		Assert.assertEquals("ok", projectNameOkValidationStatus2.message());
	}

    @BeforeClass
    public static void createLiferayWorkspaceProject() throws Exception {
        NewLiferayWorkspaceOp workspaceOp = NewLiferayWorkspaceOp.TYPE.instantiate();

        workspaceOp.setWorkspaceName( "test-maven-liferay-workspace" );
        workspaceOp.setUseDefaultLocation( true );
        workspaceOp.setProductVersion("portal-7.3-ga4");
        workspaceOp.setProvisionLiferayBundle(true);
        workspaceOp.setServerName("test-maven-liferay-workspace");
        
        TestUtil.waitForBuildAndValidation();

        if( workspaceOp.validation().ok() )
        {
            workspaceOp.setProjectProvider("maven-liferay-workspace");

            NewLiferayWorkspaceOpMethods.execute( workspaceOp, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        }

        IProject workspaceProject = CoreUtil.getProject( "test-maven-liferay-workspace" );

        assertTrue(workspaceProject != null);
        assertTrue(workspaceProject.exists());
    }

	@AfterClass
	public static void removeWorkspaceProjects() throws Exception {
		IProject workspaceProject = CoreUtil.getProject( "test-maven-liferay-workspace" );

		workspaceProject.delete(true, null);
	}
}