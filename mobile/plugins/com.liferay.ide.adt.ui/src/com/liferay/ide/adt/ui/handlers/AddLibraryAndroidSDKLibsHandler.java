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
package com.liferay.ide.adt.ui.handlers;

import com.liferay.ide.adt.core.ADTUtil;
import com.liferay.ide.adt.ui.ADTUI;
import com.liferay.mobile.sdk.core.MobileSDKCore;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author Gregory Amerson
 */
public class AddLibraryAndroidSDKLibsHandler extends AbstractHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        IStatus retval = null;

        final ISelection selection = HandlerUtil.getCurrentSelection( event );

        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            Object selected = structuredSelection.getFirstElement();

            IJavaProject project = null;

            if( selected instanceof IResource )
            {
                project = JavaCore.create( ( (IResource) selected ).getProject() );
            }
            else if (selected instanceof IJavaProject )
            {
                project = (IJavaProject) selected;
            }

            if( project != null )
            {
                try
                {
                    final Map<String, File[]> libmap = MobileSDKCore.getLibraryMap();

                    final File[] libs = libmap.get( "liferay-android-sdk-6.2.0.1" );

                    final NullProgressMonitor npm = new NullProgressMonitor();

                    ADTUtil.addLibsToAndroidProject(
                        project.getProject(), Collections.singletonList( libs ), npm );

                    project.getProject().refreshLocal( IResource.DEPTH_INFINITE, npm );

                    MessageDialog.openInformation(
                        HandlerUtil.getActiveShellChecked( event ), "Liferay Mobile SDK",
                        "Successfully added Liferay Android SDK libraries to project." );
                }
                catch( CoreException e )
                {
                    retval = ADTUI.createErrorStatus( "Could not add libraries to Android project", e );
                }
            }
        }

        return retval == null ? Status.OK_STATUS : retval;
    }


}
