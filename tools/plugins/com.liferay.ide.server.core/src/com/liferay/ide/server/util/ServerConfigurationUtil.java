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

package com.liferay.ide.server.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Andy Wu
 */
public class ServerConfigurationUtil
{

    public static String getTomcatHttpPort( String serverPath )
    {
        String retVal = "8080";

        File serverXmlFile = new File( serverPath, "conf/server.xml" );

        String portValue = getHttpPortValue( serverXmlFile, "Connector", "protocol", "HTTP/1.1", "port" );

        if( !CoreUtil.empty( portValue ) )
        {
            return portValue;
        }

        return retVal;
    }

    public static String getJbossHttpPort( String serverPath )
    {
        String retVal = "8080";

        File standaloneXmlFile = new File( serverPath, "standalone/configuration/standalone.xml" );

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

    private static String getHttpPortValue(
        File xmlFile, String tagName, String attriName, String attriValue, String targetName )
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

                        return portNode.getNodeValue();
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
}
