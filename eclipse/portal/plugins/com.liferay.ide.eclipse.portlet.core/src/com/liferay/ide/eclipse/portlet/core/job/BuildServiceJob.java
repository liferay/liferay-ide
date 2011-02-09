/*******************************************************************************
 * Copyright (c) 2010-2011 Liferay, Inc. All rights reserved.
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
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
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

	protected void disableBuildAndRefresh()
		throws CoreException {

		IWorkspaceDescription desc = getWorkspace().getDescription();
		desc.setAutoBuilding(false);

		Preferences resourcePrefs = ResourcesPlugin.getPlugin().getPluginPreferences();
		resourcePrefs.setValue(ResourcesPlugin.PREF_AUTO_REFRESH, false);

		getWorkspace().setDescription(desc);
	}

	protected IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	protected void reenableBuildAndRefresh(boolean saveAutoBuild, boolean saveAutoRefresh)
		throws CoreException {

		IWorkspaceDescription desc = getWorkspace().getDescription();

		desc.setAutoBuilding(saveAutoBuild);

		final Preferences resourcePrefs = ResourcesPlugin.getPlugin().getPluginPreferences();

		resourcePrefs.setValue(ResourcesPlugin.PREF_AUTO_REFRESH, saveAutoRefresh);

		getWorkspace().setDescription(desc);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		final IStatus[] retval = new IStatus[1];

		monitor.beginTask("Building Liferay services...", 100);

		IWorkspaceDescription desc = getWorkspace().getDescription();

		final boolean saveAutoBuild = desc.isAutoBuilding();

		final Preferences resourcePrefs = ResourcesPlugin.getPlugin().getPluginPreferences();

		final boolean saveAutoRefresh = resourcePrefs.getBoolean(ResourcesPlugin.PREF_AUTO_REFRESH);

		try {
			getWorkspace().run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor)
					throws CoreException {

					disableBuildAndRefresh();

					runBuildService(monitor);

					reenableBuildAndRefresh(saveAutoBuild, saveAutoRefresh);

					getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
				}
			}, monitor);

			getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		catch (CoreException e1) {
			retval[0] = PortletCore.createErrorStatus(e1);
		}
		finally {
			try {
				getWorkspace().run(new IWorkspaceRunnable() {

					public void run(IProgressMonitor monitor)
						throws CoreException {

						reenableBuildAndRefresh(saveAutoBuild, saveAutoRefresh);
					}
				}, monitor);
			}
			catch (CoreException e1) {
				retval[0] = PortletCore.createErrorStatus(e1);
			}
		}

		new Job("Build service classpath update") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					IJavaProject javaProject = JavaCore.create(project);

					List<IClasspathEntry> existingRawClasspath = Arrays.asList(javaProject.getRawClasspath());

					updateClasspath(existingRawClasspath, javaProject);

					project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

					if (!(project.isSynchronized(IResource.DEPTH_INFINITE))) {
						project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
					}

					reenableBuildAndRefresh(saveAutoBuild, saveAutoRefresh);
				}
				catch (Exception e) {
					PortletCore.logError(e);
				}

				return Status.OK_STATUS;
			}

		}.schedule();

		return retval[0] == null || retval[0].isOK() ? Status.OK_STATUS : retval[0];
	}

	protected void runBuildService(IProgressMonitor monitor) {
		SDK sdk = getSDK();

		monitor.worked(50);

		sdk.buildService(getProject(), serviceXmlFile, null);

		monitor.worked(90);
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

					ClasspathContainerInitializer initializer =
						JavaCore.getClasspathContainerInitializer("org.eclipse.jst.j2ee.internal.web.container");

					initializer.requestClasspathContainerUpdate(entry.getPath(), javaProject, container);
				}
				catch (Exception e) {
					return PortletCore.createErrorStatus(e);
				}

				break;
			}
		}

		return Status.OK_STATUS;
	}

}
