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

package com.liferay.ide.service.ui;

import com.liferay.ide.ui.util.UIUtil;

import java.lang.reflect.Method;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.SaveableHelper;

/**
 * @author Cindy Li
 */
@SuppressWarnings("restriction")
public class ServiceUIUtil {

	public static boolean shouldCreateServiceBuilderJob(IFile file) {
		IWorkbenchWindow[] windows = UIUtil.getWorkbenchWindows();

		for (IWorkbenchWindow window : windows) {
			IWorkbenchPage[] pages = window.getPages();

			for (IWorkbenchPage page : pages) {
				IEditorReference[] editorReferences = page.getEditorReferences();

				for (IEditorReference editorReference : editorReferences) {
					String fileName = file.getName();

					if (fileName.equals(editorReference.getName())) {
						IWorkbenchPart part = editorReference.getPart(true);

						// SaveableHelper.savePart is not visible on Indigo so must use reflection

						try {
							Method savePartMethod = SaveableHelper.class.getDeclaredMethod(
								"savePart", ISaveablePart.class, IWorkbenchPart.class, IWorkbenchWindow.class,
								Boolean.TYPE);

							savePartMethod.setAccessible(true);

							return (Boolean)savePartMethod.invoke(null, part, part, window, true);
						}
						catch (Exception e) {
						}
					}
				}
			}
		}

		return true;
	}

}