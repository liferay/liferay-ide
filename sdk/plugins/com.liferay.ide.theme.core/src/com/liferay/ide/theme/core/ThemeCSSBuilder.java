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

package com.liferay.ide.theme.core;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.sdk.ISDKConstants;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.sdk.util.SDKUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.theme.core.operation.ThemeDescriptorHelper;
import com.liferay.ide.theme.core.util.BuildHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings( "rawtypes" )
public class ThemeCSSBuilder extends IncrementalProjectBuilder
{

    public static final String ID = "com.liferay.ide.eclipse.theme.core.cssBuilder";
    public static final String NAME = "Theme CSS Builder";
    public static final String[] THEME_PARENTS = { "classic", "_styled", "_unstyled" };

    public static IStatus cssBuild( IProject project ) throws CoreException
    {

        SDK sdk = SDKUtil.getSDK( project );

        if( sdk == null )
        {
            throw new CoreException(
                ThemeCore.createErrorStatus( "No SDK for project configured. Could not build theme." ) );
        }

        ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( project );

        if( liferayRuntime == null )
        {
            throw new CoreException(
                ThemeCore.createErrorStatus( "Could not get portal runtime for project.  Could not build theme." ) );
        }

        Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( project );

        IStatus status = sdk.compileThemePlugin( project, null, appServerProperties );

        if( !status.isOK() )
        {
            throw new CoreException( status );
        }

        IFolder docroot = CoreUtil.getDocroot( project );

        IFile lookAndFeelFile = docroot.getFile( "WEB-INF/" + ILiferayConstants.LIFERAY_LOOK_AND_FEEL_XML_FILE );

        if( !lookAndFeelFile.exists() )
        {
            String id = project.getName().replaceAll( ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX, "" );
            IFile propsFile = docroot.getFile( "WEB-INF/" + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE );
            String name = id;
            if( propsFile.exists() )
            {
                Properties props = new Properties();
                try
                {
                    props.load( propsFile.getContents() );
                    String nameValue = props.getProperty( "name" );
                    if( !CoreUtil.isNullOrEmpty( nameValue ) )
                    {
                        name = nameValue;
                    }
                }
                catch( IOException e )
                {
                    ThemeCore.logError( "Unable to load plugin package properties.", e );
                }
            }

            if( liferayRuntime != null )
            {
                ThemeDescriptorHelper.createDefaultFile(
                    lookAndFeelFile, liferayRuntime.getPortalVersion() + "+", id, name );
            }
        }

        if( docroot != null && docroot.exists() )
        {
            docroot.refreshLocal( IResource.DEPTH_INFINITE, null );
        }

        return status;
    }

    private BuildHelper buildHelper;

    public ThemeCSSBuilder()
    {
        super();

        this.buildHelper = new BuildHelper();
    }

    protected void applyDiffsDeltaToDocroot(
        final IResourceDelta delta, final IFolder docroot, final IProgressMonitor monitor )
    {
        int deltaKind = delta.getKind();

        switch( deltaKind )
        {
            case IResourceDelta.REMOVED_PHANTOM:
                System.out.println();
                break;
        }

        final IPath path = CoreUtil.getResourceLocation( docroot );
        // final IPath restoreLocation = getRestoreLocation(docroot);
        String themeParent = getThemeParent( getProject() );

        IPath themesPath = null;
        try
        {
            ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( getProject() );
            themesPath = liferayRuntime.getPortalDir().append( "html/themes" );
        }
        catch( CoreException e1 )
        {
            e1.printStackTrace();
        }

        final List<IPath> restorePaths = new ArrayList<IPath>();

        for( int i = 0; i < THEME_PARENTS.length; i++ )
        {
            if( THEME_PARENTS[i].equals( themeParent ) )
            {
                restorePaths.add( themesPath.append( THEME_PARENTS[i] ) );
            }
            else
            {
                if( restorePaths.size() > 0 )
                {
                    restorePaths.add( themesPath.append( THEME_PARENTS[i] ) );
                }
            }
        }

        new Job( "publish theme delta" )
        {

            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                buildHelper.publishDelta( delta, path, restorePaths.toArray( new IPath[0] ), monitor );

                try
                {
                    docroot.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                }
                catch( CoreException e )
                {
                    e.printStackTrace();
                }

                return Status.OK_STATUS;
            }
        }.schedule();

    }

    @Override
    protected IProject[] build( int kind, Map args, IProgressMonitor monitor )
    {
        if( kind == IncrementalProjectBuilder.FULL_BUILD )
        {
            fullBuild( args, monitor );
        }
        else
        {
            IResourceDelta delta = getDelta( getProject() );
            if( delta == null )
            {
                fullBuild( args, monitor );
            }
            else
            {
                incrementalBuild( delta, monitor );
            }
        }
        return null;
    }

    protected void fullBuild( Map args, IProgressMonitor monitor )
    {
        try
        {
            if( shouldFullBuild( args ) )
            {
                cssBuild( getProject( args ) );
            }
        }
        catch( Exception e )
        {
            ThemeCore.logError( "Full build failed for Theme CSS Builder", e );
        }
    }

    // private IPath getRestoreLocation( IFolder docroot )
    // {
    // IProject project = docroot.getProject();
    //
    // IPath restoreLocation = ThemeCore.getDefault().getStateLocation().append( project.getName() + "-restore-location"
    // );
    //
    // File restoreDirectory = restoreLocation.toFile();
    //
    // if (!restoreDirectory.exists())
    // {
    // restoreDirectory.mkdirs();
    // }
    //
    // if (restoreDirectory.list().length <= 0)
    // {
    // /* create a temp theme with the same build.xml and invoke theme compile and then extract the contents */
    // SDK sdk = SDKUtil.getSDK( project );
    //
    // String tmpName = String.valueOf(System.currentTimeMillis());
    //
    // IPath tmpTheme = sdk.createNewThemeProject( tmpName, tmpName );
    //
    // File buildFile = CoreUtil.getResourceLocation( project.getFile( "build.xml" ) ).toFile();
    //
    // File newThemeDir = tmpTheme.toFile().listFiles()[0];
    //
    // FileUtil.copyFileToDir( buildFile, newThemeDir);
    //
    // IPath newThemeDestDir = CoreUtil.getResourceLocation( project ).removeLastSegments( 1 ).append(
    // newThemeDir.getName() );
    //
    // try
    // {
    // FileUtils.copyDirectory( newThemeDir, newThemeDestDir.toFile() );
    // SDKHelper helper = new SDKHelper( sdk );
    // IPath newBuildFile = newThemeDestDir.append( "build.xml" );
    // helper.runTarget( newBuildFile, ISDKConstants.TARGET_COMPILE, null );
    //
    // /* copy files from freshly compiled theme to restore-location */
    // File[] restoreFiles = newThemeDestDir.append("docroot").toFile().listFiles();
    //
    // for (File restoreFile : restoreFiles)
    // {
    // if (!"_diffs".equals(restoreFile.getName()))
    // {
    // if (restoreFile.isDirectory())
    // {
    // FileUtils.copyDirectory( restoreFile, newThemeDestDir.toFile() );
    // }
    // else
    // {
    //
    // }
    // }
    // }
    //
    //
    // FileUtil.deleteDir( newThemeDestDir.toFile(), true );
    // }
    // catch( Exception e )
    // {
    // System.out.println(e);
    // }
    // }
    //
    // return restoreLocation;
    // }

    protected IProject getProject( Map args )
    {
        return this.getProject();
    }

    private String getThemeParent( IProject project )
    {
        String retval = null;

        try
        {
            Document buildXmlDoc = FileUtil.readXML( project.getFile( "build.xml" ).getContents(), null, null );

            NodeList properties = buildXmlDoc.getElementsByTagName( "property" );

            for( int i = 0; i < properties.getLength(); i++ )
            {
                final Node item = properties.item( i );
                Node name = item.getAttributes().getNamedItem( "name" );

                if( name != null && "theme.parent".equals( name.getNodeValue() ) )
                {
                    Node value = item.getAttributes().getNamedItem( "value" );

                    retval = value.getNodeValue();
                    break;
                }
            }
        }
        catch( CoreException e )
        {
            e.printStackTrace();
        }

        if( retval == null )
        {
            retval = "_styled";
        }

        return retval;
    }

    protected void incrementalBuild( IResourceDelta delta, final IProgressMonitor monitor )
    {
        int deltaKind = delta.getKind();

        if( deltaKind == IResourceDelta.REMOVED )
        {
            return;
        }

        // final boolean[] buildCSS = new boolean[1];

        try
        {
            delta.accept( new IResourceDeltaVisitor()
            {

                private IFolder docroot = null;

                public boolean visit( IResourceDelta delta )
                {
                    IPath fullResourcePath = delta.getResource().getFullPath();

                    for( String segment : fullResourcePath.segments() )
                    {
                        if( "_diffs".equals( segment ) )
                        {
                            if( docroot == null )
                            {
                                docroot = CoreUtil.getDocroot( getProject() );
                            }

                            IFolder diffs = docroot.getFolder( "_diffs" );

                            if( diffs.exists() && diffs.getFullPath().isPrefixOf( fullResourcePath ) )
                            {
                                applyDiffsDeltaToDocroot( delta, docroot, monitor );

                                return false;
                            }
                        }
                    }

                    return true; // visit children too
                }
            } );
        }
        catch( CoreException e )
        {
            e.printStackTrace();
        }

        // if (buildCSS[0]) {
        // try {
        // cssBuild(getProject());
        // }
        // catch (CoreException e) {
        // ThemeCore.logError("Error in Theme CSS Builder", e);
        // }
        // }
    }

    protected boolean shouldFullBuild( Map args ) throws CoreException
    {
        if( args != null && args.get( "force" ) != null && args.get( "force" ).equals( "true" ) )
        {
            return true;
        }

        // check to see if there is any files in the _diffs folder
        IFolder docroot = CoreUtil.getDocroot( getProject() );

        if( docroot != null )
        {
            IFolder diffs = docroot.getFolder( "_diffs" );

            if( diffs.exists() )
            {
                IResource[] diffMembers = diffs.members();

                if( !CoreUtil.isNullOrEmpty( diffMembers ) )
                {
                    return true;
                }
            }
        }

        return false;
    }
}
