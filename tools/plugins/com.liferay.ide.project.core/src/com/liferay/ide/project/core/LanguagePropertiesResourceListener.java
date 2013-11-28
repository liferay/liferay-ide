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

package com.liferay.ide.project.core;

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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.internal.ValType;
import org.eclipse.wst.validation.internal.ValidationRunner;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class LanguagePropertiesResourceListener implements IResourceChangeListener, IResourceDeltaVisitor
{

    protected void processPropertiesFile( IFile pluginPackagePropertiesFile ) throws CoreException
    {
        if( PropertiesUtil.isLanguagePropertiesFile( pluginPackagePropertiesFile ) )
        {
            validatePortletXmlAndLifeayHookXml( CoreUtil.getLiferayProject( pluginPackagePropertiesFile ) );
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
        catch( Throwable e )
        {
            e.printStackTrace();
            // ignore
        }
    }

    protected boolean shouldProcessResourceDelta( IResourceDelta delta )
    {
        if( delta.getResource().getType() == IResource.FILE )
        {
            if( PropertiesUtil.isLanguagePropertiesFile( (IFile) delta.getResource() ) )
            {
                return true;
            }
        }

        return false;
    }

    protected void validatePortletXmlAndLifeayHookXml( IProject project )
    {
        if( project != null )
        {
            final IFile portletXml = PropertiesUtil.getPortletXml( project );
            final IFile liferayHookXml = PropertiesUtil.getLiferayHookXml( project );

            try
            {
                if( portletXml != null )
                {
                    ValidationRunner.validate( portletXml, ValType.Manual, new NullProgressMonitor(), false );
                }

                if( liferayHookXml != null )
                {
                    ValidationRunner.validate( liferayHookXml, ValType.Manual, new NullProgressMonitor(), false );
                }
            }
            catch( CoreException e )
            {
                LiferayProjectCore.logError( e );
            }
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
                if( shouldProcessResourceDelta( delta ) )
                {
                    Job job = new WorkspaceJob( Msgs.processingLanguagePropertiesResource )
                    {

                        @Override
                        public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                        {
                            final IResource resource = delta.getResource();

                            processPropertiesFile( (IFile) resource );

                            return Status.OK_STATUS;
                        }
                    };

                    job.setRule( CoreUtil.getWorkspaceRoot() );
                    job.schedule();
                }

                return false;
            }
        }

        return false;
    }

    private static class Msgs extends NLS
    {

        public static String processingLanguagePropertiesResource;

        static
        {
            initializeMessages( LanguagePropertiesResourceListener.class.getName(), Msgs.class );
        }
    }

}
