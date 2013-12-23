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

package com.liferay.ide.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.PropertiesUtil;

import org.eclipse.core.resources.IFile;
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

    protected void processFile( IFile file ) throws CoreException
    {
        if( file != null && file.exists() )
        {
            if( PropertiesUtil.isLanguagePropertiesFile( file ) )
            {
                validateLanguagePropertiesEncoding( new IFile[]{file}, false );
                return;
            }

            final IFile portletXml = PropertiesUtil.getPortletXml( CoreUtil.getLiferayProject( file ) );
            if( file.equals( portletXml ) )
            {
                IFile[] files = PropertiesUtil.getLanguagePropertiesFromPortletXml( portletXml );
                validateLanguagePropertiesEncoding( files, true );
                return;
            }

            final IFile liferayHookXml = PropertiesUtil.getLiferayHookXml( CoreUtil.getLiferayProject( file ) );
            if( ( file ).equals( liferayHookXml ) )
            {
                IFile[] files = PropertiesUtil.getLanguagePropertiesFromLiferayHookXml( liferayHookXml );
                validateLanguagePropertiesEncoding( files, true );
                return;
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

    private void validateLanguagePropertiesEncoding( final IFile[] files, final boolean clearUnusedMarkers )
    {
        Job job = new WorkspaceJob( "Valiting the encoding of Liferay language properties." )
        {

            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
            {
                for( IFile file : files )
                {
                    LiferayLanguagePropertiesValidator.getValidator( file ).validateEncoding(); 
                }

                if( clearUnusedMarkers )
                {
                    LiferayLanguagePropertiesValidator.clearUnusedValidators();
                }

                return Status.OK_STATUS;
            }
        };

        job.setRule( CoreUtil.getWorkspaceRoot() );
        job.schedule();
    }

}
