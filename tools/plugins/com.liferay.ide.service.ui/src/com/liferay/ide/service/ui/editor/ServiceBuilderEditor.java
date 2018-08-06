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

package com.liferay.ide.service.ui.editor;

import com.liferay.ide.service.core.model.ServiceBuilder6xx;

import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Gregory Amerson
 */
public class ServiceBuilderEditor extends SapphireEditorForXml {

	public ServiceBuilderEditor() {
		super(ServiceBuilder6xx.TYPE, null);
	}

	public InputStream getFileContents() throws CoreException, IOException, MalformedURLException {
		InputStream retval = null;

		IEditorInput editorInput = getEditorInput();

		if (editorInput instanceof FileEditorInput) {
			IFile file = ((FileEditorInput)editorInput).getFile();

			retval = file.getContents();
		}
		else if (editorInput instanceof IStorageEditorInput) {
			IStorage storage = ((IStorageEditorInput)editorInput).getStorage();

			retval = storage.getContents();
		}
		else if (editorInput instanceof FileStoreEditorInput) {
			URI uri = ((FileStoreEditorInput)editorInput).getURI();

			URL editorInputURL = uri.toURL();

			retval = editorInputURL.openStream();
		}

		return retval;
	}

	@Override
	protected void createDiagramPages() throws PartInitException {
		addDeferredPage(2, "Diagram", "diagramPage");
	}

	@Override
	protected void createFormPages() throws PartInitException {
		addDeferredPage(1, "Overview", "serviceBuilderPage");
	}

}