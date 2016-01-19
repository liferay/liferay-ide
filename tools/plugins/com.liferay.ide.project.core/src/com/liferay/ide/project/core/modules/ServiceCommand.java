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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;

import com.liferay.ide.server.util.SocketUtil;

import aQute.remote.api.Agent;
import aQute.remote.api.Event;
import aQute.remote.api.Supervisor;
import aQute.remote.util.AgentSupervisor;

/**
 * @author Lovett Li
 */
public class ServiceCommand
{
    private String _serviceName;

    public ServiceCommand()
    {
    }

    public ServiceCommand( String serviceName )
    {
        _serviceName = serviceName;
    }

    public String[] execute() throws Exception
    {
        ServicesSupervisor supervisor = null;
        String[] result = null;

        try
        {
            supervisor = connectRemote();

            if( _serviceName == null )
            {
                result = getServices( supervisor );
            }
            else
            {
                result = getServiceBundle( _serviceName, supervisor );
            }

            return result;
        }
        finally
        {
            if( supervisor != null )
            {
                supervisor.close();
            }
        }
    }

    private ServicesSupervisor connectRemote() throws Exception
    {
        IStatus status = SocketUtil.canConnect( "localhost", String.valueOf( Agent.DEFAULT_PORT ) );

        if( !( status != null && status.isOK() ) )
        {
            return null;
        }

        ServicesSupervisor supervisor = new ServicesSupervisor();
        supervisor.connect( "localhost", Agent.DEFAULT_PORT );

        if( !supervisor.getAgent().redirect( -1 ) )
        {
            supervisor.close();

            return null;
        }

        return supervisor;
    }

    private String[] getServices( ServicesSupervisor supervisor ) throws Exception
    {
        supervisor.getAgent().stdin( "services" );

        return parseService( supervisor.getOutInfo() );
    }

    private String[] getServiceBundle( String serviceName, ServicesSupervisor supervisor ) throws Exception
    {
        String[] serviceBundleInfo;

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

        return serviceBundleInfo;
    }

    public class ServicesSupervisor extends AgentSupervisor<Supervisor, Agent> implements Supervisor
    {
        private String outinfo;

        @Override
        public boolean stdout( String out ) throws Exception
        {
            if( !"".equals( out ) && out != null )
            {
                out = out.replaceAll( "^>.*$", "" );

                if( !"".equals( out ) && !out.startsWith( "true" ) )
                {
                    outinfo = out;
                }
            }

            return true;
        }

        @Override
        public boolean stderr( String out ) throws Exception
        {
            return true;
        }

        public void connect( String host, int port ) throws Exception
        {
            super.connect( Agent.class, this, host, port );
        }

        @Override
        public void event( Event e ) throws Exception
        {
        }

        public String getOutInfo()
        {
            return outinfo;
        }

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

            if( serviceName.contains( "bundle.id=" ) || serviceName.contains( "service.id=" ) || serviceName.contains( "=" ) )
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

        return newList.toArray( new String[0] );
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
