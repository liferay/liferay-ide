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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Simon Jiang
 */
public class PortalJBossBundle extends AbstractPortalBundle
{
    public PortalJBossBundle( IPath path )
    {
       super(path);
    }

    public PortalJBossBundle( Map<String, String> appServerProperties )
    {
       super(appServerProperties);
    }

    @Override
    public IPath getAppServerDeployDir()
    {
        return this.bundlePath.append( "/standalone/deployments/" );
    }

    @Override
    public IPath getAppServerLibGlobalDir()
    {
        return getAppServerDir().append( "/modules/com/liferay/portal/main" );
    }

    @Override
    protected IPath getAppServerLibDir()
    {
        return getAppServerDir().append( "modules" ); //$NON-NLS-1$
    }

    @Override
    public String getHttpPort()
    {
        String retVal = "8080";

        File standaloneXmlFile = new File( getAppServerDir().toPortableString(), "standalone/configuration/standalone.xml" );

        String portValue = getHttpPortValue( standaloneXmlFile, "socket-binding", "name", "http", "port" );

        if( !CoreUtil.empty( portValue ) )
        {
            if( portValue.lastIndexOf( ":" ) == -1 )
            {
                retVal = portValue;
            }
            else
            {
                retVal = portValue.substring( portValue.lastIndexOf( ":" ) + 1, portValue.length() - 1 );
            }
        }

        return retVal;
    }

    @Override
    public void setHttpPort( String port )
    {
        File standaloneXmlFile = new File( getAppServerDir().toPortableString(), "standalone/configuration/standalone.xml" );

        setHttpPortValue( standaloneXmlFile, "socket-binding", "name", "http", "port", port );
    }

    @Override
    public String getMainClass()
    {
        return "org.jboss.modules.Main";
    }

    @Override
    public IPath getAppServerPortalDir()
    {
        IPath retval = null;

        if( this.bundlePath != null )
        {
            retval = this.bundlePath.append( "/standalone/deployments/ROOT.war" );
        }

        return retval;
    }

    @Override
    public IPath[] getRuntimeClasspath()
    {
        final List<IPath> paths = new ArrayList<IPath>();

        if( this.bundlePath.toFile().exists() )
        {
            paths.add( bundlePath.append( "jboss-modules.jar" ) );
            IPath loggManagerPath = bundlePath.append( "modules/system/layers/base/org/jboss/logmanager/main" );

            if ( loggManagerPath.toFile().exists() )
            {
                File[] libFiles = loggManagerPath.toFile().listFiles( new FileFilter()
                {
                    @Override
                    public boolean accept( File libFile )
                    {
                        if ( libFile.isFile() && libFile.getName().endsWith( ".jar" ))
                        {
                            return true;
                        }
                        return false;
                    }
                });

                if ( libFiles != null )
                {
                    for( File libFile : libFiles )
                    {
                        paths.add( loggManagerPath.append( libFile.getName() ) );
                    }
                }
            }
        }

        return paths.toArray( new IPath[0] );
    }

    @Override
    public String[] getRuntimeStartProgArgs()
    {
        final List<String> args = new ArrayList<String>();

        args.add( "-mp \"" + this.bundlePath.toPortableString() +  "/modules" + "\"");
        args.add( "-jaxpmodule" );
        args.add( "javax.xml.jaxp-provider" );
        args.add( "org.jboss.as.standalone" );
        args.add( "-b" );
        args.add( "localhost" );
        args.add( "--server-config=standalone.xml" );
        args.add( "-Djboss.server.base.dir=" + "\"" + this.bundlePath.toPortableString() + "/standalone/"+ "\"" );

        return args.toArray( new String[0] );

    }

    @Override
    public String[] getRuntimeStopProgArgs()
    {
        final List<String> args = new ArrayList<String>();

        args.add( "-mp \"" + this.bundlePath.toPortableString() +  "/modules" + "\"" );
        args.add( "org.jboss.as.cli" );
        args.add( "--controller=localhost" );
        args.add( "--connect" );
        args.add( "--command=:shutdown" );

        return args.toArray( new String[0] );
    }

    @Override
    public String[] getRuntimeStartVMArgs()
    {
        final List<String> args = new ArrayList<String>();

        args.add( "-Dorg.jboss.resolver.warning=true" );
        args.add( "-Djava.net.preferIPv4Stack=true" );
        args.add( "-Dsun.rmi.dgc.client.gcInterval=3600000" );
        args.add( "-Dsun.rmi.dgc.server.gcInterval=3600000" );
        args.add( "-Djboss.modules.system.pkgs=org.jboss.byteman" );
        args.add( "-Djava.awt.headless=true" );
        args.add( "-Dfile.encoding=UTF8" );
        args.add( "-server" );
        args.add( "-Djava.util.logging.manager=org.jboss.logmanager.LogManager" );
        args.add( "-Xbootclasspath/p:" +  "\""  +  this.bundlePath +  "/modules/org/jboss/logmanager/main/jboss-logmanager-1.2.2.GA.jar"  +  "\"" );
        args.add( "-Xbootclasspath/p:" +  "\""  +  this.bundlePath +  "/modules/org/jboss/logmanager/log4j/main/jboss-logmanager-log4j-1.0.0.GA.jar"  +  "\"" );
        args.add( "-Xbootclasspath/p:" +  "\""  +  this.bundlePath +  "/modules/org/apache/log4j/main/log4j-1.2.16.jar"  +  "\"" );
        args.add( "-Djboss.modules.system.pkgs=org.jboss.logmanager");
        args.add( "-Dorg.jboss.boot.log.file=" +  "\""  + this.bundlePath.append("/standalone/log/boot.log") + "\"" );
        args.add( "-Dlogging.configuration=file:" + "\"" + this.bundlePath + "/standalone/configuration/logging.properties" + "\"" );
        args.add( "-Djboss.home.dir=" + "\"" + this.bundlePath + "\"" );
        args.add( "-Djboss.bind.address.management=localhost" );
        args.add( "-Duser.timezone=GMT" );

        return args.toArray( new String[0] );
    }

    @Override
    public String[] getRuntimeStopVMArgs()
    {
        final List<String> args = new ArrayList<String>();
        args.add( "-Djboss.home.dir=" + "\"" + this.bundlePath + "\"");

        return args.toArray( new String[0] );
    }

    @Override
    public String getType()
    {
        return "jboss";
    }

    @Override
    public String getDisplayName()
    {
        return "JBoss AS";
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

}