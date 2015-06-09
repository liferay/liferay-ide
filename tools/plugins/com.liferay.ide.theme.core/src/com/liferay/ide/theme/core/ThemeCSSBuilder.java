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

package com.liferay.ide.theme.core;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.theme.core.operation.ThemeDescriptorHelper;
import com.liferay.ide.theme.core.util.BuildHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "rawtypes" )
public class ThemeCSSBuilder extends IncrementalProjectBuilder
{

    public static final String ID = "com.liferay.ide.eclipse.theme.core.cssBuilder"; //$NON-NLS-1$

    public static IStatus compileTheme( IProject project ) throws CoreException
    {
        final SDK sdk = SDKUtil.getSDK( project );

        if( sdk == null )
        {
            throw new CoreException(
                ThemeCore.createErrorStatus( "No SDK for project configured. Could not build theme." ) ); //$NON-NLS-1$
        }

        final IStatus status = sdk.compileThemePlugin( project, null );

        if( !status.isOK() )
        {
            throw new CoreException( status );
        }

        ensureLookAndFeelFileExists( project );

        try
        {
            project.refreshLocal( IResource.DEPTH_INFINITE, null );
        }
        catch( Exception e )
        {
            ThemeCore.logError( e );
        }

        return status;
    }

    public static void ensureLookAndFeelFileExists( IProject project ) throws CoreException
    {
        // IDE-110 IDE-648
        final IWebProject lrProject = LiferayCore.create( IWebProject.class, project );

        if( lrProject == null )
        {
            return;
        }

        IFile lookAndFeelFile = null;

        final IResource res =
            lrProject.findDocrootResource( new Path( "WEB-INF/" +
                ILiferayConstants.LIFERAY_LOOK_AND_FEEL_XML_FILE ) );

        if( res instanceof IFile && res.exists() )
        {
            lookAndFeelFile = (IFile) res;
        }

        if( lookAndFeelFile == null )
        {
            // need to generate a new lnf file in deafult docroot
            String id = project.getName().replaceAll( ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX, StringPool.EMPTY );

            final IResource propertiesFileRes =
                lrProject.findDocrootResource( new Path( "WEB-INF/" +
                    ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE ) );
            String name = id;

            if( propertiesFileRes instanceof IFile && propertiesFileRes.exists() )
            {
                Properties props = new Properties();

                try
                {
                    final IFile propsFile = (IFile) propertiesFileRes;
                    final InputStream contents = propsFile.getContents();

                    props.load( contents );
                    contents.close();

                    final String nameValue = props.getProperty( "name" ); //$NON-NLS-1$

                    if( !CoreUtil.isNullOrEmpty( nameValue ) )
                    {
                        name = nameValue;
                    }

                    final ThemeDescriptorHelper themeDescriptorHelper = new ThemeDescriptorHelper( project );

                    final ILiferayProject lProject = lrProject;
                    final ILiferayPortal portal = lProject.adapt( ILiferayPortal.class );
                    String version = "6.2.0";

                    if( portal != null )
                    {
                        version = portal.getVersion();
                    }

                    final String themeType = lProject.getProperty( "theme.type", "vm" );

                    themeDescriptorHelper.createDefaultFile(
                        lrProject.getDefaultDocrootFolder().getFolder( "WEB-INF" ), version, id, name, themeType );
                }
                catch( IOException e )
                {
                    ThemeCore.logError( "Unable to load plugin package properties.", e ); //$NON-NLS-1$
                }
            }
        }
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
                break;
        }

        final IPath path = CoreUtil.getResourceLocation( docroot );
        // final IPath restoreLocation = getRestoreLocation(docroot);

        final ILiferayProject liferayProject = LiferayCore.create( getProject() );

        final String themeParent = liferayProject.getProperty( "theme.parent", "_styled" );

        final ILiferayPortal portal = liferayProject.adapt( ILiferayPortal.class );

        if( portal != null )
        {
            final IPath themesPath = portal.getAppServerPortalDir().append( "html/themes" );
            final List<IPath> restorePaths = new ArrayList<IPath>();

            for( int i = 0; i < IPluginProjectDataModelProperties.THEME_PARENTS.length; i++ )
            {
                if( IPluginProjectDataModelProperties.THEME_PARENTS[i].equals( themeParent ) )
                {
                    restorePaths.add( themesPath.append( IPluginProjectDataModelProperties.THEME_PARENTS[i] ) );
                }
                else
                {
                    if( restorePaths.size() > 0 )
                    {
                        restorePaths.add( themesPath.append( IPluginProjectDataModelProperties.THEME_PARENTS[i] ) );
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
                compileTheme( getProject( args ) );
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
                public boolean visit( IResourceDelta delta )
                {
                    final IResource resource = delta.getResource();
                    IPath fullResourcePath = resource.getFullPath();

                    for( String segment : fullResourcePath.segments() )
                    {
                        if( "_diffs".equals( segment ) ) //$NON-NLS-1$
                        {
                            // IDE-110 IDE-648
                            final IWebProject webproject = LiferayCore.create( IWebProject.class, getProject() );

                            if( webproject != null && webproject.getDefaultDocrootFolder() != null )
                            {
                                IFolder webappRoot = webproject.getDefaultDocrootFolder();

                                if( webappRoot != null )
                                {
                                    IFolder diffs = webappRoot.getFolder( new Path( "_diffs" ) ); //$NON-NLS-1$

                                    if( diffs != null && diffs.exists() &&
                                        diffs.getFullPath().isPrefixOf( fullResourcePath ) )
                                    {
                                        applyDiffsDeltaToDocroot( delta, diffs.getParent(), monitor );

                                        return false;
                                    }
                                }
                            }
                        }
                        else if( "build.xml".equals( segment ) ) //IDE-828 //$NON-NLS-1$
                        {
                            IPath relPath = resource.getProjectRelativePath();

                            if( relPath != null && relPath.segmentCount() == 1 )
                            {
                                try
                                {
                                    compileTheme( resource.getProject() );
                                }
                                catch( CoreException e )
                                {
                                    ThemeCore.logError( "Error compiling theme.", e ); //$NON-NLS-1$
                                }
                            }
                        }
                    }

                    return true; // visit children too
                }
            } );
        }
        catch( CoreException e )
        {
            ThemeCore.logError( e );
        }
    }

    protected boolean shouldFullBuild( Map args ) throws CoreException
    {
        if( args != null && args.get( "force" ) != null && args.get( "force" ).equals( "true" ) )
        {
            return true;
        }

        // check to see if there is any files in the _diffs folder
        // IDE-110 IDE-648
        final IWebProject lrproject = LiferayCore.create( IWebProject.class, getProject() );

        if( lrproject != null && lrproject.getDefaultDocrootFolder() != null )
        {
            final IFolder webappRoot = lrproject.getDefaultDocrootFolder();

            if( webappRoot != null )
            {
                IFolder diffs = webappRoot.getFolder( new Path( "_diffs" ) );

                if( diffs != null && diffs.exists() )
                {
                    IResource[] diffMembers = diffs.members();

                    if( !CoreUtil.isNullOrEmpty( diffMembers ) )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
