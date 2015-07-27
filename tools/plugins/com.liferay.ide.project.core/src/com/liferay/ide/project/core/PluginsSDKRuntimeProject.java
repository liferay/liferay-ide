/*******************************************************************************
 *
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.remote.IRemoteServerPublisher;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class PluginsSDKRuntimeProject extends FlexibleProject implements IWebProject
{

    private ILiferayRuntime liferayRuntime;

    public PluginsSDKRuntimeProject( IProject project, ILiferayRuntime liferayRuntime )
    {
        super( project );

        this.liferayRuntime = liferayRuntime;
    }

    @Override
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
            final ILiferayPortal portal = new PluginsSDKPortal( this.liferayRuntime );

            return adapterType.cast( portal );
        }

        return null;
    }

    @Override
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

    @Override
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
        return this.liferayRuntime.getUserLibs();
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

}
