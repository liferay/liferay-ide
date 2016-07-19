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

package com.liferay.ide.project.ui.upgrade;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.liferay.ide.project.core.ProjectCore;

/**
 * @author Terry Jia
 */
public class CodeUpgradeHandler extends AbstractHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        final IPath stateLocation = ProjectCore.getDefault().getStateLocation();

        File stateDir = stateLocation.toFile();

        final File codeUpgradeFile = new File( stateDir, "liferay-code-upgrade.xml" );

        try
        {
            if( codeUpgradeFile.exists() )
            {
                codeUpgradeFile.delete();
            }

            codeUpgradeFile.createNewFile();

            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            IFileStore fileStore = EFS.getLocalFileSystem().getStore( new Path( codeUpgradeFile.getPath() ) );

            IDE.openEditorOnFileStore( page, fileStore );
        }
        catch( Exception e )
        {
        }

        return null;
    }

}
