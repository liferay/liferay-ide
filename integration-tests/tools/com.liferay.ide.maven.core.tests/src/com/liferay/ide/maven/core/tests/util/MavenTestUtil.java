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

package com.liferay.ide.maven.core.tests.util;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.modules.BaseModuleOp;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;

import org.apache.maven.model.Model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectImportResult;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.IProjectCreationListener;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.tests.common.FileHelpers;
import org.eclipse.m2e.tests.common.JobHelpers;
import org.eclipse.m2e.tests.common.WorkspaceHelpers;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

import org.junit.Assert;

/**
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class MavenTestUtil {

	public static void copyDir(File src, File dst) throws IOException {
		FileHelpers.copyDir(src, dst);
	}

	public static IProject create(BaseModuleOp op) throws CoreException, InterruptedException {
		Status status = op.execute(ProgressMonitorBridge.create(new NullProgressMonitor()));

		Assert.assertNotNull(status);

		Assert.assertTrue(status.message(), status.ok());

		waitForJobsToComplete();

		Value<String> projectName = op.getProjectName();

		return CoreUtil.getProject(projectName.content());
	}

	public static IProject createAndBuild(BaseModuleOp op) throws Exception {
		Status validation = op.validation();

		Assert.assertTrue(validation.message(), validation.ok());

		IProject project = create(op);

		verifyProject(project);

		return project;
	}

	public static IProject importProject(String pomLocation) throws CoreException, IOException {
		return importProject(pomLocation, new ResolverConfiguration());
	}

	public static IProject importProject(String pomLocation, ResolverConfiguration configuration)
		throws CoreException, IOException {

		return importProject(pomLocation, configuration, null);
	}

	public static IProject importProject(
			String pomLocation, ResolverConfiguration configuration, IProjectCreationListener listener)
		throws CoreException, IOException {

		File pomFile = new File(pomLocation);

		File parentFile = pomFile.getParentFile();

		return importProjects(
			parentFile.getCanonicalPath(), new String[] {pomFile.getName()}, configuration, false, listener)[0];
	}

	public static IProject[] importProjects(
			String basedir, String[] pomNames, ResolverConfiguration configuration, boolean skipSanityCheck,
			IProjectCreationListener listener)
		throws CoreException, IOException {

		MavenModelManager mavenModelManager = MavenPlugin.getMavenModelManager();

		IWorkspace workspace = CoreUtil.getWorkspace();

		File src = new File(basedir);

		File dst = new File(CoreUtil.getWorkspaceRootFile(), src.getName());

		copyDir(src, dst);

		ArrayList<MavenProjectInfo> projectInfos = new ArrayList<>();

		for (String pomName : pomNames) {
			File pomFile = new File(dst, pomName);

			Model model = mavenModelManager.readMavenModel(pomFile);

			MavenProjectInfo projectInfo = new MavenProjectInfo(pomName, pomFile, model, null);

			_setBasedirRename(projectInfo);

			projectInfos.add(projectInfo);
		}

		ProjectImportConfiguration importConfiguration = new ProjectImportConfiguration(configuration);

		ArrayList<IMavenProjectImportResult> importResults = new ArrayList<>();

		IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

		workspace.run(
			new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					importResults.addAll(
						projectConfigurationManager.importProjects(
							projectInfos, importConfiguration, listener, monitor));
				}

			},
			projectConfigurationManager.getRule(), IWorkspace.AVOID_UPDATE, _npm);

		IProject[] projects = new IProject[projectInfos.size()];

		for (int i = 0; i < projectInfos.size(); i++) {
			IMavenProjectImportResult importResult = importResults.get(i);

			Assert.assertEquals(projectInfos.get(i), importResult.getMavenProjectInfo());

			projects[i] = importResult.getProject();

			Assert.assertNotNull("Failed to import project " + projectInfos, projects[i]);

			if (!skipSanityCheck) {
				MavenProjectInfo projectInfo = projectInfos.get(i);

				IMavenProjectRegistry projectRegistry = MavenPlugin.getMavenProjectRegistry();

				IMavenProjectFacade facade = projectRegistry.create(projects[i], _npm);

				if (facade == null) {
					Model model = projectInfo.getModel();

					Assert.fail(
						"Project " + model.getGroupId() + "-" + model.getArtifactId() + "-" + model.getVersion() +
							" was not imported. Errors: " +
								WorkspaceHelpers.toString(WorkspaceHelpers.findErrorMarkers(projects[i])));
				}
			}
		}

		return projects;
	}

	public static void verifyProject(IProject project) throws Exception {
		IProgressMonitor monitor = new NullProgressMonitor();

		Assert.assertNotNull(project);
		Assert.assertTrue(project.exists());

		Assert.assertFalse(FileUtil.exists(project.getFile("build.gradle")));

		project.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);

		JobHelpers.waitForJobsToComplete(monitor);

		project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		JobHelpers.waitForJobsToComplete(monitor);

		WorkspaceHelpers.assertNoErrors(project);

		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

		Assert.assertNotNull(bundleProject);

		IPath outputBundle = bundleProject.getOutputBundle(true, monitor);

		Assert.assertNotNull(outputBundle);

		Assert.assertTrue(FileUtil.exists(outputBundle.toFile()));
	}

	public static void waitForJobsToComplete() throws CoreException, InterruptedException {
		JobHelpers.waitForJobs(
			job -> {
				Object property = job.getProperty(ILiferayProjectProvider.LIFERAY_PROJECT_JOB);

				if ((property != null) || job.belongsTo(LiferayCore.LIFERAY_JOB_FAMILY)) {
					return true;
				}

				return false;
			},
			30 * 60 * 1000);

		JobHelpers.waitForJobsToComplete();
	}

	private static void _setBasedirRename(MavenProjectInfo projectInfo) throws IOException {
		File workspaceRoot = CoreUtil.getWorkspaceRootFile();

		File pomFile = projectInfo.getPomFile();

		File parentFile = pomFile.getParentFile();

		File basedir = parentFile.getCanonicalFile();

		int basedirRename = MavenProjectInfo.RENAME_NO;

		if (workspaceRoot.equals(basedir.getParentFile())) {
			basedirRename = MavenProjectInfo.RENAME_REQUIRED;
		}

		projectInfo.setBasedirRename(basedirRename);
	}

	private static final IProgressMonitor _npm = new NullProgressMonitor();

}