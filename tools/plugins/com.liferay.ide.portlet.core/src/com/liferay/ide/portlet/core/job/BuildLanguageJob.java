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

package com.liferay.ide.portlet.core.job;

import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKJob;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * @author Greg Amerson
 */
public class BuildLanguageJob extends SDKJob
{

    protected IFile langFile;

    public BuildLanguageJob( IFile langFile )
    {
        super( Msgs.buildLanguages );

        this.langFile = langFile;

        setUser( true );

        setProject( langFile.getProject() );
    }

    @Override
    protected IStatus run( IProgressMonitor monitor )
    {
        monitor.beginTask( Msgs.buildingLanguages, 100 );

        IWorkspaceDescription desc = ResourcesPlugin.getWorkspace().getDescription();

        boolean saveAutoBuild = desc.isAutoBuilding();

        desc.setAutoBuilding( false );

        try
        {
            ResourcesPlugin.getWorkspace().setDescription( desc );

            SDK sdk = getSDK();

            IProject project = getProject();

            monitor.worked( 10 );

            sdk.buildLanguage( project, langFile, null, ServerUtil.configureAppServerProperties( project ) );

            monitor.worked( 90 );

            try
            {
                project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
            }
            catch( Exception e )
            {
                PortletCore.logError( e );
            }

            project.build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );

            try
            {
                project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
            }
            catch( Exception e )
            {
                PortletCore.logError( e );
            }

            // check generated properties files and set to UTF8
            for( IResource file : langFile.getParent().members() )
            {
                if( file.getName().matches( "Language_.*\\.properties" ) ) //$NON-NLS-1$
                {
                    IFile generatedLangFile = (IFile) file;
                    generatedLangFile.setCharset( "UTF-8", monitor ); //$NON-NLS-1$
                }
            }
        }
        catch( CoreException e1 )
        {
            return PortletCore.createErrorStatus( e1 );
        }
        finally
        {
            desc = ResourcesPlugin.getWorkspace().getDescription();
            desc.setAutoBuilding( saveAutoBuild );

            try
            {
                ResourcesPlugin.getWorkspace().setDescription( desc );
            }
            catch( CoreException e1 )
            {
                return PortletCore.createErrorStatus( e1 );
            }
        }

        return Status.OK_STATUS;
    }

    private static class Msgs extends NLS
    {
        public static String buildingLanguages;
        public static String buildLanguages;

        static
        {
            initializeMessages( BuildLanguageJob.class.getName(), Msgs.class );
        }
    }
}
