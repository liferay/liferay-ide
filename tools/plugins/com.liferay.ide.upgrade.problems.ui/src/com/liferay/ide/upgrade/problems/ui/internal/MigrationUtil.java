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
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;

import java.io.File;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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

	public static void addMarkers(List<UpgradeProblem> problems) {
		for (UpgradeProblem problem : problems) {
			IResource upgradeProblemResource = problem.getResource();

			if (FileUtil.exists(upgradeProblemResource)) {
				try {
					IMarker marker = upgradeProblemResource.createMarker(UpgradeProblem.MARKER_TYPE);

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

	public static void openEditor(UpgradeProblem problem) {
		try {
			IResource resource = problem.getResource();

			if (resource instanceof IFile) {
				IMarker marker = null;

				try {
					marker = resource.findMarker(problem.getMarkerId());
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

						textEditor.selectAndReveal(
							problem.getStartOffset(), problem.getEndOffset() - problem.getStartOffset());
					}
				}
			}
		}
		catch (PartInitException pie) {
		}
	}

	public static void problemToMarker(UpgradeProblem problem, IMarker marker) throws CoreException {
		marker.setAttribute(IMarker.MESSAGE, problem.getTitle());
		marker.setAttribute("migrationProblem.summary", problem.getSummary());
		marker.setAttribute("migrationProblem.ticket", problem.getTicket());
		marker.setAttribute("migrationProblem.type", problem.getType());
		marker.setAttribute(IMarker.LINE_NUMBER, problem.getLineNumber());
		marker.setAttribute(IMarker.CHAR_START, problem.getStartOffset());
		marker.setAttribute(IMarker.CHAR_END, problem.getEndOffset());
		marker.setAttribute("migrationProblem.autoCorrectContext", problem.getAutoCorrectContext());
		marker.setAttribute("migrationProblem.html", problem.getHtml());
		marker.setAttribute("migrationProblem.status", problem.getStatus());

		IResource resource = problem.getResource();

		marker.setAttribute(IMarker.LOCATION, resource.getName());

		marker.setAttribute(IMarker.SEVERITY, problem.getMarkerType());
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