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

package com.liferay.ide.kaleo.ui.editor;

import com.liferay.ide.kaleo.ui.AbstractKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;

import java.io.InputStream;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Gregory Amerson
 */
public class DefaultScriptEditorHelper extends AbstractKaleoEditorHelper {

	public IEditorPart createEditorPart(ScriptPropertyEditorInput editorInput, IEditorSite editorSite) {
		IEditorPart editorPart = null;

		try {
			String fileName = editorInput.getName();

			IContentType contentType = null;

			IContentTypeManager contentTypeManager = Platform.getContentTypeManager();

			try (InputStream inputStream = editorInput.getStorage().getContents()) {
				IContentDescription contentDescription = contentTypeManager.getDescriptionFor(
					inputStream, fileName, IContentDescription.ALL);

				if (contentDescription != null) {
					contentType = contentDescription.getContentType();
				}
			}

			if (contentType == null) {

				// use basic text content type

				contentType = Platform.getContentTypeManager().getContentType("org.eclipse.core.runtime.text");
			}

			IWorkbench workBench = PlatformUI.getWorkbench();

			IEditorRegistry editRegistry = workBench.getEditorRegistry();

			IEditorDescriptor defaultEditorDescriptor = editRegistry.getDefaultEditor(fileName, contentType);

			String editorId = defaultEditorDescriptor.getId();

			IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

			IConfigurationElement[] editorConfigs = extensionRegistry.getConfigurationElementsFor(
				"org.eclipse.ui.editors");

			for (IConfigurationElement config : editorConfigs) {
				if (editorId.equals(config.getAttribute("id"))) {
					editorPart = (IEditorPart)config.createExecutableExtension("class");

					break;
				}
			}

			editorPart.init(editorSite, editorInput);
		}
		catch (Exception e) {
			KaleoUI.logError("Could not create default script editor.", e);
		}

		return editorPart;
	}

	public String getEditorId() {
		IContentType contentType = Platform.getContentTypeManager().getContentType("org.eclipse.core.runtime.text");

		IWorkbench workBench = PlatformUI.getWorkbench();

		IEditorRegistry editRegistry = workBench.getEditorRegistry();

		IEditorDescriptor defaultEditorDescriptor = editRegistry.getDefaultEditor("default.txt", contentType);

		return defaultEditorDescriptor.getId();
	}

}