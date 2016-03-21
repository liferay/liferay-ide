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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.core.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Greg Amerson
 * @author Terry Jia
 */
public class NodeUtil
{

    public static Element appendChildElement( Element parentElement, String newElementName )
    {
        return NodeUtil.appendChildElement( parentElement, newElementName, null );
    }

    public static Element appendChildElement( Element parentElement, String newElementName, String initialTextContent )
    {
        Element newChildElement = null;

        if( parentElement != null && newElementName != null )
        {
            Document ownerDocument = parentElement.getOwnerDocument();

            newChildElement = ownerDocument.createElement( newElementName );

            if( initialTextContent != null )
            {
                newChildElement.appendChild( ownerDocument.createTextNode( initialTextContent ) );
            }

            parentElement.appendChild( newChildElement );
        }

        return newChildElement;
    }

    public static Node appendTextNode( Element parentElement, String initialTextContent )
    {
        Node newChildElement = null;

        if( parentElement != null )
        {
            Document ownerDocument = parentElement.getOwnerDocument();

            newChildElement = ownerDocument.createTextNode( initialTextContent );

            parentElement.appendChild( newChildElement );
        }

        return newChildElement;
    }

    public static Element findChildElement( Element parentElement, String elementName )
    {
        Element retval = null;

        if( parentElement == null )
        {
            return retval;
        }

        NodeList children = parentElement.getChildNodes();

        for( int i = 0; i < children.getLength(); i++ )
        {
            Node child = children.item( i );

            if( child instanceof Element && child.getNodeName().equals( elementName ) )
            {
                retval = (Element) child;
                break;
            }
        }

        return retval;
    }

    public static Node findFirstChild( Element element, String elementName )
    {
        if( element != null && !( CoreUtil.isNullOrEmpty( elementName ) ) )
        {
            NodeList children = element.getChildNodes();

            if( children != null && children.getLength() > 0 )
            {
                for( int i = 0; i < children.getLength(); i++ )
                {
                    Node child = children.item( i );

                    if( elementName.equals( child.getNodeName() ) )
                    {
                        return child;
                    }
                }
            }
        }

        return null;
    }

    public static Node findLastChild( Element element, String elementName )
    {
        if( element != null && !( CoreUtil.isNullOrEmpty( elementName ) ) )
        {
            NodeList children = element.getChildNodes();

            if( children != null && children.getLength() > 0 )
            {
                for( int i = children.getLength() - 1; i >= 0; i-- )
                {
                    Node child = children.item( i );

                    if( elementName.equals( child.getNodeName() ) )
                    {
                        return child;
                    }
                }
            }
        }

        return null;
    }

    public static String getChildElementContent( Node parent, String childElement )
    {
        String retval = null;

        NodeList children = parent.getChildNodes();

        if( children != null && children.getLength() > 0 )
        {
            for( int i = 0; i < children.getLength(); i++ )
            {
                Node child = children.item( i );

                if( child instanceof Element && child.getNodeName().equals( childElement ) )
                {
                    return getTextContent( (Element) child );
                }
            }
        }
        return retval;
    }

    public static Node getFirstNamedChildNode( Element element, String string )
    {
        NodeList children = element.getChildNodes();

        if( children != null && children.getLength() > 0 )
        {
            for( int i = 0; i < children.getLength(); i++ )
            {
                Node item = children.item( i );

                if( item.getNodeName().equals( string ) )
                {
                    return item;
                }
            }
        }

        return null;
    }

    public static String getTextContent( Node node )
    {
        NodeList children = node.getChildNodes();

        if( children.getLength() == 1 )
        {
            return children.item( 0 ).getNodeValue().trim();
        }

        StringBuffer s = new StringBuffer();

        Node child = node.getFirstChild();

        while( child != null )
        {
            s.append( child.getNodeValue().trim() );

            child = child.getNextSibling();
        }

        return s.toString().trim();
    }

    public static Element insertChildElement(
        Element parentElement, Node refNode, String newElementName, String initialTextContent )
    {
        Element newChildElement = null;

        if( parentElement != null && newElementName != null )
        {
            Document ownerDocument = parentElement.getOwnerDocument();

            newChildElement = ownerDocument.createElement( newElementName );

            if( initialTextContent != null )
            {
                newChildElement.appendChild( ownerDocument.createTextNode( initialTextContent ) );
            }

            parentElement.insertBefore( newChildElement, refNode );
        }

        return newChildElement;
    }

    public static Element insertChildElementAfter(
        Element parentElement, Node refNode, String newElementName, String initialTextContent )
    {
        Element newChildElement = null;

        if( parentElement != null && newElementName != null )
        {
            Document ownerDocument = parentElement.getOwnerDocument();

            newChildElement = ownerDocument.createElement( newElementName );

            if( initialTextContent != null )
            {
                newChildElement.appendChild( ownerDocument.createTextNode( initialTextContent ) );
            }

            if( parentElement.getLastChild().equals( refNode ) )
            {
                parentElement.appendChild( newChildElement );
            }
            else
            {
                parentElement.insertBefore( newChildElement, refNode.getNextSibling() );
            }
        }

        return newChildElement;
    }

    public static void removeChildren( Element element )
    {
        while( element != null && element.hasChildNodes() )
        {
            element.removeChild( element.getFirstChild() );
        }
    }

    public static void removeChildren( Node node )
    {
        if( node == null || node.getChildNodes() == null || node.getChildNodes().getLength() <= 0 )
        {
            return;
        }

        NodeList children = node.getChildNodes();

        for( int i = 0; i < children.getLength(); i++ )
        {
            node.removeChild( children.item( i ) );
        }
    }

    public static Text setTextContent( Node namespaceNode, String textContent )
    {
        Text retval = null;

        if( namespaceNode instanceof Text )
        {
            namespaceNode.setNodeValue( textContent );

            retval = (Text) namespaceNode;
        }
        else if( namespaceNode instanceof Element )
        {
            Element namespaceElement = (Element) namespaceNode;

            removeChildren( namespaceElement );

            retval = namespaceElement.getOwnerDocument().createTextNode( textContent );

            namespaceElement.appendChild( retval );
        }
        return retval;
    }

}
