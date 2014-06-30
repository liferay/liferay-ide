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

package com.liferay.ide.service.core.operation;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;
import com.liferay.ide.project.core.descriptor.RemoveSampleElementsOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class ServiceBuilderDescriptorHelper extends LiferayDescriptorHelper
{
    private static final String DESCRIPTOR_FILE = ILiferayConstants.LIFERAY_SERVICE_BUILDER_XML_FILE; 

    private final String NEW_LINE = System.getProperty( "line.separator" );

    public ServiceBuilderDescriptorHelper()
    {
        super();
    }

    public ServiceBuilderDescriptorHelper( IProject project )
    {
        super( project );
    }

    public IStatus addDefaultColumns( final String elementName )
    {
        final IFile descriptorFile = getDescriptorFile();

        DOMModelEditOperation editOperation = new DOMModelEditOperation( descriptorFile )
        {
            @Override
            protected IStatus doExecute( IDOMDocument document )
            {
                return doAddDefaultColumns( document, elementName );
            }
        };

        return editOperation.execute();
    }

    public IStatus addDefaultEntity()
    {
        IStatus status = Status.OK_STATUS;

        final IFile descriptorFile = getDescriptorFile();

        if( descriptorFile != null )
        {
            DOMModelEditOperation editOperation = new DOMModelEditOperation( descriptorFile )
            {
                @Override
                protected IStatus doExecute( IDOMDocument document )
                {
                    return doAddDefaultEntity( document );
                }
            };

            status = editOperation.execute();
        }

        return status;
    }

    @Override
    protected void addDescriptorOperations()
    {
        addDescriptorOperation
        (
            new RemoveSampleElementsOperation()
            {
                @Override
                public IStatus removeSampleElements()
                {
                    return removeAllEntities();
                }
            }
        );
    }

    public IStatus addEntity( final String entityName )
    {
        final IFile descriptorFile = getDescriptorFile();

        if( descriptorFile == null || ! descriptorFile.exists() )
        {
            return Status.OK_STATUS;
        }

        DOMModelEditOperation editOperation = new DOMModelEditOperation( descriptorFile )
        {
            @Override
            protected IStatus doExecute( IDOMDocument document )
            {
                return doAddEntity( document, entityName );
            }
        };

        return editOperation.execute();
    }

    private void appendComment( Element element, String comment )
    {
        final Document document = element.getOwnerDocument();

        element.appendChild( document.createTextNode( NEW_LINE + NEW_LINE ) );
        element.appendChild( document.createComment( comment ) );
        element.appendChild( document.createTextNode( NEW_LINE + NEW_LINE ) );
    }

    protected IStatus doAddDefaultColumns( IDOMDocument document, String entityName )
    {
        Element entityElement = null;

        NodeList nodes = document.getDocumentElement().getChildNodes();

        if( nodes != null && nodes.getLength() > 0 )
        {
            for( int i = 0; i < nodes.getLength(); i++ )
            {
                Node node = nodes.item( i );

                if( node.getNodeName().equals( "entity" )  && node instanceof Element )
                {
                   if( entityName.equals( ( (Element) node ).getAttribute( "name" ) ) )
                   {
                       entityElement = (Element)node;
                   }
                }
            }
        }

        if( entityElement == null )
        {
            return Status.CANCEL_STATUS;
        }

        // <!-- PK fields -->
        appendComment( entityElement, " PK fields " );

        Element columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", generateEntityId( entityName ) );
        columnElem.setAttribute( "type", "long" );
        columnElem.setAttribute( "primary", "true" );

        // <!-- Group instance -->
        appendComment( entityElement, " Group instance " );
        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "groupId" );
        columnElem.setAttribute( "type", "long" );

        //<!-- Aduit fields -->
        appendComment( entityElement, " Audit fields " );

        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "companyId" );
        columnElem.setAttribute( "type", "long" );

        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "userId" );
        columnElem.setAttribute( "type", "long" );

        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "userName" );
        columnElem.setAttribute( "type", "String" );

        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "createDate" );
        columnElem.setAttribute( "type", "Date" );

        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "modifiedDate" );
        columnElem.setAttribute( "type", "Date" );

        entityElement.appendChild( document.createTextNode( NEW_LINE ) );

        new FormatProcessorXML().formatNode( entityElement );

        return Status.OK_STATUS;
    }

    protected IStatus doAddDefaultEntity( IDOMDocument document )
    {
        final String entityName = generateSampleEntityName( document );

        Element rootElement = document.getDocumentElement();

        // new <entity> element
        Element entityElement = document.createElement( "entity" );
        entityElement.setAttribute( "name", entityName );
        entityElement.setAttribute( "local-service", "true" );
        entityElement.setAttribute( "remote-service", "true" );

        // <!-- PK fields -->
        appendComment( entityElement, " PK fields " );

        Element columnElem = NodeUtil.appendChildElement( entityElement, "column" );

        columnElem.setAttribute( "name", generateEntityId( entityName ) );
        columnElem.setAttribute( "type", "long" );
        columnElem.setAttribute( "primary", "true" );

        // <!-- Group instance -->
        appendComment( entityElement, " Group instance " );
        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "groupId" );
        columnElem.setAttribute( "type", "long" );

        //<!-- Aduit fields -->
        appendComment( entityElement, " Audit fields " );

        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "companyId" );
        columnElem.setAttribute( "type", "long" );

        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "userId" );
        columnElem.setAttribute( "type", "long" );

        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "userName" );
        columnElem.setAttribute( "type", "String" );

        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "createDate" );
        columnElem.setAttribute( "type", "Date" );

        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "modifiedDate" );
        columnElem.setAttribute( "type", "Date" );

        //<!-- Other fields -->
        appendComment( entityElement, " Other fields " );

        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "field1" );
        columnElem.setAttribute( "type", "String" );

        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "field2" );
        columnElem.setAttribute( "type", "boolean" );

        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "field3" );
        columnElem.setAttribute( "type", "int" );

        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "field4" );
        columnElem.setAttribute( "type", "Date" );

        columnElem = NodeUtil.appendChildElement( entityElement, "column" );
        columnElem.setAttribute( "name", "field5" );
        columnElem.setAttribute( "type", "String" );

        // <!-- Order -->
        appendComment( entityElement, " Order " );

        final Element orderElem = NodeUtil.appendChildElement( entityElement, "order" );
        orderElem.setAttribute( "by", "asc" );
        NodeUtil.appendChildElement( orderElem, "order-column" ).setAttribute( "name", "field1" );

        // <!-- Finder methods -->
        appendComment( entityElement, " Finder methods " );

        final Element finderElem = NodeUtil.appendChildElement( entityElement, "finder" );
        finderElem.setAttribute( "name", "Field2" );
        finderElem.setAttribute( "return-type", "Collection" );
        NodeUtil.appendChildElement( finderElem, "finder-column" ).setAttribute( "name", "field2" );

        // Insert the <entity> element
        Node refNode = NodeUtil.findFirstChild( rootElement, "exceptions" );
        if( refNode == null )
        {
            NodeUtil.findFirstChild( rootElement, "service-builder-import" );
        }

        rootElement.insertBefore( entityElement, refNode );

        new FormatProcessorXML().formatNode( entityElement );

        rootElement.appendChild( document.createTextNode( NEW_LINE ) );

        return Status.OK_STATUS;
    }

    protected IStatus doAddEntity( IDOMDocument document, String entityName )
    {
        NodeList entities = document.getElementsByTagName( "entity" );

        // If there is entity named "entityName", do nothing
        for( int i = 0; i < entities.getLength(); ++i )
        {
            Node entity = entities.item( i );

            if( entity instanceof Element )
            {
                String name = ( (Element) entity ).getAttribute( "name" );

                if( name != null && name.equals( entityName ) )
                {
                    return Status.OK_STATUS;
                }
            }

        }

        Element rootElement = document.getDocumentElement();

        // new <entity> element
        Element entityElement = document.createElement( "entity" );

        entityElement.setAttribute( "name", entityName );

        // Insert the <entity> element
        Node refNode = NodeUtil.findFirstChild( rootElement, "exceptions" );

        if( refNode == null )
        {
            NodeUtil.findFirstChild( rootElement, "service-builder-import" );
        }

        rootElement.insertBefore( entityElement, refNode );

        new FormatProcessorXML().formatNode( entityElement );

        rootElement.appendChild( document.createTextNode( NEW_LINE ) );

        return Status.OK_STATUS;
    }

    private String generateEntityId( String entityName )
    {
        if( entityName == null )
        {
            return "Id";
        }

       return Character.toLowerCase( entityName.charAt( 0 ) ) + 
                       ( entityName.length() > 1 ? entityName.substring( 1 ) : "" ) + "Id";
    }

    private String generateSampleEntityName( IDOMDocument document )
    {
        String retval = "Sample";

        List<String> entityNames = new ArrayList<String>();

        final NodeList nodes = document.getDocumentElement().getChildNodes();

        if( nodes != null && nodes.getLength() > 0 )
        {
            Node node = null;

            for( int i = 0; i < nodes.getLength(); i++ )
            {
                node = nodes.item( i );

                if( "entity".equals( node.getNodeName() ) )
                {
                    String entityName = ( (Element) node ).getAttribute( "name" );

                    if( ! CoreUtil.isNullOrEmpty( entityName ) )
                    {
                        entityNames.add( entityName );
                    }
                }
            }
        }

        while( entityNames.contains( retval ) )
        {
            retval = nextSuffix( retval );
        }

        return retval;
    }

    public IFile getDescriptorFile()
    {
        return super.getDescriptorFile( DESCRIPTOR_FILE );
    }

    private String nextSuffix( String val )
    {
        final Matcher matcher = Pattern.compile( "(Sample)([0-9]+)$" ).matcher( val );

        if( matcher.matches() )
        {
            int num = Integer.parseInt( matcher.group( 2 ) );

            return matcher.group( 1 ) + ( num + 1 ) ;
        }

        return val + "1";
    }

    public IStatus removeAllEntities()
    {
        final IFile descriptorFile = getDescriptorFile();

        if( descriptorFile == null || ! descriptorFile.exists() )
        {
            return Status.OK_STATUS;
        }

        final String tagName = "entity";

        DOMModelEditOperation editOperation = new DOMModelEditOperation( descriptorFile )
        {
            @Override
            protected IStatus doExecute( IDOMDocument document )
            {
                return removeAllElements( document, tagName );
            }
        };

        return editOperation.execute();
    }
}
