/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.portlet.ui.action;

import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.core.job.BuildLanguageJob;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.ui.action.AbstractObjectAction;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author Greg Amerson
 */
public class BuildLanguagesAction extends AbstractObjectAction
{

    public BuildLanguagesAction()
    {
        super();
    }

    public void run( IAction action )
    {
        IFile langFile = null;

        if( fSelection instanceof IStructuredSelection )
        {
            Object[] elems = ( (IStructuredSelection) fSelection ).toArray();

            Object elem = elems[0];

            if( elem instanceof IFile )
            {
                langFile = (IFile) elem;
            }
            else if( elem instanceof IProject )
            {
                IProject project = (IProject) elem;

                IFolder[] srcFolders = ProjectUtil.getSourceFolders( project );

                for( IFolder src : srcFolders )
                {
                    IFile file = src.getFile( "content/Language.properties" );

                    if( file.exists() )
                    {
                        langFile = file;
                        break;
                    }
                }
            }

            if( langFile.exists() )
            {
                try
                {
                    boolean shouldContinue = checkLanguageFileEncoding( langFile );

                    if( !shouldContinue )
                    {
                        return;
                    }
                }
                catch( Exception e2 )
                {
                    PortletUIPlugin.createErrorStatus( e2 );
                }

                BuildLanguageJob job = PortletCore.createBuildLanguageJob( langFile );

                job.schedule();
            }
        }

    }

    protected boolean checkLanguageFileEncoding( IFile langFile ) throws CoreException
    {
        IProgressMonitor monitor = new NullProgressMonitor();
        langFile.refreshLocal( IResource.DEPTH_INFINITE, monitor );
        String charset = langFile.getCharset( true );

        if( !"UTF-8".equals( charset ) )
        {
            String dialogMessage =
                "The language file character set is set to " +
                    charset +
                    " which could lead to unexpected results.  Liferay expects the file to be encoded as UTF-8.  Would you like to force Eclipse to recognize this file as UTF-8?";

            MessageDialog dialog =
                new MessageDialog(
                    getDisplay().getActiveShell(), "Incompatible Character Set", getDisplay().getSystemImage(
                        SWT.ICON_WARNING ), dialogMessage, MessageDialog.WARNING,
                    new String[] { "Yes", "No", "Cancel" }, 0 );

            int retval = dialog.open();

            if( retval == 0 )
            {
                langFile.setCharset( "UTF-8", monitor );

                String question =
                    langFile.getName() +
                        " has been forced to use UTF-8 encoding.  Would you like to edit the file to verify the contents?";

                if( MessageDialog.openQuestion( getDisplay().getActiveShell(), "Preview file", question ) )
                {
                    IDE.openEditor( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), langFile );
                }

                return false;
            }
            else if( retval == 2 )
            {
                return false;
            }
        }

        return true;
    }

}
