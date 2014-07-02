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
package com.liferay.ide.alloy.ui.action;

import com.liferay.ide.alloy.core.AlloyCore;
import com.liferay.ide.alloy.core.LautRunner;
import com.liferay.ide.ui.action.AbstractObjectAction;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;


/**
 * @author Gregory Amerson
 */
public class UpgradeToolAction extends AbstractObjectAction
{

    @Override
    public void run( IAction action )
    {
        if( fSelection instanceof IStructuredSelection )
        {
            Object[] elems = ( (IStructuredSelection) fSelection ).toArray();

            Object elem = elems[0];

            IProject project = null;

            if( elem instanceof IFile )
            {
                IFile projectFile = (IFile) elem;

                project = projectFile.getProject();
            }
            else if( elem instanceof IProject )
            {
                project = (IProject) elem;
            }

            if( project != null )
            {
                runLaut( project );
            }
        }
    }

    private void runLaut( final IProject project )
    {
        final LautRunner lautRunner = AlloyCore.getLautRunner();

        if( lautRunner == null )
        {
            // TODO IDE-1311 display error to user
        }
        else
        {
            if( lautRunner.hasUpdateAvailable() )
            {
                // TODO IDE-1311 prompt user
            }

            final IRunnableWithProgress runnable = new IRunnableWithProgress()
            {
                @Override
                public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                {
                    lautRunner.exec( project, monitor );
                }
            };

            try
            {
                PlatformUI.getWorkbench().getProgressService().run( true, false, runnable );
            }
            catch( Exception e )
            {
                AlloyCore.logError( "Unable to execute laut tool", e );
            }
        }
    }

}
