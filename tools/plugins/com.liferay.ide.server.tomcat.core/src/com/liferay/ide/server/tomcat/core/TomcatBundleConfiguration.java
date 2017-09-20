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
import com.liferay.ide.server.core.portal.LiferayServerPort;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.core.portal.PortalBundleConfiguration;
import com.liferay.ide.server.util.XMLUtil;

/**
 * @author Simon Jiang
 */
public class TomcatBundleConfiguration extends PortalBundleConfiguration
{

    public TomcatBundleConfiguration( PortalBundle bundle )
    {
        super( bundle );
    }

    @Override
    public List<LiferayServerPort> getConfiguredServerPorts()
    {
        final List<LiferayServerPort> ports = new ArrayList<LiferayServerPort>();

        ports.addAll( getPortsValue( configurationDocument, "Connector", "port", "protocol" ) );
        ports.addAll( getPortsValue( configurationDocument, "Server", "port", "shutdown" ) );

        return ports;
    }

    @Override
    public void loadBundleConfiguration( IProgressMonitor monitor ) throws CoreException
    {
        try
        {
            final IPath file = bundle.getAppServerDir().append( "conf" ).append( "server.xml" );

            if( file.toFile().exists() )
            {
                configurationDocument =
                    XMLUtil.getDocumentBuilder().parse( new InputSource( new FileInputStream( file.toFile() ) ) );
            }
            else
            {
                throw new CoreException(
                    new Status(
                        IStatus.WARNING, LiferayServerCore.PLUGIN_ID, 0,
                        "Could not load the Tomcat server configuration", null ) );
            }

        }
        catch( Exception e )
        {
            throw new CoreException( LiferayServerCore.createErrorStatus( e ) );
        }

    }

    private Node getAttributeNode( String tagName, String nodeTagName, String nodeTagValue )
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
                        if( itemNode.getNodeName().equals( nodeTagName ) )
                        {
                            if( itemNode.getNodeValue().equals( nodeTagValue ) )
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

    private List<LiferayServerPort> getPortsValue(
        Document document, String tagName, String portName, String protocolName )
    {
        List<LiferayServerPort> retPorts = new ArrayList<LiferayServerPort>();

        try
        {
            NodeList connectorNodes = document.getElementsByTagName( tagName );

            for( int i = 0; i < connectorNodes.getLength(); i++ )
            {
                Node node = connectorNodes.item( i );

                NamedNodeMap attributes = node.getAttributes();
                String portValue = null;
                String protocolValue = null;

                for( int j = 0; j < attributes.getLength(); j++ )
                {
                    Node itemNode = attributes.item( j );

                    if( itemNode != null )
                    {
                        if( itemNode.getNodeName().equals( portName ) )
                        {
                            portValue = itemNode.getNodeValue();
                        }

                        if( itemNode.getNodeName().equals( protocolName ) )
                        {
                            protocolValue = itemNode.getNodeValue();
                        }
                    }
                }
                boolean existed = false;

                for( LiferayServerPort port : retPorts )
                {
                    if( port.getId().equals( protocolValue ) && protocolValue != null )
                    {
                        existed = true;
                        break;
                    }
                }

                if( !existed )
                {
                    LiferayServerPort createServerPort =
                        createLiferayServerPort( protocolValue, protocolValue, portValue );
                    retPorts.add( createServerPort );
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
    public void saveBundleConfiguration( IProgressMonitor monitor ) throws CoreException
    {
        try
        {
            final IPath file = bundle.getAppServerDir().append( "conf" ).append( "server.xml" );

            if( file.toFile().exists() && isServerDirty )
            {
                XMLUtil.save( file.toOSString(), configurationDocument );
            }
        }
        catch( Exception e )
        {
            throw new CoreException( LiferayServerCore.createErrorStatus( e ) );
        }
    }

    @Override
    public void applyBundleChange( LiferayServerPort port )
    {
        if( port.getStoreLocation().equals( LiferayServerPort.defaultStoreInXML ) )
        {
            final Node connectorNode = getAttributeNode( "Connector", "protocol", port.getId() );

            if( connectorNode != null )
            {
                XMLUtil.setNodeValue( connectorNode, port.getId(), String.valueOf( port.getPort() ) );
            }

            final Node serverNode = getAttributeNode( "Server", "shutdown", port.getId() );

            if( serverNode != null )
            {
                XMLUtil.setNodeValue( serverNode, port.getId(), String.valueOf( port.getPort() ) );
            }
        }
    }

    @Override
    public int getHttpPort()
    {
        List<LiferayServerPort> ports = new ArrayList<LiferayServerPort>();

        if( isServerDirty == true )
        {
            IPath file = bundle.getAppServerDir().append( "conf" ).append( "server.xml" );

            if( file.toFile().exists() )
            {
                try
                {
                    Document refreshDocument =
                        XMLUtil.getDocumentBuilder().parse( new InputSource( new FileInputStream( file.toFile() ) ) );

                    ports.addAll( getPortsValue( refreshDocument, "Connector", "port", "protocol" ) );
                    ports.addAll( getPortsValue( refreshDocument, "Server", "port", "shutdown" ) );
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
            if( port.getProtocol().equals( "HTTP/1.1" ) )
            {
                return port.getPort();
            }
        }

        return -1;
    }

}
