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

package com.liferay.ide.project.core.facet;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.ISDKConstants;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.server.util.ServerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.w3c.dom.Element;

/**
 * @author Greg Amerson
 * @author kamesh.sampath
 */
@SuppressWarnings( "restriction" )
public class ExtPluginFacetInstall extends PluginFacetInstall
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
            // get the template zip for portlets and extract into the project
            SDK sdk = getSDK();

            String extName = this.masterModel.getStringProperty( EXT_NAME );

            // FIX IDE-450
            if( extName.endsWith( ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX ) )
            {
                extName = extName.substring( 0, extName.indexOf( ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX ) );
            }
            // END FIX IDE-450

            String displayName = this.masterModel.getStringProperty( DISPLAY_NAME );

            Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( project );

            IPath newExtPath = sdk.createNewExtProject( extName, displayName, appServerProperties );

            IPath tempInstallPath = newExtPath.append( extName + ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX );

            processNewFiles( tempInstallPath, false );
            // cleanup ext temp files
            FileUtil.deleteDir( tempInstallPath.toFile(), true );

            this.project.refreshLocal( IResource.DEPTH_INFINITE, monitor );

            deleteFolder( this.project.getFolder( "docroot/WEB-INF/src" ) );
            deleteFolder( this.project.getFolder( "docroot/WEB-INF/classes" ) );
            deleteFolder( this.project.getFolder( "docroot/WEB-INF/ext-web/docroot/WEB-INF/lib" ) );
        }

        IJavaProject javaProject = JavaCore.create( project );

        List<IClasspathEntry> existingRawClasspath = Arrays.asList( javaProject.getRawClasspath() );

        List<IClasspathEntry> newRawClasspath = new ArrayList<IClasspathEntry>();

        // first lets add all new source folders
        for( int i = 0; i < IPluginFacetConstants.EXT_PLUGIN_SDK_SOURCE_FOLDERS.length; i++ )
        {
            IPath sourcePath =
                this.project.getFolder( IPluginFacetConstants.EXT_PLUGIN_SDK_SOURCE_FOLDERS[i] ).getFullPath();

            IPath outputPath =
                this.project.getFolder( IPluginFacetConstants.EXT_PLUGIN_SDK_OUTPUT_FOLDERS[i] ).getFullPath();

            IClasspathAttribute[] attributes =
                new IClasspathAttribute[] { JavaCore.newClasspathAttribute( "owner.project.facets", "liferay.ext" ) };

            IClasspathEntry sourceEntry =
                JavaCore.newSourceEntry( sourcePath, new IPath[0], new IPath[0], outputPath, attributes );

            newRawClasspath.add( sourceEntry );
        }

        // next add all previous classpath entries except for source folders
        for( IClasspathEntry entry : existingRawClasspath )
        {
            if( entry.getEntryKind() != IClasspathEntry.CPE_SOURCE )
            {
                newRawClasspath.add( entry );
            }
        }

        javaProject.setRawClasspath(
            newRawClasspath.toArray( new IClasspathEntry[0] ),
            this.project.getFolder( IPluginFacetConstants.EXT_PLUGIN_SDK_OUTPUT_FOLDERS[0] ).getFullPath(), null );

        ProjectUtil.fixExtProjectSrcFolderLinks( this.project );

        // fixTilesDefExtFile();
    }

    protected void deleteFolder( IFolder folder ) throws CoreException
    {

        if( folder != null && folder.exists() )
        {
            folder.delete( true, null );
        }
    }

    protected void fixTilesDefExtFile()
    {
        IFile tilesDefExtFile = this.project.getFile( "docroot/WEB-INF/ext-web/docroot/WEB-INF/tiles-defs-ext.xml" );

        if( tilesDefExtFile.exists() )
        {
            try
            {
                IDOMModel domModel =
                    (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit( tilesDefExtFile );

                domModel.aboutToChangeModel();

                IDOMDocument document = domModel.getDocument();

                Element root = document.getDocumentElement();

                Element def = document.createElement( "definition" );

                def.setAttribute( "name", "" );

                root.appendChild( def );
                root.appendChild( document.createTextNode( "\n" ) );

                new FormatProcessorXML().formatNode( def );

                domModel.changedModel();
                domModel.save();
                domModel.releaseFromEdit();

                tilesDefExtFile.refreshLocal( IResource.DEPTH_INFINITE, null );
            }
            catch( Exception e )
            {
                ProjectCorePlugin.logError( e );
            }
        }
    }

    @Override
    protected String getDefaultOutputLocation()
    {
        return IPluginFacetConstants.EXT_PLUGIN_DEFAULT_OUTPUT_FOLDER;
    }

}
