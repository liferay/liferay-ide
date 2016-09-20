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

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author Terry Jia
 */
public class RerunActionHandler extends SapphireActionHandler
{

    @Override
    protected Object run( Presentation context )
    {
        Boolean openNewLiferayProjectWizard = MessageDialog.openQuestion(
            UIUtil.getActiveShell(), "re-run code upgrade tool?", "Do you want to re-run the code upgrade tool?" );

        if( openNewLiferayProjectWizard )
        {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            IEditorPart editor = page.getActiveEditor();

            page.closeEditor( editor, false );

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

                IFileStore fileStore = EFS.getLocalFileSystem().getStore( new Path( codeUpgradeFile.getPath() ) );

                IDE.openEditorOnFileStore( page, fileStore );
            }
            catch( Exception e )
            {
            }
        }

        return null;
    }

}
