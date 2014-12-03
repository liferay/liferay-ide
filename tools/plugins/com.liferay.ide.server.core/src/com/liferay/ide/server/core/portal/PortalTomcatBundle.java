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
package com.liferay.ide.server.core.portal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;


/**
 * @author Gregory Amerson
 */
public class PortalTomcatBundle implements PortalBundle
{

    private final IPath autoDeployPath;
    private final IPath liferayHome;
    private final IPath modulesPath;
    private final PortalRuntime runtime;
    private final IPath tomcatPath;

    public PortalTomcatBundle()
    {
        this.autoDeployPath = null;
        this.liferayHome = null;
        this.modulesPath = null;
        this.runtime = null;
        this.tomcatPath = null;
    }

    public PortalTomcatBundle( PortalRuntime portalRuntime )
    {
        if( portalRuntime == null )
        {
            throw new IllegalArgumentException( "portalRuntime cannot be null" );
        }

        this.runtime = portalRuntime;

        // TODO detect tomcat installation
        final IPath location = this.runtime.getRuntime().getLocation();

        this.autoDeployPath = location.append( "deploy" );
        this.liferayHome = location;
        this.modulesPath = location.append( "osgi" );
        this.tomcatPath = location.append( "tomcat-7.0.42" );
    }

    public String[] getRuntimeProgArgs( String launchMode )
    {
        final String[] retval = new String[1];

        if( PortalServer.STOP.equals( launchMode ) )
        {
            retval[0] = "stop";
        }
        else
        {
            retval[0] = "start";
        }

        return retval;
    }

    public String getType()
    {
        return "tomcat";
    }

    public String[] getRuntimeVMArgs()
    {
        final List<String> args = new ArrayList<String>();

        args.add( "-Dcatalina.base=" + this.tomcatPath.toPortableString() );
        args.add( "-Dcatalina.home=" + this.tomcatPath.toPortableString() );
        // TODO use dynamic attach API
        args.add( "-Dcom.sun.management.jmxremote" );
        args.add( "-Dcom.sun.management.jmxremote.authenticate=false" );
        args.add( "-Dcom.sun.management.jmxremote.port=33133" );
        args.add( "-Dcom.sun.management.jmxremote.ssl=false" );
        args.add( "-Dfile.encoding=UTF8" );
        args.add( "-Djava.endorsed.dirs=" + this.tomcatPath.append( "endorsed" ).toPortableString() );
        args.add( "-Djava.io.tmpdir=" + this.tomcatPath.append( "temp" ).toPortableString() );
        args.add( "-Djava.net.preferIPv4Stack=true" );
        args.add( "-Djava.util.logging.config.file=" + this.tomcatPath.append( "conf/logging.properties" ) );
        args.add( "-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager" );
        args.add( "-Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false" );
        args.add( "-Duser.timezone=GMT" );

        return args.toArray( new String[0] );
    }

    public IPath[] getRuntimeClasspath()
    {
        final List<IPath> paths = new ArrayList<IPath>();

        final IPath binPath = this.tomcatPath.append( "bin" );

        if ( binPath.toFile().exists() )
        {
            paths.add( binPath.append("bootstrap.jar") );

            final IPath juli = binPath.append( "tomcat-juli.jar" );

            if( juli.toFile().exists() )
            {
                paths.add( juli );
            }
        }

        return paths.toArray( new IPath[0] );
    }

    public IPath getAutoDeployPath()
    {
        return this.autoDeployPath;
    }

    public IPath getModulesPath()
    {
        return this.modulesPath;
    }

    public IPath getLiferayHome()
    {
        return this.liferayHome;
    }

}
