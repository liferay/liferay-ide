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

package com.liferay.ide.project.ui.tests;

import com.liferay.ide.project.core.tests.ProjectCoreBase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * @author Li Lu
 */
public class ProjectUITestBase extends ProjectCoreBase
{

    protected void addProjectToServer( IProject project ) throws CoreException
    {
        IModule module = ServerUtil.getModule( project );

        if( ServerUtil.containsModule( server, module, new NullProgressMonitor() ) )
        {
            return;
        }
        IServerWorkingCopy copy = server.createWorkingCopy();

        copy.modifyModules( new IModule[] { module }, new IModule[0], new NullProgressMonitor() );

        server = copy.save( true, new NullProgressMonitor() );
    }

    public boolean checkConsoleMessage( CharSequence expectedMessage ) throws Exception
    {
        TextConsole serverConsole = (TextConsole) getConsole( server.getName() );

        long timeoutExpiredMs = System.currentTimeMillis() + 20000;

        while( true )
        {
            Thread.sleep( 500 );

            IDocument content = serverConsole.getDocument();

            if( content.get().contains( expectedMessage ) )
            {
                return true;
            }

            if( System.currentTimeMillis() >= timeoutExpiredMs )
            {
                return false;
            }
        }
    }

    public static IConsole getConsole( String name )
    {
        ConsolePlugin plugin = ConsolePlugin.getDefault();

        IConsoleManager conMan = plugin.getConsoleManager();

        IConsole[] existing = conMan.getConsoles();

        for( int i = 0; i < existing.length; i++ )
            if( ( existing[i].getName() ).contains( name ) )
                return existing[i];

        return null;
    }

}
