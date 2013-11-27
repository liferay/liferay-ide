/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.ui;

import com.liferay.ide.project.core.util.ProjectUtil;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.PlatformUI;

/**
 * @author Kuo Zhang
 */
public class LanguageFileEncodingNotDefaultResolution implements IMarkerResolution
{

    public void run( IMarker marker )
    {
        if( marker.getResource() instanceof IProject )
        {
            final IProject proj = (IProject) marker.getResource();

            try
            {
                PlatformUI.getWorkbench().getProgressService().run
                (
                    true, true,
                    new IRunnableWithProgress()
                    {
                        public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                        {
                            monitor.beginTask( "Encoding Liferay Language File to Default (UTF-8)... ", 10 );

                            ProjectUtil.encodeLanguagePropertiesFilesToDefault( proj, monitor );

                            monitor.done();
                        }
                    }
                );
            }
            catch( Exception e )
            {
                ProjectUIPlugin.logError( e );
            }
        }
    }

    public String getLabel()
    {
        return "Encode Language Files to Default (UTF-8).";
    }

}
