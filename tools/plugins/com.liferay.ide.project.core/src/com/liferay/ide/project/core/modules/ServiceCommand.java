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

import aQute.remote.api.Agent;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.TargetPlatformUtil;
import com.liferay.ide.server.core.portal.BundleSupervisor;
import com.liferay.ide.server.core.portal.PortalServerBehavior;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Lovett Li
 * @author Simon Jiang
 */
public class ServiceCommand
{

    private final IServer _server;
    private String _serviceName;

    private static final String[] _portalImplExpPackage = new String[] {
        "com.liferay.portal.bean",
        "com.liferay.portal.cache.thread.local",
        "com.liferay.portal.cluster",
        "com.liferay.portal.convert.database",
        "com.liferay.portal.convert.util",
        "com.liferay.portal.dao.jdbc.aop",
        "com.liferay.portal.dao.orm.hibernate",
        "com.liferay.portal.deploy.hot",
        "com.liferay.portal.events",
        "com.liferay.portal.freemarker",
        "com.liferay.portal.increment",
        "com.liferay.portal.messaging.async",
        "com.liferay.portal.monitoring.statistics.service",
        "com.liferay.portal.plugin",
        "com.liferay.portal.resiliency.service",
        "com.liferay.portal.search",
        "com.liferay.portal.security.access.control",
        "com.liferay.portal.security.auth",
        "com.liferay.portal.security.lang",
        "com.liferay.portal.service.http",
        "com.liferay.portal.service.permission",
        "com.liferay.portal.servlet",
        "com.liferay.portal.servlet.filters.authverifier",
        "com.liferay.portal.spring.aop",
        "com.liferay.portal.spring.bean",
        "com.liferay.portal.spring.context",
        "com.liferay.portal.spring.hibernate",
        "com.liferay.portal.spring.transaction",
        "com.liferay.portal.systemevent",
        "com.liferay.portal.template",
        "com.liferay.portal.tools",
        "com.liferay.portal.upgrade.util",
        "com.liferay.portal.upgrade.util.classname",
        "com.liferay.portal.upgrade.util.classname.dependency",
        "com.liferay.portal.upgrade.v7_0_0",
        "com.liferay.portal.upload",
        "com.liferay.portal.util",
        "com.liferay.portal.xml",
        "com.liferay.portlet.asset",
        "com.liferay.portlet.asset.model",
        "com.liferay.portlet.asset.model.impl",
        "com.liferay.portlet.asset.service",
        "com.liferay.portlet.asset.service.permission",
        "com.liferay.portlet.asset.service.persistence",
        "com.liferay.portlet.asset.util",
        "com.liferay.portlet.documentlibrary",
        "com.liferay.portlet.documentlibrary.action",
        "com.liferay.portlet.documentlibrary.antivirus",
        "com.liferay.portlet.documentlibrary.convert",
        "com.liferay.portlet.documentlibrary.lar",
        "com.liferay.portlet.documentlibrary.model",
        "com.liferay.portlet.documentlibrary.service",
        "com.liferay.portlet.documentlibrary.service.permission",
        "com.liferay.portlet.documentlibrary.store",
        "com.liferay.portlet.documentlibrary.util",
        "com.liferay.portlet.expando.model",
        "com.liferay.portlet.expando.service",
        "com.liferay.portlet.expando.service.persistence",
        "com.liferay.portlet.expando.util",
        "com.liferay.portlet.layoutsadmin.display.context",
        "com.liferay.portlet.messageboards.model",
        "com.liferay.portlet.messageboards.service",
        "com.liferay.portlet.messageboards.service.persistence",
        "com.liferay.portlet.social.model",
        "com.liferay.portlet.social.service",
        "com.liferay.portlet.social.service.persistence",
        "com.liferay.portlet.trash",
        "com.liferay.portlet.trash.model",
        "com.liferay.portlet.trash.service",
        "com.liferay.portlet.trash.service.persistence",
        "com.liferay.portlet.trash.util",
        "com.liferay.portlet.usersadmin.search"
    };

    public ServiceCommand( IServer server )
    {
        _server = server;
    }

    public ServiceCommand( IServer server, String serviceName )
    {
        _serviceName = serviceName;
        _server = server;
    }

    private File checkStaticServicesFile() throws IOException
    {
        final URL url =
            FileLocator.toFileURL( ProjectCore.getDefault().getBundle().getEntry( "OSGI-INF/services-static.json" ) );
        final File servicesFile = new File( url.getFile() );

        if( servicesFile.exists() )
        {
            return servicesFile;
        }

        throw new FileNotFoundException( "can't find static services file services-static.json" );
    }

    public ServiceContainer execute() throws Exception
    {
        BundleSupervisor supervisor = null;
        ServiceContainer result;

        if( _server == null )
        {
            return getServiceFromTargetPlatform();
        }
        try
        {
            PortalServerBehavior serverBehavior =
                (PortalServerBehavior) _server.loadAdapter( PortalServerBehavior.class, null );
            supervisor = serverBehavior.getBundleSupervisor();

            if( supervisor == null )
            {
                return getServiceFromTargetPlatform();
            }

            if( !supervisor.getAgent().redirect( Agent.COMMAND_SESSION ) )
            {
                return getServiceFromTargetPlatform();
            }

            if( _serviceName == null )
            {
                String[] services = getServices( supervisor );
                result = new ServiceContainer( Arrays.asList( services ) );
            }
            else
            {
                String[] serviceBundle = getServiceBundle( _serviceName, supervisor );
                result = new ServiceContainer( serviceBundle[0], serviceBundle[1], serviceBundle[2] );
            }
        }
        finally
        {
            supervisor.getAgent().redirect( Agent.NONE );
        }

        return result;
    }

    private ServiceContainer getServiceFromTargetPlatform() throws Exception
    {
        ServiceContainer result;

        if( _serviceName == null )
        {
            result = TargetPlatformUtil.getServicesList();
        }
        else
        {
            result = TargetPlatformUtil.getServiceBundle( _serviceName );
        }

        return result;
    }

    private String[] getServiceBundle( String serviceName, BundleSupervisor supervisor ) throws Exception
    {
        String[] serviceBundleInfo;
        String bundleGroup = "";
        String bundleName;
        String bundleVersion;

        supervisor.getAgent().stdin( "packages " + serviceName.substring( 0, serviceName.lastIndexOf( "." ) ) );

        if( supervisor.getOutInfo().startsWith( "No exported packages" ) )
        {
            supervisor.getAgent().stdin(
                "services " + "(objectClass=" + serviceName + ")" + " | grep \"Registered by bundle:\" " );
            serviceBundleInfo = parseRegisteredBundle( supervisor.getOutInfo() );
        }
        else
        {
            serviceBundleInfo = parseSymbolicName( supervisor.getOutInfo() );
        }

        bundleName = serviceBundleInfo[0];
        bundleVersion = serviceBundleInfo[1];

        if( bundleName.equals( "org.eclipse.osgi,system.bundle" ) )
        {
            bundleGroup = "com.liferay.portal";
        }
        else if( bundleName.startsWith( "com.liferay" ) )
        {
            bundleGroup = "com.liferay";
        }
        else
        {
            int ordinalIndexOf = StringUtils.ordinalIndexOf( bundleName, ".", 3 );

            if(ordinalIndexOf != -1)
            {
                bundleGroup = bundleName.substring( 0, ordinalIndexOf );
            }else{
                ordinalIndexOf = StringUtils.ordinalIndexOf( bundleName, ".", 2 );

                if(ordinalIndexOf != -1){
                    bundleGroup = bundleName.substring( 0, ordinalIndexOf );
                }
            }
        }

        return new String[] { bundleGroup, bundleName, bundleVersion };
    }

    private String[] getServices( BundleSupervisor supervisor ) throws Exception
    {
        supervisor.getAgent().stdin( "services" );

        return parseService( supervisor.getOutInfo() );
    }

    private void updateServicesStaticFile( final String[] servicesList, final BundleSupervisor supervisor ) throws Exception
    {
        final File servicesFile = checkStaticServicesFile();
        final ObjectMapper mapper = new ObjectMapper();
        final Map<String, String[]> map = new LinkedHashMap<>();

        final Job job = new WorkspaceJob( "Update services static file...")
        {

            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor )
            {
                try
                {
                    for( String serviceName : servicesList )
                    {
                        if( monitor.isCanceled() )
                        {
                            return Status.CANCEL_STATUS;
                        }
                        String[] serviceBundle = getServiceBundle( serviceName, supervisor );

                        if( serviceBundle != null )
                        {
                            map.put( serviceName, serviceBundle );
                        }
                    }

                    mapper.writeValue( servicesFile, map );
                }
                catch( Exception e )
                {
                    return Status.CANCEL_STATUS;
                }
                finally
                {
                    if( supervisor != null )
                    {
                        try
                        {
                            supervisor.getAgent().redirect( Agent.NONE );
                        }
                        catch( Exception e )
                        {
                            // ignore error
                        }
                    }
                }

                return Status.OK_STATUS;
            }
        };

        job.schedule();
    }

    private String[] parseRegisteredBundle( String serviceName )
    {
        if( serviceName.startsWith( "false" ) )
        {
            return null;
        }

        String str = serviceName.substring( 0, serviceName.indexOf( "[" ) );
        str = str.replaceAll( "\"Registered by bundle:\"", "" ).trim();
        String[] result = str.split( "_" );

        if( result.length == 2 )
        {
            return result;
        }

        return null;
    }

    private String[] parseService( String outinfo )
    {
        final Pattern pattern = Pattern.compile( "(?<=\\{)(.+?)(?=\\})" );
        final Matcher matcher = pattern.matcher( outinfo );
        final List<String> ls = new ArrayList<>();

        while( matcher.find() )
        {
            ls.add( matcher.group() );
        }

        Iterator<String> iterator = ls.iterator();

        while( iterator.hasNext() )
        {
            String serviceName = iterator.next();

            if( serviceName.contains( "bundle.id=" ) || serviceName.contains( "service.id=" ) ||
                serviceName.contains( "=" ) )
            {
                iterator.remove();
            }
        }

        final List<String> listservice = new ArrayList<>();

        for( String bs : ls )
        {
            if( bs.split( "," ).length > 1 )
            {
                for( String bbs : bs.split( "," ) )
                {
                    listservice.add( bbs.trim() );
                }
            }
            else
            {
                listservice.add( bs );
            }
        }

        final Set<String> set = new HashSet<String>();
        final List<String> newList = new ArrayList<>();

        for( Iterator<String> iter = listservice.iterator(); iter.hasNext(); )
        {
            String element = iter.next();

            if( set.add( element ) )
            {
                newList.add( element );
            }
        }

        Collections.sort( newList );

        Iterator<String> newListIterator = newList.iterator();

        while( newListIterator.hasNext() )
        {
            String serviceName = newListIterator.next();

            for( String packageName : _portalImplExpPackage )
            {
                if( serviceName.startsWith( packageName ) )
                {
                    newListIterator.remove();
                    break;
                }
            }
        }

        return newList.toArray( new String[0] );
    }

    private String[] parseSymbolicName( String info )
    {
        final int symbolicIndex = info.indexOf( "bundle-symbolic-name" );
        final int versionIndex = info.indexOf( "version:Version" );
        String symbolicName;
        String version;

        if( symbolicIndex != -1 && versionIndex != -1 )
        {
            symbolicName = info.substring( symbolicIndex, info.indexOf( ";", symbolicIndex ) );
            version = info.substring( versionIndex, info.indexOf( ";", versionIndex ) );

            final Pattern p = Pattern.compile( "\"([^\"]*)\"" );
            Matcher m = p.matcher( symbolicName );

            while( m.find() )
            {
                symbolicName = m.group( 1 );
            }

            m = p.matcher( version );

            while( m.find() )
            {
                version = m.group( 1 );
            }

            return new String[] { symbolicName, version };
        }

        return null;
    }
}
