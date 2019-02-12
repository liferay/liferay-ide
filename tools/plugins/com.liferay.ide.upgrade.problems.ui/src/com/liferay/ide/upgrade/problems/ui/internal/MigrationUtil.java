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

package com.liferay.ide.upgrade.problems.ui.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.problems.core.FileUpgradeProblem;

import java.io.File;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Lovett Li
 */
public class MigrationUtil {

	public static void addMarkers(List<FileUpgradeProblem> problems) {
		IWorkspaceRoot ws = CoreUtil.getWorkspaceRoot();

		for (FileUpgradeProblem problem : problems) {
			IResource workspaceResource = null;

			File problemFile = problem.file;

			IResource[] containers = ws.findContainersForLocationURI(problemFile.toURI());

			if (ListUtil.isNotEmpty(containers)) {

				// prefer project containers

				for (IResource container : containers) {
					if (FileUtil.exists(container)) {
						if (container.getType() == IResource.PROJECT) {
							workspaceResource = container;

							break;
						}
						else {
							IProject project = container.getProject();

							if (CoreUtil.isLiferayProject(project)) {
								workspaceResource = container;

								break;
							}
						}
					}
				}

				if (workspaceResource == null) {
					final IFile[] files = ws.findFilesForLocationURI(problemFile.toURI());

					for (IFile file : files) {
						if (file.exists()) {
							if (workspaceResource == null) {
								if (CoreUtil.isLiferayProject(file.getProject())) {
									workspaceResource = file;
								}
							}
							else {

								// prefer the path that is shortest (to avoid a nested version)

								if (FileUtil.getSegmentCount(file.getFullPath()) <
										FileUtil.getSegmentCount(workspaceResource.getFullPath())) {

									workspaceResource = file;
								}
							}
						}
					}
				}

				if (workspaceResource == null) {
					for (IResource container : containers) {
						if (workspaceResource == null) {
							workspaceResource = container;
						}
						else {

							// prefer the path that is shortest (to avoid a nested version)

							if (FileUtil.getSegmentCount(container.getLocation()) <
									FileUtil.getSegmentCount(workspaceResource.getLocation())) {

								workspaceResource = container;
							}
						}
					}
				}
			}

			if (FileUtil.exists(workspaceResource)) {
				try {
					IMarker marker = workspaceResource.createMarker(FileUpgradeProblem.MARKER_TYPE);

					problem.setMarkerId(marker.getId());

					problemToMarker(problem, marker);
				}
				catch (CoreException ce) {
				}
			}
		}
	}

	public static void openEditor(FileProblemsContainer problem) {
		try {
			IResource resource = _getIResourceFromFile(problem.getFile());

			if (resource instanceof IFile) {
				IDE.openEditor(UIUtil.getActivePage(), (IFile)resource);
			}
		}
		catch (PartInitException pie) {
		}
	}

	public static void openEditor(FileUpgradeProblem problem) {
		try {
			IResource resource = _getIResourceFromFile(problem.file);

			if (resource instanceof IFile) {
				IMarker marker = null;

				try {
					marker = resource.findMarker(problem.markerId);
				}
				catch (CoreException ce) {
				}

				if (marker != null) {
					IDE.openEditor(UIUtil.getActivePage(), marker, OpenStrategy.activateOnOpen());
				}
				else {
					IEditorPart editor = IDE.openEditor(UIUtil.getActivePage(), (IFile)resource);

					if (editor instanceof ITextEditor) {
						ITextEditor textEditor = (ITextEditor)editor;

						textEditor.selectAndReveal(problem.startOffset, problem.endOffset - problem.startOffset);
					}
				}
			}
		}
		catch (PartInitException pie) {
		}
	}

	public static void problemToMarker(FileUpgradeProblem problem, IMarker marker) throws CoreException {
		marker.setAttribute(IMarker.MESSAGE, problem.title);
		marker.setAttribute("migrationProblem.summary", problem.summary);
		marker.setAttribute("migrationProblem.ticket", problem.ticket);
		marker.setAttribute("migrationProblem.type", problem.type);
		marker.setAttribute(IMarker.LINE_NUMBER, problem.lineNumber);
		marker.setAttribute(IMarker.CHAR_START, problem.startOffset);
		marker.setAttribute(IMarker.CHAR_END, problem.endOffset);
		marker.setAttribute("migrationProblem.autoCorrectContext", problem.getAutoCorrectContext());
		marker.setAttribute("migrationProblem.html", problem.html);
		marker.setAttribute("migrationProblem.status", problem.status);
		marker.setAttribute(IMarker.LOCATION, problem.file.getName());
		marker.setAttribute(IMarker.SEVERITY, problem.markerType);
	}

	private static IResource _getIResourceFromFile(File f) {
		IResource retval = null;

		IFile[] files = CoreUtil.findFilesForLocationURI(f.toURI());

		for (IFile file : files) {
			if (file.exists()) {
				if (retval == null) {

					// always prefer the file in a liferay project

					if (CoreUtil.isLiferayProject(file.getProject())) {
						retval = file;
					}
				}
				else {

					// if not lets pick the one that is shortest path

					IPath fileFullPath = file.getFullPath();
					IPath retvalFullPath = retval.getFullPath();

					if (fileFullPath.segmentCount() < retvalFullPath.segmentCount()) {
						retval = file;
					}
				}
			}
			else {
				if (retval == null) {
					IPath path = file.getFullPath();

					IProject project = CoreUtil.getProject(path.segment(path.segmentCount() - 1));

					if (project.exists()) {
						retval = project;
					}
				}
			}
		}

		return retval;
	}

}