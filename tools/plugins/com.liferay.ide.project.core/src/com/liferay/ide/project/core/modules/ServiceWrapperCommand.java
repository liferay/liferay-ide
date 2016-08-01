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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
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

    public ServiceContainer execute() throws Exception
    {

        if( _server == null )
        {
            return getServiceWrapperFromTargetPlatform();
        }
        else
        {
            Map<String, String[]> dynamicServiceWrappers = getDynamicServiceWrapper();
            ServiceContainer result;

            if( _serviceWrapperName == null )
            {
                result =
                    new ServiceContainer( Arrays.asList( dynamicServiceWrappers.keySet().toArray( new String[0] ) ) );
            }
            else
            {
                String[] wrapperBundle = dynamicServiceWrappers.get( _serviceWrapperName );
                result = new ServiceContainer( wrapperBundle[0], wrapperBundle[1] );
            }

            return result;
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

    private  Map<String,String[]> getDynamicServiceWrapper() throws IOException
    {
        final IPath bundleLibPath =
            ( (PortalRuntime) _server.getRuntime().loadAdapter( PortalRuntime.class, null ) ).getAppServerLibGlobalDir();
        final IPath bundleServerPath =
            ( (PortalRuntime) _server.getRuntime().loadAdapter( PortalRuntime.class, null ) ).getAppServerDir();
        final Map<String, String[]> map = new LinkedHashMap<>();
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

                                            getServiceWrapperList( map, entryName, jarInputStream );
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
                    }
                    else if( lib.getName().endsWith( "api.jar" ) || lib.getName().equals( "portal-kernel.jar" ) )
                    {
                        JarInputStream jarinput = null;

                        try(JarFile jar = new JarFile( lib ))
                        {
                            jarinput = new JarInputStream( new FileInputStream( lib ) );

                            Enumeration<JarEntry> enu = jar.entries();

                            while( enu.hasMoreElements() )
                            {
                                JarEntry entry = enu.nextElement();
                                String name = entry.getName();

                                getServiceWrapperList( map, name, jarinput );
                            }
                        }
                        catch( IOException e )
                        {
                        }
                        finally
                        {

                            if( jarinput != null )
                            {
                                    jarinput.close();
                            }
                        }
                    }
                }
            }
        }
        catch( FileNotFoundException e )
        {
        }

        return map;
    }

    private void getServiceWrapperList( final Map<String,String[]> wrapperMap, String name, JarInputStream jarInputStream  )
    {
        if( name.endsWith( "ServiceWrapper.class" ) && !( name.contains( "$" ) ) )
        {
            name = name.replaceAll( "\\\\", "." ).replaceAll( "/", "." );
            name = name.substring( 0, name.lastIndexOf( "." ) );
            Attributes mainAttributes = jarInputStream.getManifest().getMainAttributes();
            String bundleName = mainAttributes.getValue( "Bundle-SymbolicName" );
            String version = mainAttributes.getValue( "Bundle-Version" );

            wrapperMap.put( name, new String[] { bundleName, version } );
        }
    }

    private ServiceContainer getServiceWrapperFromTargetPlatform() throws Exception
    {
        ServiceContainer result;

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

    private void updateServiceWrapperStaticFile( final Map<String, String[]> wrappers ) throws Exception
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
                    mapper.writeValue( wrappersFile, wrappers );
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
