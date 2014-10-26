/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay.ide.xml.search.ui;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;


/**
 * @author Gregory Amerson
 */
public abstract class CommonWorkbenchMarkerResolution extends WorkbenchMarkerResolution
{
    protected final IMarker marker;

    public CommonWorkbenchMarkerResolution( IMarker marker )
    {
        this.marker = marker;
    }

    protected void openEditor( IFile file ) throws PartInitException
    {
        final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        IDE.openEditor( page, file );
    }

    protected abstract void resolve( IMarker marker );

    public void run( IMarker marker )
    {
        resolve( marker );

        CoreUtil.validateFile( (IFile) marker.getResource(), new NullProgressMonitor() );
    }

}
