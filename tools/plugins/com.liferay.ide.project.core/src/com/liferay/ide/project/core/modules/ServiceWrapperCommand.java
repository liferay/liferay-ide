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
package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.TargetPlatformUtil;
import com.liferay.ide.server.core.portal.PortalRuntime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Lovett Li
 */
public class ServiceWrapperCommand
{

    private final IServer _server;
    private String _serviceWrapperName;

    public ServiceWrapperCommand( IServer server )
    {
        _server = server;
    }

    public ServiceWrapperCommand( IServer _server, String _serviceWrapperName )
    {
        this._server = _server;
        this._serviceWrapperName = _serviceWrapperName;
    }

    public String[] getServiceWrapper() throws Exception
    {

        if( _server == null )
        {
            return getServiceWrapperFromTargetPlatform();
        }
        else
        {
            String[] wrappers = getDynamicServiceWrapper();

            return wrappers;
        }
    }

    private File checkStaticWrapperFile() throws IOException
    {
        final URL url =
            FileLocator.toFileURL( ProjectCore.getDefault().getBundle().getEntry( "OSGI-INF/wrappers-static.json" ) );
        final File servicesFile = new File( url.getFile() );

        if( servicesFile.exists() )
        {
            return servicesFile;
        }

        throw new FileNotFoundException( "can't find static services file wrappers-static.json" );
    }

    private String[] getDynamicServiceWrapper()
    {
        final IPath bundleLibPath =
            ( (PortalRuntime) _server.getRuntime().loadAdapter( PortalRuntime.class, null ) ).getAppServerLibGlobalDir();
        final IPath bundleServerPath =
            ( (PortalRuntime) _server.getRuntime().loadAdapter( PortalRuntime.class, null ) ).getAppServerDir();
        final List<String> wrapperList = new ArrayList<>();
        List<File> libFiles;
        File portalkernelJar = null;

        try
        {
            libFiles = FileListing.getFileListing( new File( bundleLibPath.toOSString() ) );

            for( File lib : libFiles )
            {
                if( lib.exists() && lib.getName().endsWith( "portal-kernel.jar" ) )
                {
                    portalkernelJar = lib;
                    break;
                }
            }

            libFiles = FileListing.getFileListing( new File( bundleServerPath.append( "../osgi" ).toOSString() ) );
            libFiles.add( portalkernelJar );

            if( !libFiles.isEmpty() )
            {
                for( File lib : libFiles )
                {
                    if( lib.getName().endsWith( ".lpkg" ) )
                    {
                        try(JarFile jar = new JarFile( lib ))
                        {
                            Enumeration<JarEntry> enu = jar.entries();

                            while( enu.hasMoreElements() )
                            {
                                JarInputStream jarInputStream = null;

                                try
                                {
                                    JarEntry entry = enu.nextElement();

                                    String name = entry.getName();

                                    if( name.contains( ".api-" ) )
                                    {
                                        JarEntry jarentry = jar.getJarEntry( name );
                                        InputStream inputStream = jar.getInputStream( jarentry );

                                        jarInputStream = new JarInputStream( inputStream );
                                        JarEntry nextJarEntry;

                                        while( ( nextJarEntry = jarInputStream.getNextJarEntry() ) != null )
                                        {
                                            String entryName = nextJarEntry.getName();

                                            getServiceWrapperList( wrapperList, entryName );
                                        }

                                    }
                                }
                                catch( Exception e )
                                {
                                }
                                finally
                                {
                                    if( jarInputStream != null )
                                    {
                                        jarInputStream.close();
                                    }
                                }
                            }
                        }
                        catch( IOException e )
                        {
                        }
                    }
                    else if( lib.getName().endsWith( "api.jar" ) || lib.getName().equals( "portal-kernel.jar" ) )
                    {
                        try(JarFile jar = new JarFile( lib ))
                        {
                            Enumeration<JarEntry> enu = jar.entries();

                            while( enu.hasMoreElements() )
                            {
                                JarEntry entry = enu.nextElement();
                                String name = entry.getName();

                                getServiceWrapperList( wrapperList, name );
                            }
                        }
                        catch( IOException e )
                        {
                        }
                    }
                }
            }
        }
        catch( FileNotFoundException e )
        {
        }

        return wrapperList.toArray( new String[0] );
    }

    private void getServiceWrapperList( final List<String> wrapperList, String name )
    {
        if( name.endsWith( "ServiceWrapper.class" ) && !( name.contains( "$" ) ) )
        {
            name = name.replaceAll( "\\\\", "." ).replaceAll( "/", "." );
            name = name.substring( 0, name.lastIndexOf( "." ) );
            wrapperList.add( name );
        }
    }

    private String[] getServiceWrapperFromTargetPlatform() throws Exception
    {
        String[] result;

        if( _serviceWrapperName == null )
        {
            result = TargetPlatformUtil.getServiceWrapperList();
        }
        else
        {
            result = TargetPlatformUtil.getServiceWrapperBundle( _serviceWrapperName );
        }

        return result;
    }

    private void updateServiceWrapperStaticFile( final String[] wrapperList ) throws Exception
    {
        final File wrappersFile = checkStaticWrapperFile();
        final ObjectMapper mapper = new ObjectMapper();

        final Job job = new WorkspaceJob( "Update ServiceWrapper static file...")
        {

            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor )
            {
                try
                {
                    mapper.writeValue( wrappersFile, wrapperList );
                }
                catch( IOException e )
                {
                    return Status.CANCEL_STATUS;
                }

                return Status.OK_STATUS;
            }
        };

        job.schedule();

    }
}
