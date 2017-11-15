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

package com.liferay.ide.maven.core;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

/**
 * @author Gregory Amerson
 */
public class FacetedMavenBundleProject extends FacetedMavenProject implements IBundleProject {

	public FacetedMavenBundleProject(IProject project) {
		super(project);

		_bundleProject = new MavenBundlePluginProject(project);
	}

	@Override
	public <T> T adapt(Class<T> adapterType) {
		if (ILiferayPortal.class.equals(adapterType)) {
			return null;
		}

		return super.adapt(adapterType);
	}

	@Override
	public boolean filterResource(IPath resourcePath) {
		return _bundleProject.filterResource(resourcePath);
	}

	@Override
	public String getBundleShape() {
		return "war";
	}

	@Override
	public IPath getOutputBundle(boolean cleanBuild, IProgressMonitor monitor) throws CoreException {
		IPath outputJar = null;

		MavenProjectBuilder mavenProjectBuilder = new MavenProjectBuilder(getProject());

		List<String> goals = new ArrayList<>();

		if (cleanBuild) {
			goals.add("clean");
		}

		goals.add("package");

		for (String goal : goals) {
			mavenProjectBuilder.runMavenGoal(getProject(), goal, monitor);
		}

		// we are going to try to get the output jar even if the package failed.

		IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade(getProject(), monitor);

		MavenProject mavenProject = projectFacade.getMavenProject(monitor);

		String targetName = mavenProject.getBuild().getFinalName() + "." + getBundleShape();

		IFolder targetFolder = getProject().getFolder("target");

		if (FileUtil.exists(targetFolder)) {

			// targetFolder.refreshLocal( IResource.DEPTH_ONE, monitor );

			IPath targetFile = targetFolder.getRawLocation().append(targetName);

			if (FileUtil.exists(targetFile)) {
				outputJar = targetFile;
			}
		}

		if (FileUtil.notExists(outputJar)) {
			throw new CoreException(
				LiferayMavenCore.createErrorStatus(
					"Unable to get output bundle for project " + getProject().getName()));
		}

		return outputJar;
	}

	@Override
	public IPath getOutputBundlePath() {
		IPath outputJar = null;

		try {
			IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade(getProject(), null);

			MavenProject mavenProject = projectFacade.getMavenProject(null);

			String targetName = mavenProject.getBuild().getFinalName() + "." + getBundleShape();

			IFolder targetFolder = getProject().getFolder("target");

			if (targetFolder.exists()) {
				IPath targetFile = targetFolder.getRawLocation().append(targetName);

				if (targetFile.toFile().exists()) {
					outputJar = targetFile;
				}
			}
		}
		catch (Exception e) {
			LiferayMavenCore.logError(e);
		}

		return outputJar;
	}

	@Override
	public String getSymbolicName() throws CoreException {
		return this._bundleProject.getSymbolicName();
	}

	@Override
	public boolean isFragmentBundle() {
		return false;
	}

	private MavenBundlePluginProject _bundleProject;

}