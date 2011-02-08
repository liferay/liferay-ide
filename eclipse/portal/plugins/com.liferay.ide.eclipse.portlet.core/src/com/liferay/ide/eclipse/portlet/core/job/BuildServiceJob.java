/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.portlet.core.job;

import com.liferay.ide.eclipse.portlet.core.PortletCore;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.job.SDKJob;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ClasspathEntry;

/**
 * @author Greg Amerson
 */
@SuppressWarnings({
	"restriction", "deprecation"
})
public class BuildServiceJob extends SDKJob {

	protected IFile serviceXmlFile;

	public BuildServiceJob(IFile serviceXmlFile) {
		super("Build services");

		this.serviceXmlFile = serviceXmlFile;

		setUser(true);

		setProject(serviceXmlFile.getProject());
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Building Liferay services...", 100);

		IWorkspaceDescription desc = ResourcesPlugin.getWorkspace().getDescription();

		boolean saveAutoBuild = desc.isAutoBuilding();

		desc.setAutoBuilding(false);

		Preferences resourcePrefs = ResourcesPlugin.getPlugin().getPluginPreferences();

		boolean autoRefresh = resourcePrefs.getBoolean(ResourcesPlugin.PREF_AUTO_REFRESH);

		if (autoRefresh) {
			resourcePrefs.setValue(ResourcesPlugin.PREF_AUTO_REFRESH, false);
		}

		try {
			ResourcesPlugin.getWorkspace().setDescription(desc);

			SDK sdk = getSDK();

			monitor.worked(50);

			sdk.buildService(getProject(), serviceXmlFile, null);

			monitor.worked(90);

			getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);

			getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);

			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor)
					throws CoreException {

					List<IClasspathEntry> existingRawClasspath = null;

					try {
						IJavaProject javaProject = JavaCore.create(project);

						existingRawClasspath = Arrays.asList(javaProject.getRawClasspath());

						updateClasspath(existingRawClasspath, javaProject);

						project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

						if (!(project.isSynchronized(IResource.DEPTH_INFINITE))) {
							project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
						}
					}
					catch (Exception e) {
						PortletCore.logError(e);
					}

				}
			}, null);

			getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		catch (CoreException e1) {
			return PortletCore.createErrorStatus(e1);
		}
		finally {
			desc = ResourcesPlugin.getWorkspace().getDescription();

			desc.setAutoBuilding(saveAutoBuild);

			if (autoRefresh) {
				resourcePrefs.setValue(ResourcesPlugin.PREF_AUTO_REFRESH, autoRefresh);
			}

			try {
				ResourcesPlugin.getWorkspace().setDescription(desc);
			}
			catch (CoreException e) {
				return PortletCore.createErrorStatus(e);
			}
		}

		return Status.OK_STATUS;
	}

	protected IStatus updateClasspath(List<IClasspathEntry> existingRawClasspath, IJavaProject javaProject) {
		for (IClasspathEntry entry : existingRawClasspath) {
			if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER &&
				entry.getPath().toString().equals("org.eclipse.jst.j2ee.internal.web.container")) {

				try {
					IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), javaProject);

					IClasspathEntry[] webappEntries = container.getClasspathEntries();

					for (IClasspathEntry entry2 : webappEntries) {
						if (entry2.getPath().lastSegment().equals(getProject().getName() + "-service.jar")) {
							((ClasspathEntry) entry2).sourceAttachmentPath =
								getProject().getFolder("docroot/WEB-INF/service").getFullPath();

							break;
						}
					}
				}
				catch (JavaModelException e) {
					return PortletCore.createErrorStatus(e);
				}

				break;
			}
		}

		try {
			javaProject.setRawClasspath(existingRawClasspath.toArray(new IClasspathEntry[0]), null);
		}
		catch (JavaModelException e) {
			return PortletCore.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

}
