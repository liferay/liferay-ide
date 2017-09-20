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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.XMLUtil;

/**
 * @author Simon Jiang
 */
public class JBossBundleConfiguration extends PortalBundleConfiguration
{

    public JBossBundleConfiguration( PortalBundle bundle )
    {
        super( bundle );
    }

    @Override
    public List<LiferayServerPort> getConfiguredServerPorts()
    {
        List<LiferayServerPort> ports = new ArrayList<LiferayServerPort>();

        if( isServerDirty == true )
        {
            IPath file =
                bundle.getAppServerDir().append( "standalone" ).append( "configuration" ).append( "standalone.xml" );

            if( file.toFile().exists() )
            {
                try
                {
                    Document refreshDocument =
                        XMLUtil.getDocumentBuilder().parse( new InputSource( new FileInputStream( file.toFile() ) ) );

                    ports = getPortsValue( refreshDocument, "socket-binding" );
                }
                catch( SAXException | IOException e )
                {
                    LiferayServerCore.logError( e );
                }
            }
        }
        else
        {
            ports = getPortsValue( configurationDocument, "socket-binding" );
        }

        return ports;
    }

    @Override
    public void applyBundleChange( LiferayServerPort port )
    {
        if( port.getStoreLocation().equals( LiferayServerPort.defaultStoreInXML ) )
        {
            Node attributeNode = getAttributeNode( "socket-binding", port.getId() );

            if( attributeNode != null )
            {
                XMLUtil.setNodeValue( attributeNode, port.getId(), String.valueOf( port.getPort() ) );
            }
        }
    }

    @Override
    public void saveBundleConfiguration( IProgressMonitor monitor ) throws CoreException
    {
        try
        {
            IPath filePath = bundle.getAppServerDir().append( "/standalone/configuration/" ).append( "standalone.xml" );

            if( filePath.toFile().exists() )
            {
                if( isServerDirty )
                {
                    XMLUtil.save( filePath.toOSString(), configurationDocument );
                }
            }
        }
        catch( Exception e )
        {
            throw new CoreException( LiferayServerCore.createErrorStatus( e ) );
        }
    }

    @Override
    public void loadBundleConfiguration( IProgressMonitor monitor ) throws CoreException
    {
        try
        {
            IPath configurationFile =
                bundle.getAppServerDir().append( "/standalone/configuration/" ).append( "standalone.xml" );

            if( configurationFile.toFile().exists() )
            {
                configurationDocument = XMLUtil.getDocumentBuilder().parse(
                    new InputSource( new FileInputStream( configurationFile.toFile() ) ) );
            }

            if( !configurationFile.toFile().exists() )
            {
                throw new CoreException(
                    new Status(
                        IStatus.WARNING, LiferayServerCore.PLUGIN_ID, 0,
                        "Could not load the JBoss server configuration", null ) );
            }
        }
        catch( Exception e )
        {
            throw new CoreException( LiferayServerCore.createErrorStatus( e ) );
        }
    }

    private Node getAttributeNode( String tagName, String attrName )
    {
        try
        {
            Document document = configurationDocument;

            NodeList connectorNodes = document.getElementsByTagName( tagName );

            for( int i = 0; i < connectorNodes.getLength(); i++ )
            {
                Node node = connectorNodes.item( i );

                NamedNodeMap attributes = node.getAttributes();

                for( int j = 0; j < attributes.getLength(); j++ )
                {
                    Node itemNode = attributes.item( j );

                    if( itemNode != null )
                    {
                        if( itemNode.getNodeName().equals( "name" ) )
                        {
                            if( itemNode.getNodeValue().equals( attrName.toLowerCase() ) )
                            {
                                return attributes.getNamedItem( "port" );
                            }
                        }
                    }
                }
            }
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( e );
        }

        return null;
    }

    private List<LiferayServerPort> getPortsValue( Document document, String tagName )
    {
        List<LiferayServerPort> retPorts = new ArrayList<LiferayServerPort>();
        try
        {
            NodeList connectorNodes = document.getElementsByTagName( tagName );

            for( int i = 0; i < connectorNodes.getLength(); i++ )
            {
                Node node = connectorNodes.item( i );

                NamedNodeMap attributes = node.getAttributes();

                for( int j = 0; j < attributes.getLength(); j++ )
                {
                    Node itemNode = attributes.item( j );

                    if( itemNode != null )
                    {
                        if( itemNode.getNodeName().equals( "name" ) )
                        {
                            boolean existed = false;

                            for( LiferayServerPort port : retPorts )
                            {
                                if( port.getId().equals( itemNode.getNodeValue() ) )
                                {
                                    existed = true;
                                    break;
                                }
                            }

                            if( !existed )
                            {
                                Node portNode = attributes.getNamedItem( "port" );

                                LiferayServerPort createServerPort = createLiferayServerPort(
                                    itemNode.getNodeValue(), itemNode.getNodeValue(), portNode.getNodeValue() );

                                retPorts.add( createServerPort );
                            }
                        }
                    }
                }
            }
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( e );
        }
        return retPorts;
    }

    @Override
    public int getHttpPort()
    {
        List<LiferayServerPort> ports = new ArrayList<LiferayServerPort>();

        if( isServerDirty == true )
        {
            IPath file = bundle.getAppServerDir().append( "/standalone/configuration/" ).append( "standalone.xml" );

            if( file.toFile().exists() )
            {
                try
                {
                    Document refreshDocument =
                        XMLUtil.getDocumentBuilder().parse( new InputSource( new FileInputStream( file.toFile() ) ) );

                    ports = getPortsValue( refreshDocument, "socket-binding" );
                }
                catch( SAXException | IOException e )
                {
                    LiferayServerCore.logError( e );
                }
            }
        }
        else
        {
            ports = getConfiguredServerPorts();
        }

        for( LiferayServerPort port : ports )
        {
            if( port.getProtocol().toLowerCase().equals( "http" ) )
            {
                return port.getPort();
            }
        }

        return -1;
    }
}
