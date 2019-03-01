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

package com.liferay.ide.ui.util;

import com.liferay.ide.core.util.CoreUtil;

import java.io.File;

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
 */
public class Editors {

	public static void open(File file) {
		try {
			IResource resource = _getIResourceFromFile(file);

			if (resource instanceof IFile) {
				IDE.openEditor(UIUtil.getActivePage(), (IFile)resource);
			}
		}
		catch (PartInitException pie) {
		}
	}

	public static void open(IResource resource, long markerId, int offset, int length) {
		try {
			if (resource instanceof IFile) {
				IMarker marker = null;

				try {
					marker = resource.findMarker(markerId);
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

						textEditor.selectAndReveal(offset, length);
					}
				}
			}
		}
		catch (PartInitException pie) {
		}
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