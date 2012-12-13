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
import com.liferay.ide.core.util.StringUtil;
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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings( "rawtypes" )
public class ThemeCSSBuilder extends IncrementalProjectBuilder
{

    public static final String ID = "com.liferay.ide.eclipse.theme.core.cssBuilder"; //$NON-NLS-1$
    public static final String[] THEME_PARENTS = { "classic", "_styled", "_unstyled" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    public static IStatus cssBuild( IProject project ) throws CoreException
    {
        final SDK sdk = SDKUtil.getSDK( project );

        if( sdk == null )
        {
            throw new CoreException(
                ThemeCore.createErrorStatus( "No SDK for project configured. Could not build theme." ) ); //$NON-NLS-1$
        }

        final ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( project );

        if( liferayRuntime == null )
        {
            throw new CoreException(
                ThemeCore.createErrorStatus( "Could not get portal runtime for project.  Could not build theme." ) ); //$NON-NLS-1$
        }

        final Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( project );

        final IStatus status = sdk.compileThemePlugin( project, null, appServerProperties );

        if( !status.isOK() )
        {
            throw new CoreException( status );
        }

        // IDE-110 IDE-648
        final IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

        IVirtualFile lookAndFeelFile = null;

        if( webappRoot != null )
        {
            final IVirtualFile file =
                webappRoot.getFile( new Path( "WEB-INF/" + ILiferayConstants.LIFERAY_LOOK_AND_FEEL_XML_FILE ) ); //$NON-NLS-1$

            if( file != null && file.exists() )
            {
                lookAndFeelFile = file;
            }
        }

        if( lookAndFeelFile == null )
        {
            String id = project.getName().replaceAll( ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX, StringUtil.EMPTY );

            IVirtualFile propertiesFile = webappRoot.getFile( new Path( "WEB-INF/" + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE ) ); //$NON-NLS-1$
            String name = id;

            if( propertiesFile != null && propertiesFile.exists() )
            {
                Properties props = new Properties();

                try
                {
                    final IFile underlyingFile = propertiesFile.getUnderlyingFile();
                    props.load( underlyingFile.getContents() );
                    String nameValue = props.getProperty( "name" ); //$NON-NLS-1$

                    if( !CoreUtil.isNullOrEmpty( nameValue ) )
                    {
                        name = nameValue;
                    }

                    if( liferayRuntime != null )
                    {
                        final ThemeDescriptorHelper themeDescriptorHelper = new ThemeDescriptorHelper( project );

                        themeDescriptorHelper.createDefaultFile( underlyingFile.getParent(), liferayRuntime.getPortalVersion() , id, name );
                    }

                    try
                    {
                        underlyingFile.getParent().refreshLocal( IResource.DEPTH_INFINITE, null );
                    }
                    catch( Exception e )
                    {
                        ThemeCore.logError( e );
                    }
                }
                catch( IOException e )
                {
                    ThemeCore.logError( "Unable to load plugin package properties.", e ); //$NON-NLS-1$
                }
            }
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
        final IResourceDelta delta, final IContainer docroot, final IProgressMonitor monitor )
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
            themesPath = liferayRuntime.getPortalDir().append( "html/themes" ); //$NON-NLS-1$
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

        new Job( "publish theme delta" ) //$NON-NLS-1$
        {
            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                buildHelper.publishDelta( delta, path, restorePaths.toArray( new IPath[0] ), monitor );

                try
                {
                    docroot.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                }
                catch( Exception e )
                {
                    ThemeCore.logError( e );
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
            ThemeCore.logError( "Full build failed for Theme CSS Builder", e ); //$NON-NLS-1$
        }
    }

    protected IProject getProject( Map args )
    {
        return this.getProject();
    }

    private String getThemeParent( IProject project )
    {
        String retval = null;

        try
        {
            Document buildXmlDoc = FileUtil.readXML( project.getFile( "build.xml" ).getContents(), null, null ); //$NON-NLS-1$

            NodeList properties = buildXmlDoc.getElementsByTagName( "property" ); //$NON-NLS-1$

            for( int i = 0; i < properties.getLength(); i++ )
            {
                final Node item = properties.item( i );
                Node name = item.getAttributes().getNamedItem( "name" ); //$NON-NLS-1$

                if( name != null && "theme.parent".equals( name.getNodeValue() ) ) //$NON-NLS-1$
                {
                    Node value = item.getAttributes().getNamedItem( "value" ); //$NON-NLS-1$

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
            retval = "_styled"; //$NON-NLS-1$
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
            delta.accept
            (
                new IResourceDeltaVisitor()
                {
                    public boolean visit( IResourceDelta delta )
                    {
                        IPath fullResourcePath = delta.getResource().getFullPath();

                        for( String segment : fullResourcePath.segments() )
                        {
                            if( "_diffs".equals( segment ) ) //$NON-NLS-1$
                            {
                                // IDE-110 IDE-648
                                IVirtualFolder webappRoot = CoreUtil.getDocroot( getProject() );

                                if( webappRoot != null )
                                {
                                    IVirtualFolder diffs = webappRoot.getFolder( new Path("_diffs") ); //$NON-NLS-1$

                                    if( diffs != null && diffs.exists() &&
                                        diffs.getUnderlyingFolder().getFullPath().isPrefixOf( fullResourcePath ) )
                                    {
                                        applyDiffsDeltaToDocroot( delta, diffs.getUnderlyingFolder().getParent(), monitor );

                                        return false;
                                    }
                                }
                            }
                        }

                        return true; // visit children too
                    }
                }
            );
        }
        catch( CoreException e )
        {
            e.printStackTrace();
        }
    }

    protected boolean shouldFullBuild( Map args ) throws CoreException
    {
        if( args != null && args.get( "force" ) != null && args.get( "force" ).equals( "true" ) )   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        {
            return true;
        }

        // check to see if there is any files in the _diffs folder
        // IDE-110 IDE-648
        IVirtualFolder webappRoot = CoreUtil.getDocroot( getProject() );

        if( webappRoot != null )
        {
            IVirtualFolder diffs = webappRoot.getFolder( new Path( "_diffs" ) ); //$NON-NLS-1$

            if( diffs != null && diffs.exists() )
            {
                IResource[] diffMembers = diffs.getUnderlyingResources();

                if( !CoreUtil.isNullOrEmpty( diffMembers ) )
                {
                    return true;
                }
            }
        }

        return false;
    }
}
