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
import org.eclipse.osgi.util.NLS;
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
                    IFile file = src.getFile( "content/Language.properties" ); //$NON-NLS-1$

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

        try
        {
            langFile.refreshLocal( IResource.DEPTH_INFINITE, monitor );
        }
        catch( Exception e )
        {
            PortletUIPlugin.logError( e );
        }

        String charset = langFile.getCharset( true );

        if( !"UTF-8".equals( charset ) ) //$NON-NLS-1$
        {
            String dialogMessage = NLS.bind( Msgs.languageFileCharacterSet, charset );

            MessageDialog dialog =
                new MessageDialog(
                    getDisplay().getActiveShell(), Msgs.incompatibleCharacterSet, getDisplay().getSystemImage(
                        SWT.ICON_WARNING ), dialogMessage, MessageDialog.WARNING, new String[] { Msgs.yes, Msgs.no,
                        Msgs.cancel }, 0 );

            int retval = dialog.open();

            if( retval == 0 )
            {
                langFile.setCharset( "UTF-8", monitor ); //$NON-NLS-1$

                String question = NLS.bind( Msgs.forcedEditFile, langFile.getName() );

                if( MessageDialog.openQuestion( getDisplay().getActiveShell(), Msgs.previewFile, question ) )
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

    private static class Msgs extends NLS
    {
        public static String cancel;
        public static String forcedEditFile;
        public static String incompatibleCharacterSet;
        public static String languageFileCharacterSet;
        public static String no;
        public static String previewFile;
        public static String yes;

        static
        {
            initializeMessages( BuildLanguagesAction.class.getName(), Msgs.class );
        }
    }
}
