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

package com.liferay.ide.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.PropertiesUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Kuo Zhang
 */
public class LiferayLanguagePropertiesListener implements IResourceChangeListener, IResourceDeltaVisitor
{
    public LiferayLanguagePropertiesListener()
    {
        new WorkspaceJob( "clear abondoned liferay language properties markers" )
        {
            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
            {
                LiferayLanguagePropertiesValidator.clearAbandonedMarkers();
                return Status.OK_STATUS;
            }
        }.schedule();
    }

    protected void processFile( IFile file ) throws CoreException
    {
        if( file != null && file.exists() )
        {
            if( PropertiesUtil.isLanguagePropertiesFile( file ) )
            {
                validateLanguagePropertiesEncoding( new IFile[]{file}, null );
                return;
            }

            final String filename = file.getName();

            if( filename.equals( ILiferayConstants.PORTLET_XML_FILE ) )
            {
                final ILiferayProject lrproject = LiferayCore.create( CoreUtil.getLiferayProject( file ) );

                if( lrproject != null )
                {
                    final IFile portletXml = lrproject.getDescriptorFile( ILiferayConstants.PORTLET_XML_FILE );

                    if( portletXml != null && file.equals( portletXml ) )
                    {
                        final IFile[] files = PropertiesUtil.getLanguagePropertiesFromPortletXml( portletXml );
                        validateLanguagePropertiesEncoding( files, CoreUtil.getLiferayProject( file ) );

                        return;
                    }
                }
            }

            if( filename.equals( ILiferayConstants.LIFERAY_HOOK_XML_FILE ) )
            {
                final ILiferayProject lrproject = LiferayCore.create( CoreUtil.getLiferayProject( file ) );

                if( lrproject != null )
                {
                    final IFile liferayHookXml = lrproject.getDescriptorFile( ILiferayConstants.LIFERAY_HOOK_XML_FILE );

                    if( file.equals( liferayHookXml ) )
                    {
                        final IFile[] files = PropertiesUtil.getLanguagePropertiesFromLiferayHookXml( liferayHookXml );
                        validateLanguagePropertiesEncoding( files, CoreUtil.getLiferayProject( file ) );

                        return;
                    }
                }
            }
        }
    }

    public void resourceChanged( IResourceChangeEvent event )
    {
        if( event == null )
        {
            return;
        }

        try
        {
            event.getDelta().accept( this );
        }
        catch( CoreException e )
        {
        }
    }

    public boolean visit( final IResourceDelta delta ) throws CoreException
    {
        switch( delta.getResource().getType() )
        {
            case IResource.ROOT:
            case IResource.PROJECT:
            case IResource.FOLDER:
                return true;

            case IResource.FILE:
            {
                processFile( (IFile) delta.getResource() );
                return false;
            }
        }

        return false;
    }

    private void validateLanguagePropertiesEncoding( final IFile[] files, final IProject project )
    {
        final Job job = new WorkspaceJob( "Validate Liferay language properties encoding..." )
        {
            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
            {
                if( files != null && files.length > 0 )
                {
                    for( IFile file : files )
                    {
                        LiferayLanguagePropertiesValidator.getValidator( file ).validateEncoding();
                    }
                }

                if( project != null )
                {
                    LiferayLanguagePropertiesValidator.clearUnusedValidatorsAndMarkers( project );
                }

                return Status.OK_STATUS;
            }
        };

        job.setRule( CoreUtil.getWorkspaceRoot() );
        job.schedule();
    }

}
