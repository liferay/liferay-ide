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

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.IResourceBundleProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.FlexibleProject;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.maven.project.MavenProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Simon Jiang
 */
public class FacetedMavenProject extends LiferayMavenProject implements IWebProject, IResourceBundleProject {

	public FacetedMavenProject(IProject project) {
		super(project);

		_flexibleProject = new FlexibleProject(project) {

			@Override
			public String getProperty(String key, String defaultValue) {
				return null;
			}

		};
	}

	@Override
	public <T> T adapt(Class<T> adapterType) {
		T adapter = super.adapt(adapterType);

		if (adapter != null) {
			return adapter;
		}

		IMavenProjectFacade facade = MavenUtil.getProjectFacade(getProject(), new NullProgressMonitor());

		if (facade != null) {
			if (ILiferayPortal.class.equals(adapterType)) {
				ILiferayPortal portal = new LiferayPortalMaven(this);

				return adapterType.cast(portal);
			}
		}

		return null;
	}

	@Override
	public IResource findDocrootResource(IPath path) {
		return this._flexibleProject.findDocrootResource(path);
	}

	@Override
	public IFolder getDefaultDocrootFolder() {
		return this._flexibleProject.getDefaultDocrootFolder();
	}

	@Override
	public List<IFile> getDefaultLanguageProperties() {
		return _flexibleProject.getDefaultLanguageProperties();
	}

	@Override
	public IFile getDescriptorFile(String name) {
		return this._flexibleProject.getDescriptorFile(name);
	}

	public Collection<IFile> getOutputs(boolean buildIfNeeded, IProgressMonitor monitor) throws CoreException {
		Collection<IFile> outputs = new HashSet<>();

		if (buildIfNeeded) {
			getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);

			new MavenProjectBuilder(getProject()).runMavenGoal(getProject(), "package", monitor);

			IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade(getProject(), monitor);

			MavenProject mavenProject = projectFacade.getMavenProject(monitor);

			String targetFolder = mavenProject.getBuild().getDirectory();
			String targetWar = mavenProject.getBuild().getFinalName() + "." + mavenProject.getPackaging();

			IFile output = getProject().getFile(new Path(targetFolder).append(targetWar));

			if (FileUtil.exists(output)) {
				outputs.add(output);
			}
		}

		return outputs;
	}

	private FlexibleProject _flexibleProject;

}