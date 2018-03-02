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

package com.liferay.ide.project.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.project.core.util.ClasspathUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Simon Jiang
 */
public class SDKBuildPropertiesResourceListener implements IResourceChangeListener, IResourceDeltaVisitor {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event == null) {
			return;
		}

		IResourceDelta delta = event.getDelta();

		if (delta == null) {
			return;
		}

		try {
			for (IResourceDelta child : delta.getAffectedChildren()) {
				IResource resource = child.getResource();

				if (resource == null) {
					continue;
				}

				IProject[] sdkProjects = SDKUtil.getWorkspaceSDKs();

				for (IProject sdkProject : sdkProjects) {
					IPath sdkProjectLocation = sdkProject.getLocation();

					if (sdkProjectLocation == null) {
						continue;
					}

					IResourceDelta[] sdkChangedFiles = child.getAffectedChildren(
						IResourceDelta.CHANGED | IResourceDelta.ADDED | IResourceDelta.REMOVED);

					for (IResourceDelta sdkDelta : sdkChangedFiles) {
						IResource sdkDeltaResource = sdkDelta.getResource();

						if ((sdkDeltaResource != null) && (sdkDeltaResource.getLocation() != null) &&
							sdkProjectLocation.isPrefixOf(sdkDeltaResource.getLocation())) {

							String deltaLastSegment = sdkDelta.getFullPath().lastSegment();

							Matcher propertiesMatcher = _PATTERN_BUILD_PROPERTIES.matcher(deltaLastSegment);

							if (propertiesMatcher.matches()) {
								sdkDelta.accept(this);

								break;
							}
						}
					}
				}
			}
		}
		catch (Throwable e) {
			ProjectCore.logError("build.properties resource listener failed.", e);
		}
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		switch (delta.getResource().getType()) {
			case IResource.ROOT:
			case IResource.PROJECT:
			case IResource.FOLDER:
				return true;

			case IResource.FILE:
				IFile deltaFile = (IFile)delta.getResource();

				Job job = new WorkspaceJob("Processing SDK build properties file") {

					@Override
					public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
						try {
							boolean hasMultipleSDK = _checkMultipleSDK(monitor);

							if (hasMultipleSDK) {
								return Status.OK_STATUS;
							}

							IPath deltaLocation = deltaFile.getLocation();

							if (deltaLocation != null) {
								SDK sdk = SDKUtil.getWorkspaceSDK();

								if (sdk.getLocation().isPrefixOf(deltaLocation)) {
									processPropertiesFileChanged(deltaFile);
								}
							}
						}
						catch (CoreException ce) {
							ProjectCore.logError(ce);
						}

						return Status.OK_STATUS;
					}

				};

				job.schedule();
		}

		return false;
	}

	protected void processPropertiesFileChanged(IFile deltaFile) throws CoreException {
		IProject deltaProject = deltaFile.getProject();

		SDK sdk = SDKUtil.createSDKFromLocation(deltaProject.getLocation());

		if (sdk == null) {
			return;
		}

		IMarker[] problemMarkers = MarkerUtil.findMarkers(
			deltaFile.getProject(), IMarker.PROBLEM, _MARKER_ID_SDK_PROPERTIES_INVALID);
		IStatus sdkStatus = sdk.validate(true);

		if (sdkStatus.isOK()) {
			if (ListUtil.isNotEmpty(problemMarkers)) {
				MarkerUtil.clearMarkers(deltaFile.getProject(), IMarker.PROBLEM, _MARKER_ID_SDK_PROPERTIES_INVALID);
			}

			for (IProject project : CoreUtil.getAllProjects()) {
				if (SDKUtil.isSDKProject(project) && sdk.getLocation().isPrefixOf(project.getLocation())) {
					Job job = new WorkspaceJob("Updating dependencies " + project.getName()) {

						@Override
						public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
							ClasspathUtil.updateRequestContainer(project);

							return Status.OK_STATUS;
						}

					};

					job.schedule();
				}
			}
		}
		else {
			IStatus[] statuses = sdkStatus.getChildren();

			for (IMarker marker : problemMarkers) {
				boolean canDelete = true;
				String message = (String)marker.getAttribute(IMarker.MESSAGE);

				for (IStatus status : statuses) {
					if (status.getMessage().equals(message)) {
						canDelete = false;
						break;
					}
				}

				if (canDelete) {
					marker.delete();
				}
			}

			for (IStatus status : statuses) {
				boolean canAdd = true;

				for (IMarker marker : problemMarkers) {
					if (marker.exists()) {
						String message = (String)marker.getAttribute(IMarker.MESSAGE);

						if (status.getMessage().equals(message)) {
							canAdd = false;
							break;
						}
					}
				}

				if (canAdd) {
					MarkerUtil.setMarker(
						deltaFile, IMarker.PROBLEM, IMarker.SEVERITY_ERROR, status.getMessage(),
						deltaFile.getFullPath().toPortableString(), _MARKER_ID_SDK_PROPERTIES_INVALID);
				}
			}
		}
	}

	private boolean _checkMultipleSDK(IProgressMonitor monitor) throws CoreException {
		boolean hasMultipleSDK = false;
		boolean findSDK = false;

		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject existProject : projects) {
			if (SDKUtil.isValidSDKLocation(existProject.getLocation().toPortableString())) {
				IMarker[] problemMarkers = MarkerUtil.findMarkers(
					existProject, IMarker.PROBLEM, _ID_WORKSPACE_SDK_INVALID);

				if (findSDK == false) {
					if (ListUtil.isNotEmpty(problemMarkers)) {
						MarkerUtil.clearMarkers(existProject, IMarker.PROBLEM, _ID_WORKSPACE_SDK_INVALID);
					}

					findSDK = true;
				}
				else {
					if ((problemMarkers == null) || (problemMarkers.length < 1)) {
						MarkerUtil.setMarker(
							existProject, IMarker.PROBLEM, IMarker.SEVERITY_ERROR, "Workspace has more than one SDK",
							existProject.getFullPath().toPortableString(), _ID_WORKSPACE_SDK_INVALID);
					}

					hasMultipleSDK = true;
				}

				existProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}
		}

		return hasMultipleSDK;
	}

	private static final String _ID_WORKSPACE_SDK_INVALID = "workspace-sdk-invalid";

	private static final String _MARKER_ID_SDK_PROPERTIES_INVALID = "sdk-properties-invalid";

	private static final Pattern _PATTERN_BUILD_PROPERTIES = Pattern.compile("build.[\\w|\\W.]*properties");

}