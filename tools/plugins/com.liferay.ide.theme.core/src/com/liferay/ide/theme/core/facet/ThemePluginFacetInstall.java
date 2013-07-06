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

package com.liferay.ide.theme.core.facet;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.facet.PluginFacetInstall;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.theme.core.ThemeCSSBuilder;
import com.liferay.ide.theme.core.ThemeCore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
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
 * @author Kamesh Sampath [IDE-450]
 * @author Cindy Li
 */
public class ThemePluginFacetInstall extends PluginFacetInstall
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
            // get the template zip for theme and extract into the project
            SDK sdk = getSDK();

            String themeName = this.masterModel.getStringProperty( THEME_NAME );

            // FIX IDE-450
            if( themeName.endsWith( ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX ) )
            {
                themeName = themeName.substring( 0, themeName.indexOf( ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX ) );
            }
            // END FIX IDE-450

            String displayName = this.masterModel.getStringProperty( DISPLAY_NAME );

            IPath newThemePath = sdk.createNewThemeProject( themeName, displayName );

            processNewFiles( newThemePath.append( themeName + ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX ) );

            // cleanup files
            FileUtil.deleteDir( newThemePath.toFile(), true );

            // delete META-INF
            CoreUtil.deleteResource( project.findMember( ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/META-INF" ) ); //$NON-NLS-1$
        }
        else if( shouldSetupDefaultOutputLocation() )
        {
            setupDefaultOutputLocation();
        }

        // IDE-925 commented method out below to allow for src/resources-importer
        // removeUnneededClasspathEntries();
        // IDE-925 also added configuration of deployment assembly to map WEB-INF/src to WEB-INF/classes
        if( shouldConfigureDeploymentAssembly() )
        {
            // IDE-565
            configureDeploymentAssembly( IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER, DEFAULT_DEPLOY_PATH );
        }

        IResource libRes = CoreUtil.getDefaultDocrootFolder( project ).findMember( "WEB-INF/lib" ); //$NON-NLS-1$

        if( libRes != null && libRes.exists() )
        {
            IFolder libFolder = (IFolder) libRes;
            IResource[] libFiles = libFolder.members( true );

            if( CoreUtil.isNullOrEmpty( libFiles ) )
            {
                libRes.delete( true, monitor );
            }
        }

        if( shouldUpdateBuildXml() )
        {
            updateBuildXml( project );
        }

        if( shouldInstallThemeBuilder() )
        {
            installThemeBuilder( this.project );
        }

        try
        {
            this.project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
        }
        catch( Exception e )
        {
            ThemeCore.logError( e );
        }
    }

    @Override
    protected String getDefaultOutputLocation()
    {
        return IPluginFacetConstants.THEME_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER;
    }

    public String getTemplateExtension( String tplFramework )
    {
        Map<String, String> tplMap = new HashMap<String, String>();

        tplMap.put( "Velocity", "vm" ); //$NON-NLS-1$ //$NON-NLS-2$
        tplMap.put( "Freemarker", "ftl" ); //$NON-NLS-1$ //$NON-NLS-2$
        tplMap.put( "JSP", "jsp" ); //$NON-NLS-1$ //$NON-NLS-2$

        return tplMap.get( tplFramework );
    }

    protected void installThemeBuilder( IProject project ) throws CoreException
    {
        if( project == null )
        {
            return;
        }

        IProjectDescription desc = project.getDescription();
        ICommand[] commands = desc.getBuildSpec();

        for( ICommand command : commands )
        {
            if( ThemeCSSBuilder.ID.equals( command.getBuilderName() ) )
            {
                return;
            }
        }

        // add builder to project
        ICommand command = desc.newCommand();
        command.setBuilderName( ThemeCSSBuilder.ID );
        ICommand[] nc = new ICommand[commands.length + 1];

        // Add it before other builders.
        System.arraycopy( commands, 0, nc, 1, commands.length );
        nc[0] = command;
        desc.setBuildSpec( nc );

        project.setDescription( desc, null );
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

        }
    }

    protected boolean shouldInstallThemeBuilder()
    {
        return this.model.getBooleanProperty( INSTALL_THEME_CSS_BUILDER );
    }

    protected boolean shouldUpdateBuildXml()
    {
        return this.model.getBooleanProperty( UPDATE_BUILD_XML );
    }

    public void updateBuildXml( IProject project ) throws CoreException
    {
        String themeParent = this.masterModel.getStringProperty( THEME_PARENT );
        String tplFramework = this.masterModel.getStringProperty( THEME_TEMPLATE_FRAMEWORK );

        if( ( themeParent != null && !themeParent.equals( this.masterModel.getDefaultProperty( THEME_PARENT ) ) ) ||
            ( tplFramework != null && !tplFramework.equals( this.masterModel.getDefaultProperty( THEME_TEMPLATE_FRAMEWORK ) ) ) )
        {
            IFile buildXml = project.getFile( "build.xml" ); //$NON-NLS-1$
            InputStream inputStream = buildXml.getContents();

            try
            {
                String strCon = CoreUtil.readStreamToString( inputStream );
                inputStream.close();

                if( !themeParent.equals( this.masterModel.getDefaultProperty( THEME_PARENT ) ) )
                {
                    strCon = strCon.replace(
                            "<property name=\"theme.parent\" value=\"_styled\" />", "<property name=\"theme.parent\" value=\"" + themeParent + "\" />" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }

                if( !tplFramework.equals( this.masterModel.getDefaultProperty( THEME_TEMPLATE_FRAMEWORK ) ) )
                {
                    String tplExtension = getTemplateExtension( tplFramework );
                    strCon = strCon.replace(
                            "</project>", "\t<property name=\"theme.type\" value=\"" + tplExtension + "\" />\n</project>" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }

                buildXml.setContents( new ByteArrayInputStream( strCon.getBytes() ), IResource.FORCE, null );
            }
            catch( IOException e )
            {
                ThemeCore.logError( e );
            }
        }
    }

}
