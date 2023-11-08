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
import com.liferay.ide.server.util.JavaUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;
import org.eclipse.m2e.jdt.internal.MavenClasspathHelpers;

import org.osgi.framework.Version;

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

			// String currentVersion = JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE);

			IVMInstall defaultVMInstall = JavaRuntime.getDefaultVMInstall();

			String javaVersion;

			if (defaultVMInstall instanceof IVMInstall2) {
				IVMInstall2 vmInstall2 = (IVMInstall2)defaultVMInstall;

				javaVersion = vmInstall2.getJavaVersion();
			}
			else {
				Version version = Version.parseVersion(JavaUtil.getJDKVersion(defaultVMInstall));

				javaVersion = version.toString();
			}

			if (JavaCore.compareJavaVersions(javaVersion, JavaCore.VERSION_1_8) != 0) {

				String vmCompliance = _getVmCompliance(defaultVMInstall);

				_updateComplianceSettings(javaProject, vmCompliance);
			}
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

	private String _getVmCompliance(IVMInstall defaultVMInstall) {
		if (defaultVMInstall instanceof IVMInstall2) {
			IVMInstall2 vmInstall2 = (IVMInstall2)defaultVMInstall;

			String javaVersion = vmInstall2.getJavaVersion();

			if (javaVersion != null) {
				String compliance = null;

				if (javaVersion.startsWith(JavaCore.VERSION_1_5)) {
					compliance = JavaCore.VERSION_1_5;
				}
				else if (javaVersion.startsWith(JavaCore.VERSION_1_6)) {
					compliance = JavaCore.VERSION_1_6;
				}
				else if (javaVersion.startsWith(JavaCore.VERSION_1_7)) {
					compliance = JavaCore.VERSION_1_7;
				}
				else if (javaVersion.startsWith(JavaCore.VERSION_1_8)) {
					compliance = JavaCore.VERSION_1_8;
				}
				else if (javaVersion.startsWith(JavaCore.VERSION_9) &&
						 ((javaVersion.length() == JavaCore.VERSION_9.length()) ||
						  (javaVersion.charAt(JavaCore.VERSION_9.length()) == '.'))) {

					compliance = JavaCore.VERSION_9;
				}
				else if (javaVersion.startsWith(JavaCore.VERSION_10) &&
						 ((javaVersion.length() == JavaCore.VERSION_10.length()) ||
						  (javaVersion.charAt(JavaCore.VERSION_10.length()) == '.'))) {

					compliance = JavaCore.VERSION_10;
				}
				else if (javaVersion.startsWith(JavaCore.VERSION_11) &&
						 ((javaVersion.length() == JavaCore.VERSION_11.length()) ||
						  (javaVersion.charAt(JavaCore.VERSION_11.length()) == '.'))) {

					compliance = JavaCore.VERSION_11;
				}
				else if (javaVersion.startsWith(JavaCore.VERSION_12) &&
						 ((javaVersion.length() == JavaCore.VERSION_12.length()) ||
						  (javaVersion.charAt(JavaCore.VERSION_12.length()) == '.'))) {

					compliance = JavaCore.VERSION_12;
				}
				else if (javaVersion.startsWith(JavaCore.VERSION_13) &&
						 ((javaVersion.length() == JavaCore.VERSION_13.length()) ||
						  (javaVersion.charAt(JavaCore.VERSION_13.length()) == '.'))) {

					compliance = JavaCore.VERSION_13;
				}
				else if (javaVersion.startsWith(JavaCore.VERSION_14) &&
						 ((javaVersion.length() == JavaCore.VERSION_14.length()) ||
						  (javaVersion.charAt(JavaCore.VERSION_14.length()) == '.'))) {

					compliance = JavaCore.VERSION_14;
				}
				else if (javaVersion.startsWith(JavaCore.VERSION_15) &&
						 ((javaVersion.length() == JavaCore.VERSION_15.length()) ||
						  (javaVersion.charAt(JavaCore.VERSION_15.length()) == '.'))) {

					compliance = JavaCore.VERSION_15;
				}
				else if (javaVersion.startsWith(JavaCore.VERSION_16) &&
						 ((javaVersion.length() == JavaCore.VERSION_16.length()) ||
						  (javaVersion.charAt(JavaCore.VERSION_16.length()) == '.'))) {

					compliance = JavaCore.VERSION_16;
				}
				else if (javaVersion.startsWith(JavaCore.VERSION_17) &&
						 ((javaVersion.length() == JavaCore.VERSION_17.length()) ||
						  (javaVersion.charAt(JavaCore.VERSION_17.length()) == '.'))) {

					compliance = JavaCore.VERSION_17;
				}
				else if (javaVersion.startsWith(JavaCore.VERSION_18) &&
						 ((javaVersion.length() == JavaCore.VERSION_18.length()) ||
						  (javaVersion.charAt(JavaCore.VERSION_18.length()) == '.'))) {

					compliance = JavaCore.VERSION_18;
				}
				else if (javaVersion.startsWith(JavaCore.VERSION_19) &&
						 ((javaVersion.length() == JavaCore.VERSION_19.length()) ||
						  (javaVersion.charAt(JavaCore.VERSION_19.length()) == '.'))) {

					compliance = JavaCore.VERSION_19;
				}
				else if (javaVersion.startsWith(JavaCore.VERSION_20) &&
						 ((javaVersion.length() == JavaCore.VERSION_20.length()) ||
						  (javaVersion.charAt(JavaCore.VERSION_20.length()) == '.'))) {

					compliance = JavaCore.VERSION_20;
				}
				else {
					compliance = JavaCore.VERSION_20; // use latest by default
				}

				return compliance;
			}
		}

		return JavaCore.VERSION_1_8;
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

	private void _updateComplianceSettings(IJavaProject project, String compliance) {
		HashMap<String, String> defaultOptions = new HashMap<>();

		JavaCore.setComplianceOptions(compliance, defaultOptions);

		Set<Map.Entry<String, String>> entrySet = defaultOptions.entrySet();

		Iterator<Map.Entry<String, String>> it = entrySet.iterator();

		while (it.hasNext()) {
			Map.Entry<String, String> pair = it.next();

			project.setOption(pair.getKey(), pair.getValue());
		}
	}

}