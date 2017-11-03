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

package com.liferay.ide.project.ui;

import com.liferay.ide.core.util.PropertiesUtil;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Kuo Zhang
 */
public class LanguageFileEncodingNotDefaultResolution implements IMarkerResolution {

	public String getLabel() {
		return "Encode Language Files to Default (UTF-8).";
	}

	public void run(IMarker marker) {
		if (marker.getResource() instanceof IProject) {
			final IProject proj = (IProject)marker.getResource();

			try {
				IProgressService serivce = PlatformUI.getWorkbench().getProgressService();

				serivce.run(
					true, true,
					new IRunnableWithProgress() {

						public void run(IProgressMonitor monitor)
							throws InterruptedException, InvocationTargetException {

							monitor.beginTask("Encoding Liferay Language File to Default (UTF-8)... ", 10);

							PropertiesUtil.encodeLanguagePropertiesFilesToDefault(proj, monitor);

							monitor.done();
						}

					});
			}
			catch (Exception e) {
				ProjectUI.logError(e);
			}
		}
	}

}