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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.AbstractPortalBundle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
public class PortalTomcatBundle extends AbstractPortalBundle
{
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

    @Override
    public String getHttpPort()
    {
        String retVal = "8080";

        File serverXmlFile = new File( getAppServerDir().toPortableString(), "conf/server.xml" );

        String portValue = getHttpPortValue( serverXmlFile, "Connector", "protocol", "HTTP/1.1", "port" );

        if( !CoreUtil.empty( portValue ) )
        {
            return portValue;
        }

        return retVal;
    }

    @Override
    public void setHttpPort( String port )
    {
        setHttpPortValue(
            new File( getAppServerDir().toPortableString(), "conf/server.xml" ), "Connector", "protocol", "HTTP/1.1",
            "port", port );
    }

    @Override
    public String getMainClass()
    {
        return "org.apache.catalina.startup.Bootstrap";
    }

    @Override
    public IPath getAppServerPortalDir()
    {
        IPath retval = null;

        if( this.bundlePath != null )
        {
            retval = this.bundlePath.append( "webapps/ROOT" );
        }

        return retval;
    }

    @Override
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

    @Override
    public String getType()
    {
        return "tomcat";
    }

    @Override
    public String getDisplayName()
    {
        return "Tomcat";
    }

    @Override
    public IPath[] getUserLibs()
    {
        List<IPath> libs = new ArrayList<IPath>();
        try
        {
            List<File>  portallibFiles = FileListing.getFileListing( new File( getAppServerPortalDir().append( "WEB-INF/lib" ).toPortableString() ) );
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

    private void setHttpPortValue(
        File xmlFile, String tagName, String attriName, String attriValue, String targetName, String value )
    {
        DocumentBuilder db = null;

        DocumentBuilderFactory dbf = null;

        try
        {
            dbf = DocumentBuilderFactory.newInstance();

            db = dbf.newDocumentBuilder();

            Document document = db.parse( xmlFile );

            NodeList connectorNodes = document.getElementsByTagName( tagName );

            for( int i = 0; i < connectorNodes.getLength(); i++ )
            {
                Node node = connectorNodes.item( i );

                NamedNodeMap attributes = node.getAttributes();

                Node protocolNode = attributes.getNamedItem( attriName );

                if( protocolNode != null )
                {
                    if( protocolNode.getNodeValue().equals( attriValue ) )
                    {
                        Node portNode = attributes.getNamedItem( targetName );

                        portNode.setNodeValue( value );

                        break;
                    }
                }
            }

            TransformerFactory factory = TransformerFactory.newInstance();

            Transformer transformer = factory.newTransformer();

            DOMSource domSource = new DOMSource( document );

            StreamResult result = new StreamResult( xmlFile );

            transformer.transform( domSource, result );
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( e );
        }
    }

    @Override
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
}