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
 *      Gregory Amerson - initial implementation review and ongoing maintanence
 *******************************************************************************/

package com.liferay.ide.portlet.ui.editor.internal;

import com.liferay.ide.core.model.internal.GenericResourceBundlePathService;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.util.PortletUtil;
import com.liferay.ide.portlet.ui.PortletUIPlugin;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.forms.PropertyEditorActionHandler;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;

/**
 * @author Kamesh Sampath
 */
public abstract class AbstractResourceBundleActionHandler extends PropertyEditorActionHandler
{

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IWorkspaceRoot wroot = workspace.getRoot();
    protected Listener listener;

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler#computeEnablementState()
     */
    @Override
    protected boolean computeEnablementState()
    {
        boolean isEnabled = super.computeEnablementState();

        if( isEnabled )
        {
            final Element element = getModelElement();
            final Property property = property();
            final IProject project = element.adapt( IProject.class );
            String rbFile = element.property( (ValueProperty) property.definition() ).text();
            if( rbFile != null )
            {
                String ioFileName =
                    PortletUtil.convertJavaToIoFileName( rbFile, GenericResourceBundlePathService.RB_FILE_EXTENSION );
                isEnabled = !getFileFromClasspath( project, ioFileName );
            }
            else
            {
                isEnabled = false;
            }
        }

        return isEnabled;
    }

    /**
     * @param project
     * @param ioFileName
     * @return
     */
    protected final boolean getFileFromClasspath( IProject project, String ioFileName )
    {

        IClasspathEntry[] cpEntries = CoreUtil.getClasspathEntries( project );
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
                else
                {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * @param project
     * @param ioFileName
     * @return
     */
    protected final IFolder getResourceBundleFolderLocation( IProject project, String ioFileName )
    {

        IClasspathEntry[] cpEntries = CoreUtil.getClasspathEntries( project );
        for( IClasspathEntry iClasspathEntry : cpEntries )
        {
            if( IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind() )
            {
                IFolder srcFolder = wroot.getFolder( iClasspathEntry.getPath() );
                IPath rbSourcePath = srcFolder.getLocation();
                rbSourcePath = rbSourcePath.append( ioFileName );
                IFile resourceBundleFile = wroot.getFileForLocation( rbSourcePath );
                if( resourceBundleFile != null )
                {
                    return srcFolder;
                }
            }
        }
        return null;
    }

    /**
     * @param packageName
     * @param rbFiles
     * @param rbFileBuffer
     */
    protected final void createFiles(
        final Presentation context, final IProject project, final String packageName,
        final List<IFile> rbFiles, final StringBuilder rbFileBuffer )
    {
        if( !rbFiles.isEmpty() )
        {
            final int workUnit = rbFiles.size() + 2;
            final IRunnableWithProgress rbCreationProc = new IRunnableWithProgress()
            {

                public void run( final IProgressMonitor monitor ) throws InvocationTargetException,
                    InterruptedException
                {
                    monitor.beginTask( StringPool.EMPTY, workUnit );
                    try
                    {
                        IJavaProject javaProject = JavaCore.create( project );
                        IPackageFragmentRoot pkgSrc = PortletUtil.getSourceFolder( javaProject );
                        IPackageFragment rbPackageFragment = pkgSrc.getPackageFragment( packageName );
                        if( rbPackageFragment != null && !rbPackageFragment.exists() )
                        {
                            pkgSrc.createPackageFragment( packageName, true, monitor );
                        }
                        monitor.worked( 1 );
                        ListIterator<IFile> rbFilesIterator = rbFiles.listIterator();
                        while( rbFilesIterator.hasNext() )
                        {

                            IFile rbFile = rbFilesIterator.next();

                            rbFile.create(
                                new ByteArrayInputStream( rbFileBuffer.toString().getBytes() ), true, monitor );
                            monitor.worked( 1 );
                        }

                        project.refreshLocal( IResource.DEPTH_INFINITE, monitor );

                    }
                    catch( CoreException e )
                    {
                        PortletUIPlugin.logError( e );
                    }
                    finally
                    {
                        monitor.done();
                    }

                }
            };

            try
            {
                ( new ProgressMonitorDialog( ( (SwtPresentation) context ).shell() ) ).run(
                    false, false, rbCreationProc );
                rbFiles.clear();
            }
            catch( InvocationTargetException e )
            {
                PortletUIPlugin.logError( e );
            }
            catch( InterruptedException e )
            {
                PortletUIPlugin.logError( e );
            }
        }
    }

}
