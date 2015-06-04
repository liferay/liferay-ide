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

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.portal.AbstractPortalBundle;
import com.liferay.ide.server.core.portal.PortalBundle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class PortalTomcatBundle extends AbstractPortalBundle implements PortalBundle
{
    private static final Collection<String> portalDependencyJars = Arrays.asList
    (
        "annotations-api.jar",
        "ecj-4.2.2.jar",
        "el-api.jar",
        "activation.jar",
        "ccpp.jar",
        "hsql.jar",
        "jms.jar",
        "jta.jar",
        "jtds.jar",
        "junit.jar",
        "jutf7.jar",
        "mail.jar",
        "persitenece.jar",
        "portal-service.jar",
        "portlet.jar",
        "postgresql.jar",
        "support-tomcat.jar",
        "jasper-el.jar",
        "jasper.jar",
        "jsp-api.jar",
        "servlet-api.jar",
        "tomcat-api.jar",
        "tomcat-jdbc.jar",
        "tomcat-util.jar"
    );

    public PortalTomcatBundle( IPath path )
    {
       super(path);
    }

    public PortalTomcatBundle( Map<String, String> appServerProperties )
    {
       super(appServerProperties);
    }


    @Override
    protected IPath getAppServerLibDir()
    {
        return getAppServerDir().append( "lib" ); //$NON-NLS-1$
    }

    @Override
    public IPath getAppServerDeployDir()
    {
        return getAppServerDir().append( "webapps" ); //$NON-NLS-1$
    }

    @Override
    public IPath getAppServerLibGlobalDir()
    {
        return getAppServerDir().append( "/lib/ext" );
    }
    
    protected int getDefaultJMXRemotePort()
    {
        int retval = 8099;

        final IPath setenv = this.bundlePath.append( "bin/setenv." + getShellExtension() );
        final String contents = FileUtil.readContents( setenv.toFile() );
        String port = null;

        if( contents != null )
        {
            final Matcher matcher =
                Pattern.compile( ".*-Dcom.sun.management.jmxremote.port(\\s*)=(\\s*)([0-9]+).*" ).matcher(
                    contents );

            if( matcher.matches() )
            {
                port = matcher.group( 3 );
            }
        }

        if( port != null )
        {
            retval = Integer.parseInt( port );
        }

        return retval;
    }

    @Override
    public String getMainClass()
    {
        return "org.apache.catalina.startup.Bootstrap";
    }


    @Override
    protected Collection<String> getPortalDependencyJars()
    {
        return portalDependencyJars;
    }

    @Override
    public IPath getPortalDir()
    {
        IPath retval = null;

        if( this.bundlePath != null )
        {
            retval = this.bundlePath.append( "webapps/ROOT" );
        }

        return retval;
    }

    public IPath[] getRuntimeClasspath()
    {
        final List<IPath> paths = new ArrayList<IPath>();

        final IPath binPath = this.bundlePath.append( "bin" );

        if( binPath.toFile().exists() )
        {
            paths.add( binPath.append( "bootstrap.jar" ) );

            final IPath juli = binPath.append( "tomcat-juli.jar" );

            if( juli.toFile().exists() )
            {
                paths.add( juli );
            }
        }

        return paths.toArray( new IPath[0] );
    }

    @Override
    public String[] getRuntimeStartProgArgs()
    {
        final String[] retval = new String[1];
        retval[0] = "start";
        return retval;
    }

    @Override
    public String[] getRuntimeStopProgArgs()
    {
        final String[] retval = new String[1];
        retval[0] = "stop";
        return retval;
    }

    @Override
    public String[] getRuntimeStartVMArgs()
    {
        return getRuntimeVMArgs();
    }

    @Override
    public String[] getRuntimeStopVMArgs()
    {
        return getRuntimeVMArgs();
    }

    private String[] getRuntimeVMArgs()
    {
        final List<String> args = new ArrayList<String>();

        args.add( "-Dcatalina.base=" + "\"" + this.bundlePath.toPortableString() + "\"" );
        args.add( "-Dcatalina.home=" + "\"" + this.bundlePath.toPortableString() + "\"" );
        // TODO use dynamic attach API
        args.add( "-Dcom.sun.management.jmxremote" );
        args.add( "-Dcom.sun.management.jmxremote.authenticate=false" );
        args.add( "-Dcom.sun.management.jmxremote.port=" + getJmxRemotePort() );
        args.add( "-Dcom.sun.management.jmxremote.ssl=false" );
        args.add( "-Dfile.encoding=UTF8" );
        args.add( "-Djava.endorsed.dirs=" + "\"" + this.bundlePath.append( "endorsed" ).toPortableString() + "\"" );
        args.add( "-Djava.io.tmpdir=" + "\"" + this.bundlePath.append( "temp" ).toPortableString() + "\"" );
        args.add( "-Djava.net.preferIPv4Stack=true" );
        args.add( "-Djava.util.logging.config.file=" + "\"" + this.bundlePath.append( "conf/logging.properties" ) +
            "\"" );
        args.add( "-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager" );
        args.add( "-Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false" );
        args.add( "-Duser.timezone=GMT" );

        return args.toArray( new String[0] );
    }

    private String getShellExtension()
    {
        return Platform.OS_WIN32.equals( Platform.getOS() ) ? "bat" : "sh";
    }

    public String getType()
    {
        return "tomcat";
    }

    @Override
    public IPath[] getUserLibs()
    {
        List<IPath> libs = new ArrayList<IPath>();
        try
        {
            List<File>  portallibFiles = FileListing.getFileListing( new File( getPortalDir().append( "WEB-INF/lib" ).toPortableString() ) );
            for( File lib : portallibFiles )
            {
                if( lib.exists() && lib.getName().endsWith( ".jar" ) ) //$NON-NLS-1$
                {
                    libs.add( new Path( lib.getPath() ) );
                }
            }

            List<File>  libFiles = FileListing.getFileListing( new File( getAppServerLibDir().toPortableString() ) );
            for( File lib : libFiles )
            {
                if( lib.exists() && lib.getName().endsWith( ".jar" ))
                {
                    libs.add( new Path( lib.getPath() ) );
                }
            }

            List<File>  extlibFiles = FileListing.getFileListing( new File( getAppServerLibGlobalDir().toPortableString() ) );
            for( File lib : extlibFiles )
            {
                if( lib.exists() && lib.getName().endsWith( ".jar" ) )
                {
                    libs.add( new Path( lib.getPath() ) );
                }
            }            
        }
        catch( FileNotFoundException e )
        {
        }

        return libs.toArray( new IPath[libs.size()] );
    }
}
