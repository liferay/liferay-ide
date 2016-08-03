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

package com.liferay.ide.project.ui.upgrade.action;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

import com.liferay.ide.project.ui.migration.OpenJavaProjectSelectionDialogAction;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class CompileAction extends OpenJavaProjectSelectionDialogAction
{

    public CompileAction( String text, Shell shell )
    {
        super( text, shell );
    }

    @Override
    public void run()
    {
        final ISelection selection = getSelectionProjects();

        if( selection != null && selection instanceof IStructuredSelection )
        {
            Object[] projects = ( (IStructuredSelection) selection ).toArray();

            try
            {
                SDK sdk = SDKUtil.getWorkspaceSDK();

                PlatformUI.getWorkbench().getProgressService().busyCursorWhile( new IRunnableWithProgress()
                {

                    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                    {
                        for( Object project : projects )
                        {
                            if( project instanceof IProject )
                            {
                                IProject p = (IProject) project;

                                sdk.war( p, null, false, monitor );

                                ProcessConsole pc = (ProcessConsole) getConsole( p.getName() );

                                if( pc.getDocument().get().contains( "BUILD FAILED" ) )
                                {
                                    return;
                                }
                            }
                        }
                    }
                } );
            }
            catch( Exception e )
            {
            }
        }
    }

    public static IConsole getConsole( String name )
    {
        ConsolePlugin plugin = ConsolePlugin.getDefault();

        IConsoleManager conMan = plugin.getConsoleManager();

        IConsole[] existing = conMan.getConsoles();

        for( int i = 0; i < existing.length; i++ )
        {
            if( ( existing[i].getName() ).contains( name ) )
            {
                return existing[i];
            }
        }

        return null;
    }

}
