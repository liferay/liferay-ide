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
 * Contributors:
 *      Kamesh Sampath - initial implementation
 *      Gregory Amerson - initial implemenation and ongoing maintanence
 *******************************************************************************/

package com.liferay.ide.portlet.ui.editor.internal;

import static com.liferay.ide.core.model.internal.GenericResourceBundlePathService.RB_FILE_EXTENSION;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.util.PortletUtil;
import com.liferay.ide.portlet.ui.PortletUIPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.ui.SapphireJumpActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author Kamesh Sampath
 */
public class ResourceBundleJumpActionHandler extends SapphireJumpActionHandler
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler#computeEnablementState()
     */
    @Override
    protected boolean computeEnablementState()
    {
        final Element element = getModelElement();
        IProject project = element.adapt( IProject.class );

        final ValueProperty property = (ValueProperty) property().definition();

        final String text = element.property( property ).text( true );
        boolean isEnabled = super.computeEnablementState();
        if( isEnabled && text != null )
        {
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
            final IWorkspaceRoot wroot = workspace.getRoot();
            final IClasspathEntry[] cpEntries = CoreUtil.getClasspathEntries( project );
            String ioFileName = PortletUtil.convertJavaToIoFileName( text, RB_FILE_EXTENSION );
            for( IClasspathEntry iClasspathEntry : cpEntries )
            {
                if( IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind() )
                {
                    IPath entryPath = wroot.getFolder( iClasspathEntry.getPath() ).getLocation();
                    entryPath = entryPath.append( ioFileName );
                    IFile resourceBundleFile = wroot.getFileForLocation( entryPath );
                    if( resourceBundleFile != null && resourceBundleFile.exists() )
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
     */
    @Override
    protected Object run( SapphireRenderingContext context )
    {

        final Element element = getModelElement();

        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        final ValueProperty property = (ValueProperty) property().definition();

        final IProject project = element.adapt( IProject.class );

        final Value<Path> value = element.property( property );

        final String text = value.text( false );

        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IWorkspaceRoot wroot = workspace.getRoot();
        final IClasspathEntry[] cpEntries = CoreUtil.getClasspathEntries( project );
        String ioFileName = PortletUtil.convertJavaToIoFileName( text, RB_FILE_EXTENSION );

        for( IClasspathEntry iClasspathEntry : cpEntries )
        {
            if( IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind() )
            {
                IPath entryPath = wroot.getFolder( iClasspathEntry.getPath() ).getLocation();
                entryPath = entryPath.append( ioFileName );
                IFile resourceBundleFile = wroot.getFileForLocation( entryPath );
                if( resourceBundleFile != null && resourceBundleFile.exists() )
                {
                    if( window != null )
                    {
                        final IWorkbenchPage page = window.getActivePage();
                        IEditorDescriptor editorDescriptor = null;

                        try
                        {
                            editorDescriptor = IDE.getEditorDescriptor( resourceBundleFile.getName() );
                        }
                        catch( PartInitException e )
                        {
                            // No editor was found for this file type.
                        }

                        if( editorDescriptor != null )
                        {
                            try
                            {
                                IDE.openEditor( page, resourceBundleFile, editorDescriptor.getId(), true );
                            }
                            catch( PartInitException e )
                            {
                                PortletUIPlugin.logError( e );
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
