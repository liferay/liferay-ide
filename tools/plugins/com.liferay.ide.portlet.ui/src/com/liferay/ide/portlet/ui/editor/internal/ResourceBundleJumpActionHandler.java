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

package com.liferay.ide.portlet.ui.editor.internal;

import static com.liferay.ide.core.model.internal.GenericResourceBundlePathService.RB_FILE_EXTENSION;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.util.PortletUtil;
import com.liferay.ide.portlet.ui.PortletUIPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.forms.JumpActionHandler;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author Kamesh Sampath
 */
public class ResourceBundleJumpActionHandler extends JumpActionHandler {

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler#
	 * computeEnablementState()
	 */
	@Override
	protected boolean computeEnablementState() {
		Element element = getModelElement();

		IProject project = element.adapt(IProject.class);

		ValueProperty property = (ValueProperty)property().definition();

		String text = element.property(property).text(true);

		boolean enabled = super.computeEnablementState();

		if (enabled && (text != null)) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();

			IWorkspaceRoot wroot = workspace.getRoot();

			IClasspathEntry[] cpEntries = CoreUtil.getClasspathEntries(project);
			String ioFileName = PortletUtil.convertJavaToIoFileName(text, RB_FILE_EXTENSION);

			if (cpEntries != null) {
				for (IClasspathEntry iClasspathEntry : cpEntries) {
					if (IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind()) {
						IPath entryPath = wroot.getFolder(iClasspathEntry.getPath()).getLocation();

						entryPath = entryPath.append(ioFileName);

						IFile resourceBundleFile = wroot.getFileForLocation(entryPath);

						if ((resourceBundleFile != null) && resourceBundleFile.exists()) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.
	 * SapphireRenderingContext)
	 */
	@Override
	protected Object run(Presentation context) {
		Element element = getModelElement();

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		ValueProperty property = (ValueProperty)property().definition();

		IProject project = element.adapt(IProject.class);

		Value<Path> value = element.property(property);

		String text = value.text(false);

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IWorkspaceRoot wroot = workspace.getRoot();

		IClasspathEntry[] cpEntries = CoreUtil.getClasspathEntries(project);
		String ioFileName = PortletUtil.convertJavaToIoFileName(text, RB_FILE_EXTENSION);

		for (IClasspathEntry iClasspathEntry : cpEntries) {
			if (IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind()) {
				IPath entryPath = wroot.getFolder(iClasspathEntry.getPath()).getLocation();

				entryPath = entryPath.append(ioFileName);

				IFile resourceBundleFile = wroot.getFileForLocation(entryPath);

				if ((resourceBundleFile != null) && resourceBundleFile.exists()) {
					if (window != null) {
						IWorkbenchPage page = window.getActivePage();
						IEditorDescriptor editorDescriptor = null;

						try {
							editorDescriptor = IDE.getEditorDescriptor(resourceBundleFile.getName());
						}
						catch (PartInitException pie) {

							// No editor was found for this file type.

						}

						if (editorDescriptor != null) {
							try {
								IDE.openEditor(page, resourceBundleFile, editorDescriptor.getId(), true);
							}
							catch (PartInitException pie) {
								PortletUIPlugin.logError(pie);
							}
						}
					}
				}
			}
		}

		return null;
	}

}