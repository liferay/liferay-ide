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

package com.liferay.ide.xml.search.ui;

import com.liferay.ide.server.util.ComponentUtil;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;

/**
 * @author Gregory Amerson
 */
public abstract class CommonWorkbenchMarkerResolution extends WorkbenchMarkerResolution {

	public CommonWorkbenchMarkerResolution(IMarker marker) {
		this.marker = marker;
	}

	public void run(IMarker marker) {
		resolve(marker);

		ComponentUtil.validateFile((IFile)marker.getResource(), new NullProgressMonitor());
	}

	protected void openEditor(IFile file, int offset, int length) throws PartInitException {
		IWorkbenchPage page = UIUtil.getActivePage();

		ITextEditor editor = (ITextEditor)IDE.openEditor(page, file);

		editor.selectAndReveal(offset, length);
	}

	protected abstract void resolve(IMarker marker);

	protected final IMarker marker;

}