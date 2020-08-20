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

package com.liferay.ide.upgrade.problems.core;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;

import java.io.File;

import java.util.Collection;
import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
public interface UpgradeProblemSupport {

	public default void addMarkers(Collection<UpgradeProblem> upgradeProblems) {
		upgradeProblems.stream(
		).filter(
			upgradeProblem -> Objects.nonNull(upgradeProblem.getResource())
		).filter(
			upgradeProblem -> {
				File resourceFile = upgradeProblem.getResource();

				return resourceFile.exists();
			}
		).filter(
			upgradeProblem -> {
				File resourceFile = upgradeProblem.getResource();

				IResource problemFile = findResourceForLocationURI(resourceFile);

				return problemFile != null;
			}
		).forEach(
			upgradeProblem -> {
				File resource = upgradeProblem.getResource();

				IResource problemFile = findResourceForLocationURI(resource);

				try {
					IMarker marker = problemFile.createMarker(UpgradeProblem.MARKER_TYPE);

					upgradeProblem.setMarkerId(marker.getId());

					upgradeProblemToMarker(upgradeProblem, marker);
				}
				catch (CoreException ce) {
				}
			}
		);
	}

	public default void deleteMarker(IMarker marker) {
		try {
			marker.delete();
		}
		catch (CoreException ce) {
		}
	}

	public default IResource filterIResource(IResource[] resources) {
		IResource result = null;

		for (IResource resource : resources) {
			if (result == null) {
				result = resource;
			}
			else {
				IPath filePath = resource.getProjectRelativePath();
				IPath resourcePath = result.getProjectRelativePath();

				if (filePath.segmentCount() < resourcePath.segmentCount()) {
					result = resource;
				}
			}
		}

		if (result == null) {
			return null;
		}

		return result;
	}

	public default IMarker findMarker(UpgradeProblem upgradeProblem) {
		if (upgradeProblem == null) {
			return null;
		}

		File file = upgradeProblem.getResource();

		IFile resource = (IFile)findResourceForLocationURI(file);

		if (resource != null) {
			long markerId = upgradeProblem.getMarkerId();

			try {
				return resource.findMarker(markerId);
			}
			catch (CoreException ce) {
			}
		}

		return null;
	}

	public default IResource findResourceForLocationURI(File file) {
		IWorkspaceRoot root = getWorkspaceRoot();

		IFile[] files = root.findFilesForLocationURI(file.toURI());

		return filterIResource(files);
	}

	public default IProject getProject(File file) {
		IWorkspaceRoot ws = getWorkspaceRoot();

		IResource[] containers = ws.findContainersForLocationURI(file.toURI());

		IResource resource = filterIResource(containers);

		if (resource == null) {
			return null;
		}

		return resource.getProject();
	}

	public default IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public default IWorkspaceRoot getWorkspaceRoot() {
		IWorkspace workspace = getWorkspace();

		return workspace.getRoot();
	}

	public default boolean markerExists(IMarker marker) {
		if ((marker != null) && marker.exists()) {
			return true;
		}

		return false;
	}

	public default void refreshProjects(Collection<UpgradeProblem> upgradeProblems, IProgressMonitor progressMonitor) {
		upgradeProblems.stream(
		).map(
			UpgradeProblem::getResource
		).map(
			this::getProject
		).filter(
			Objects::nonNull
		).distinct(
		).forEach(
			project -> {
				try {
					project.refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				}
				catch (CoreException ce) {
				}
			}
		);
	}

	public default void removeMarkers(Collection<UpgradeProblem> upgradeProblems) {
		upgradeProblems.stream(
		).map(
			this::findMarker
		).filter(
			this::markerExists
		).forEach(
			this::deleteMarker
		);
	}

	public default void upgradeProblemToMarker(UpgradeProblem upgradeProblem, IMarker marker) throws CoreException {
		marker.setAttribute(IMarker.CHAR_END, upgradeProblem.getEndOffset());
		marker.setAttribute(IMarker.CHAR_START, upgradeProblem.getStartOffset());
		marker.setAttribute(IMarker.LINE_NUMBER, upgradeProblem.getLineNumber());
		marker.setAttribute(IMarker.MESSAGE, upgradeProblem.getTitle());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_AUTOCORRECTCONTEXT, upgradeProblem.getAutoCorrectContext());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_HTML, upgradeProblem.getHtml());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_STATUS, upgradeProblem.getStatus());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_SUMMARY, upgradeProblem.getSummary());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_TICKET, upgradeProblem.getTicket());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_TYPE, upgradeProblem.getType());

		File file = upgradeProblem.getResource();

		IResource resource = findResourceForLocationURI(file);

		marker.setAttribute(IMarker.LOCATION, resource.getName());

		marker.setAttribute(IMarker.SEVERITY, upgradeProblem.getMarkerType());
	}

}