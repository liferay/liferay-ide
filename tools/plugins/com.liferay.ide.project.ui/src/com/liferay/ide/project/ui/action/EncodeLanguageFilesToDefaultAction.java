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

package com.liferay.ide.project.ui.action;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUIPlugin;

/**
 * @author Kuo Zhang
 */
public class EncodeLanguageFilesToDefaultAction implements IObjectActionDelegate
{

    private ISelection selection;

    public EncodeLanguageFilesToDefaultAction()
    {

    }

    public void run( IAction action )
    {
        if( selection instanceof IStructuredSelection )
        {
            final Object elem = ( (IStructuredSelection) selection ).toArray()[0];

            if( elem instanceof IProject )
            {
                try
                {
                    new ProgressMonitorDialog( new Shell() ).run( true, false, new IRunnableWithProgress()
                    {

                        public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                            InterruptedException
                        {
                            monitor.beginTask( "Encoding Liferay Language File to Default (UTF-8)... ", 10 );

                            ProjectUtil.encodeLanguageFilesToDefault( (IProject) elem, monitor );

                            monitor.done();
                        }

                    } );
                }
                catch( Exception e )
                {
                    ProjectUIPlugin.logError( e );
                }
            }
        }
    }

    public void selectionChanged( IAction action, ISelection selection )
    {
        this.selection = selection;
    }

    public void setActivePart( IAction action, IWorkbenchPart targetPart )
    {

    }

}
