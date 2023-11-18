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

import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.Objects;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;
import org.eclipse.m2e.jdt.internal.MavenClasspathHelpers;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class BundleProjectConfigurator extends AbstractProjectConfigurator implements IJavaProjectConfigurator {

	public BundleProjectConfigurator() {
	}

	@Override
	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		IProject project = MavenUtil.getProject(request);

		if (Objects.isNull(project)) {
			MavenProject mavenProject = request.mavenProject();

			throw new CoreException(
				LiferayMavenCore.createErrorStatus(
					"Can not get correct eclipse project for " + mavenProject.getName()));
		}

		if (_isMavenBundlePlugin(project)) {
			LiferayNature.addLiferayNature(project, monitor);
		}

		if (project.hasNature(JavaCore.NATURE_ID)) {
			IJavaProject javaProject = JavaCore.create(project);

			String vmCompliance = ProjectUtil.getVmCompliance(JavaRuntime.getDefaultVMInstall());

			ProjectUtil.updateComplianceSettings(javaProject, vmCompliance);
		}

		monitor.worked(100);
		monitor.done();
	}

	public void configureClasspath(IMavenProjectFacade facade, IClasspathDescriptor classpath, IProgressMonitor monitor)
		throws CoreException {
	}

	public void configureRawClasspath(
			ProjectConfigurationRequest request, IClasspathDescriptor classpath, IProgressMonitor monitor)
		throws CoreException {

		IMavenProjectFacade mavenProjectFacade = request.mavenProjectFacade();

		IClasspathEntry jreContainerEntry = MavenClasspathHelpers.getJREContainerEntry(
			JavaCore.create(mavenProjectFacade.getProject()));

		classpath.removeEntry(jreContainerEntry.getPath());

		IClasspathEntry defaultJREContainerEntry = JavaCore.newContainerEntry(
			JavaRuntime.newJREContainerPath(JavaRuntime.getDefaultVMInstall()));

		classpath.addEntry(defaultJREContainerEntry);
	}

	private boolean _isMavenBundlePlugin(IProject project) {
		NullProgressMonitor monitor = new NullProgressMonitor();

		IMavenProjectFacade facade = MavenUtil.getProjectFacade(project, monitor);

		if (facade != null) {
			try {
				MavenProject mavenProject = facade.getMavenProject(new NullProgressMonitor());

				if ((mavenProject != null) && Objects.equals("bundle", mavenProject.getPackaging())) {
					Plugin mavenBundlePlugin = MavenUtil.getPlugin(
						facade, ILiferayMavenConstants.MAVEN_BUNDLE_PLUGIN_KEY, monitor);

					if (mavenBundlePlugin != null) {
						return true;
					}
				}
				else if ((mavenProject != null) && Objects.equals("jar", mavenProject.getPackaging())) {
					Plugin bndMavenPlugin = MavenUtil.getPlugin(
						facade, ILiferayMavenConstants.BND_MAVEN_PLUGIN_KEY, monitor);

					if (bndMavenPlugin != null) {
						return true;
					}
				}
			}
			catch (CoreException ce) {
			}
		}

		return false;
	}

}