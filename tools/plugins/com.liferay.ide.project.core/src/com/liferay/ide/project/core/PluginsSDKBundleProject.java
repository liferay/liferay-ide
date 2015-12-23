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
package com.liferay.ide.project.core;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.remote.IRemoteServerPublisher;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class PluginsSDKBundleProject extends FlexibleProject implements IWebProject, IBundleProject
{

    private final String[] IGNORE_PATHS = new String[] { "docroot/WEB-INF/classes" };

    private PortalBundle portalBundle;

    public PluginsSDKBundleProject( IProject project, PortalBundle portalBundle )
    {
        super( project );

        this.portalBundle = portalBundle;
    }

    public <T> T adapt( Class<T> adapterType )
    {
        T adapter = super.adapt( adapterType );

        if( adapter != null )
        {
            return adapter;
        }

        if( IProjectBuilder.class.equals( adapterType ) )
        {
            final SDK sdk = getSDK();

            if( sdk != null )
            {
                final IProjectBuilder projectBuilder = new SDKProjectBuilder( getProject(), sdk );

                return adapterType.cast( projectBuilder );
            }
        }
        else if( IRemoteServerPublisher.class.equals( adapterType ) )
        {
            final SDK sdk = getSDK();

            if( sdk != null )
            {
                final IRemoteServerPublisher remotePublisher = new SDKProjectRemoteServerPublisher( getProject(), sdk );

                return adapterType.cast( remotePublisher );
            }
        }
        else if( ILiferayPortal.class.equals( adapterType ) )
        {
            return adapterType.cast( this.portalBundle );
        }

        return null;
    }

    public IPath getLibraryPath( String filename )
    {
        final IPath[] libs = getUserLibs();

        if( ! CoreUtil.isNullOrEmpty( libs ) )
        {
            for( IPath lib : libs )
            {
                if( lib.lastSegment().startsWith( filename ) )
                {
                    return lib;
                }
            }
        }

        return null;
    }

    public String getProperty( final String key, final String defaultValue )
    {
        String retval = defaultValue;

        if( ( "theme.type".equals( key ) || "theme.parent".equals( key ) ) && ProjectUtil.isThemeProject( getProject() ) )
        {
            try
            {
                Document buildXmlDoc = FileUtil.readXML( getProject().getFile( "build.xml" ).getContents(), null, null );

                NodeList properties = buildXmlDoc.getElementsByTagName( "property" ); //$NON-NLS-1$

                for( int i = 0; i < properties.getLength(); i++ )
                {
                    final Node item = properties.item( i );
                    Node name = item.getAttributes().getNamedItem( "name" ); //$NON-NLS-1$

                    if( name != null && key.equals( name.getNodeValue() ) )
                    {
                        Node value = item.getAttributes().getNamedItem( "value" ); //$NON-NLS-1$

                        retval = value.getNodeValue();
                        break;
                    }
                }
            }
            catch( CoreException e )
            {
                ProjectCore.logError( "Unable to get property " + key, e );
            }
        }

        return retval;
    }

    protected SDK getSDK()
    {
        SDK retval = null;

        // try to determine SDK based on project location
        IPath sdkLocation = getProject().getRawLocation().removeLastSegments( 2 );

        retval = SDKManager.getInstance().getSDK( sdkLocation );

        if( retval == null )
        {
            retval = SDKUtil.createSDKFromLocation( sdkLocation );
            SDKManager.getInstance().addSDK( retval );
        }

        return retval;
    }

    public IPath[] getUserLibs()
    {
        return this.portalBundle.getUserLibs();
    }

    public Collection<IFile> getOutputs( boolean build, IProgressMonitor monitor ) throws CoreException
    {
        final Collection<IFile> outputs = new HashSet<IFile>();

        if( build )
        {
            this.getProject().build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );

            final SDK sdk = SDKUtil.getSDK( this.getProject() );

            final IStatus warStatus = sdk.war( this.getProject(), null, true, monitor );

            if( warStatus.isOK() )
            {

            }
        }

        return outputs;
    }

    @Override
    public boolean filterResource( IPath resourcePath )
    {
        if( filterResource( resourcePath, IGNORE_PATHS ) )
        {
            return true;
        }

        return false;
    }

    @Override
    public IPath getOutputBundle( boolean buildIfNeeded, IProgressMonitor monitor ) throws CoreException
    {
        IPath retval = null;

        final SDK sdk = getSDK();
        final IStatus status = sdk.validate();

        if ( !status.isOK() )
        {
            throw new CoreException( status );
        }

        IStatus warStatus = Status.OK_STATUS;

        if( buildIfNeeded )
        {
            warStatus = sdk.war(
                getProject(), null, true, new String[] { "-Duser.timezone=GMT" }, monitor );
        }

        final IPath distPath = sdk.getLocation().append( "dist" );

        // TODO need to find a better way to determine the actual output file.

        final File[] distFiles = distPath.toFile().listFiles( new FilenameFilter()
        {
            @Override
            public boolean accept( File dir, String name )
            {
                return name.contains( getProject().getLocation().lastSegment() );
            }
        });

        if( warStatus.isOK() )
        {
            try
            {
                retval = new Path( distFiles[0].getCanonicalPath() );
            }
            catch( IOException e )
            {
                throw new CoreException( ProjectCore.createErrorStatus( e ) );
            }
        }

        return retval;
    }

    @Override
    public String getSymbolicName() throws CoreException
    {
        return this.getProject().getLocation().lastSegment();
    }

}
