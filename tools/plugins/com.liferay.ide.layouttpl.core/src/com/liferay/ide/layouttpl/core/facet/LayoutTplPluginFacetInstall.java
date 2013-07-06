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
 *    Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.layouttpl.core.facet;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.layouttpl.core.LayoutTplCore;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.facet.PluginFacetInstall;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.server.util.ServerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author Greg Amerson
 * @author Kamesh Sampath - [IDE-450]
 */
public class LayoutTplPluginFacetInstall extends PluginFacetInstall
{

    @Override
    public void execute( IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor )
        throws CoreException
    {
        super.execute( project, fv, config, monitor );

        IDataModel model = (IDataModel) config;
        IDataModel masterModel = (IDataModel) model.getProperty( FacetInstallDataModelProvider.MASTER_PROJECT_DM );

        if( masterModel != null && masterModel.getBooleanProperty( CREATE_PROJECT_OPERATION ) )
        {
            // get the template zip for layouttpl and extract into the project
            SDK sdk = getSDK();

            String layoutTplName = this.masterModel.getStringProperty( LAYOUTTPL_NAME );
            // FIX IDE-450
            if( layoutTplName.endsWith( ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX ) )
            {
                layoutTplName =
                    layoutTplName.substring( 0, layoutTplName.indexOf( ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX ) );
            }
            // END FIX IDE-450

            String displayName = this.masterModel.getStringProperty( DISPLAY_NAME );

            Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( project );

            IPath newLayoutTplPath = sdk.createNewLayoutTplProject( layoutTplName, displayName, appServerProperties );

            processNewFiles( newLayoutTplPath.append( layoutTplName + ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX ) );

            // cleanup files
            FileUtil.deleteDir( newLayoutTplPath.toFile(), true );
        }
        else if( shouldSetupDefaultOutputLocation() )
        {
            setupDefaultOutputLocation();
        }

        removeUnneededClasspathEntries();

        final IFolder folder = CoreUtil.getDefaultDocrootFolder( project );

        if( folder != null && folder.exists() )
        {
            IResource libRes = folder.findMember( "WEB-INF/lib" ); //$NON-NLS-1$

            if( libRes != null && libRes.exists() )
            {
                IFolder libFolder = (IFolder) libRes;
                IResource[] libFiles = libFolder.members( true );

                if( CoreUtil.isNullOrEmpty( libFiles ) )
                {
                    libRes.delete( true, monitor );
                }
            }
        }

        try
        {
            this.project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
        }
        catch( Exception e )
        {
            LayoutTplCore.logError( e );
        }
    }

    @Override
    protected String getDefaultOutputLocation()
    {
        return IPluginFacetConstants.LAYOUTTPL_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER;
    }

    protected void removeUnneededClasspathEntries()
    {
        IFacetedProjectWorkingCopy facetedProject = getFacetedProject();
        IJavaProject javaProject = JavaCore.create( facetedProject.getProject() );

        try
        {
            IClasspathEntry[] existingClasspath = javaProject.getRawClasspath();
            List<IClasspathEntry> newClasspath = new ArrayList<IClasspathEntry>();

            for( IClasspathEntry entry : existingClasspath )
            {
                if( entry.getEntryKind() == IClasspathEntry.CPE_SOURCE )
                {
                    continue;
                }
                else if( entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER )
                {
                    String path = entry.getPath().toPortableString();

                    if( path.contains( "org.eclipse.jdt.launching.JRE_CONTAINER" ) || //$NON-NLS-1$
                        path.contains( "org.eclipse.jst.j2ee.internal.web.container" ) || //$NON-NLS-1$
                        path.contains( "org.eclipse.jst.j2ee.internal.module.container" ) ) //$NON-NLS-1$
                    {
                        continue;
                    }
                }

                newClasspath.add( entry );
            }

            javaProject.setRawClasspath( newClasspath.toArray( new IClasspathEntry[0] ), null );

            IResource sourceFolder =
                javaProject.getProject().findMember( IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER );

            if( sourceFolder.exists() )
            {
                sourceFolder.delete( true, null );
            }
        }
        catch( Exception e )
        {
            // no need to report errors
        }
    }

    @Override
    protected boolean shouldInstallPluginLibraryDelegate()
    {
        return false;
    }

}
