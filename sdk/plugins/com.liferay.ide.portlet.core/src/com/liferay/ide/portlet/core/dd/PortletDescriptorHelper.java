/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.portlet.core.dd;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.project.core.util.LiferayDescriptorHelper;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.j2ee.common.ParamValue;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings( { "restriction", "unchecked" } )
public class PortletDescriptorHelper extends LiferayDescriptorHelper implements INewPortletClassDataModelProperties
{

    public PortletDescriptorHelper( IProject project )
    {
        super( project );
    }

    public IStatus addNewPortlet( final IDataModel model )
    {
        final IFile descriptorFile = getDescriptorFile( ILiferayConstants.PORTLET_XML_FILE );

        DOMModelOperation domModelOperation = new DOMModelEditOperation( descriptorFile )
        {
            protected void createDefaultFile()
            {
                String templateString =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<portlet-app xmlns=\"http://java.sun.com/xml/ns/"
                        + "portlet/portlet-app_2_0.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:"
                        + "schemaLocation=\"http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd http://java.sun.com/"
                        + "xml/ns/portlet/portlet-app_2_0.xsd\" version=\"2.0\">\n</portlet-app>";

                createDefaultDescriptor( templateString, "" );
            }

            protected IStatus doExecute( IDOMDocument document )
            {
                return updatePortletXML( document, model );
            }
        };

        IStatus status = domModelOperation.execute();

        if( !status.isOK() )
        {
            return status;
        }

        domModelOperation = new DOMModelEditOperation( getDescriptorFile( ILiferayConstants.LIFERAY_PORTLET_XML_FILE ) )
        {
            protected void createDefaultFile()
            {
                String templateString =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE liferay-portlet-app PUBLIC \"-//Liferay//"
                        + "DTD Portlet Application {0}//EN\" \"http://www.liferay.com/dtd/liferay-portlet-app_{1}.dtd"
                        + "\">\n\n<liferay-portlet-app>\n\u0009<role-mapper>\n\u0009\u0009<role-name>administrator</role-"
                        + "name>\n\u0009\u0009<role-link>Administrator</role-link>\n\u0009</role-mapper>\n\u0009<role-"
                        + "mapper>\n\u0009\u0009<role-name>guest</role-name>\n\u0009\u0009<role-link>Guest</role-link>\n"
                        + "\u0009</role-mapper>\n\u0009<role-mapper>\n\u0009\u0009<role-name>power-user</role-name>\n"
                        + "\u0009\u0009<role-link>Power User</role-link>\n\u0009</role-mapper>\n\u0009<role-mapper>\n"
                        + "\u0009\u0009<role-name>user</role-name>\n\u0009\u0009<role-link>User</role-link>\n\u0009</"
                        + "role-mapper>\n</liferay-portlet-app>";

                createDefaultDescriptor( templateString, getDescriptorVersion() );
            }

            protected IStatus doExecute( IDOMDocument document )
            {
                return updateLiferayPortletXML( document, model );
            }
        };

        status = domModelOperation.execute();

        if( !status.isOK() )
        {
            return status;
        }

        domModelOperation = new DOMModelEditOperation( getDescriptorFile( ILiferayConstants.LIFERAY_DISPLAY_XML_FILE ) )
        {
            protected void createDefaultFile()
            {
                String templateString =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE display PUBLIC \"-//Liferay//DTD Display "
                        + "{0}//EN\" \"http://www.liferay.com/dtd/liferay-display_{1}.dtd\">\n\n<display>\n</display>";

                createDefaultDescriptor( templateString, getDescriptorVersion() );
            }

            protected IStatus doExecute( IDOMDocument document )
            {
                return updateLiferayDisplayXML( document, model );
            }
        };

        status = domModelOperation.execute();

        return status;
    }

    public String[] getAllPortletNames()
    {
        final List<String> allPortletNames = new ArrayList<String>();

        final IFile descriptorFile = getDescriptorFile( ILiferayConstants.PORTLET_XML_FILE );

        if( descriptorFile != null )
        {
            DOMModelOperation op = new DOMModelReadOperation( descriptorFile )
            {
                protected IStatus doExecute( IDOMDocument document )
                {
                    NodeList nodeList = document.getElementsByTagName( "portlet-name" );

                    for( int i = 0; i < nodeList.getLength(); i++ )
                    {
                        Element portletName = (Element) nodeList.item( i );
                        allPortletNames.add( NodeUtil.getTextContent( portletName ) );
                    }

                    return Status.OK_STATUS;
                }
            };

            op.execute();
        }

        return allPortletNames.toArray( new String[0] );
    }

    public IStatus removeAllPortlets()
    {
        final String portletTagName = "portlet";
        final String categoryTagName = "category";

        final IFile descriptorFile = getDescriptorFile( ILiferayConstants.PORTLET_XML_FILE );

        DOMModelEditOperation domModelOperation = new DOMModelEditOperation( descriptorFile )
        {
            protected void createDefaultFile()
            {
                // we are deleting nodes, no need to create new file
            }

            protected IStatus doExecute( IDOMDocument document )
            {
                return removeAllElements( document, portletTagName );
            }
        };

        IStatus status = domModelOperation.execute();

        if( !status.isOK() )
        {
            return status;
        }

        domModelOperation = new DOMModelEditOperation( getDescriptorFile( ILiferayConstants.LIFERAY_PORTLET_XML_FILE ) )
        {
            protected void createDefaultFile()
            {
                // we are deleting nodes, no need to create new file
            }

            protected IStatus doExecute( IDOMDocument document )
            {
                return removeAllElements( document, portletTagName );
            }
        };

        status = domModelOperation.execute();

        if( !status.isOK() )
        {
            return status;
        }

        domModelOperation = new DOMModelEditOperation( getDescriptorFile( ILiferayConstants.LIFERAY_DISPLAY_XML_FILE ) )
        {
            protected void createDefaultFile()
            {
                // we are deleting nodes, no need to create new file
            }

            protected IStatus doExecute( IDOMDocument document )
            {
                return removeAllElements( document, categoryTagName );
            }
        };

        status = domModelOperation.execute();

        return status;
    }

    public IStatus updateLiferayDisplayXML( IDOMDocument document, final IDataModel model )
    {
        // <display> element
        Element docRoot = document.getDocumentElement();

        // for the category assignment check to see if there is already a
        // category element with that id
        Element category = null;

        String modelCategory = model.getStringProperty( CATEGORY );

        for( Element child : getChildElements( docRoot ) )
        {
            if( child.getNodeName().equals( "category" ) && modelCategory.equals( child.getAttribute( "name" ) ) )
            {
                category = child;

                break;
            }
        }

        Element id = null;

        String modelId = model.getStringProperty( ID );

        if( category != null )
        {
            // check to make sure we don't aleady have a portlet with our id in
            // this category
            for( Element child : getChildElements( category ) )
            {
                if( child.getNodeName().equals( "portlet" ) && modelId.equals( child.getAttribute( "id" ) ) )
                {
                    id = child;

                    break;
                }
            }
        }
        else
        {
            category = document.createElement( "category" );
            category.setAttribute( "name", modelCategory );

            docRoot.appendChild( category );

            Node newline = document.createTextNode( System.getProperty( "line.separator" ) );

            docRoot.appendChild( newline );
        }

        if( id == null )
        {
            appendChildElement( category, "portlet" ).setAttribute( "id", modelId );
        }

        // format the new node added to the model;
        FormatProcessorXML processor = new FormatProcessorXML();

        processor.formatNode( category );

        return Status.OK_STATUS;
    }

    public IStatus updateLiferayPortletXML( IDOMDocument document, final IDataModel model )
    {
        // <liferay-portlet-app> element
        Element docRoot = document.getDocumentElement();

        // new <portlet> element
        Element newPortletElement = document.createElement( "portlet" );

        appendChildElement( newPortletElement, "portlet-name", model.getStringProperty( LIFERAY_PORTLET_NAME ) );

        appendChildElement( newPortletElement, "icon", model.getStringProperty( ICON_FILE ) );

        if( model.getBooleanProperty( ADD_TO_CONTROL_PANEL ) )
        {
            String entryCategory = model.getStringProperty( ENTRY_CATEGORY ).replaceAll( "^category\\.", "" );
            appendChildElement( newPortletElement, "control-panel-entry-category", entryCategory );
            appendChildElement( newPortletElement, "control-panel-entry-weight", model.getStringProperty( ENTRY_WEIGHT ) );

            if( model.getBooleanProperty( CREATE_ENTRY_CLASS ) )
            {
                appendChildElement(
                    newPortletElement, "control-panel-entry-class", model.getStringProperty( JAVA_PACKAGE ) + "." +
                        model.getStringProperty( ENTRY_CLASS_NAME ) );
            }
        }

        appendChildElement(
            newPortletElement, "instanceable", Boolean.toString( model.getBooleanProperty( ALLOW_MULTIPLE ) ) );

        appendChildElement( newPortletElement, "header-portlet-css", model.getStringProperty( CSS_FILE ) );

        appendChildElement( newPortletElement, "footer-portlet-javascript", model.getStringProperty( JAVASCRIPT_FILE ) );

        appendChildElement( newPortletElement, "css-class-wrapper", model.getStringProperty( CSS_CLASS_WRAPPER ) );

        // must append this before any role-mapper elements
        Element firstRoleMapper = null;

        for( Element child : getChildElements( docRoot ) )
        {
            if( child.getNodeName().equals( "role-mapper" ) )
            {
                firstRoleMapper = child;

                break;
            }
        }
        Node newline = document.createTextNode( System.getProperty( "line.separator" ) );

        if( firstRoleMapper != null )
        {
            docRoot.insertBefore( newPortletElement, firstRoleMapper );

            docRoot.insertBefore( newline, firstRoleMapper );
        }
        else
        {
            docRoot.appendChild( newPortletElement );

            docRoot.appendChild( newline );
        }

        // format the new node added to the model;
        FormatProcessorXML processor = new FormatProcessorXML();

        processor.formatNode( newPortletElement );

        return Status.OK_STATUS;
    }

    public IStatus updatePortletXML( IDOMDocument document, IDataModel model )
    {
        // <portlet-app> element
        Element docRoot = document.getDocumentElement();

        // new <portlet> element
        Element newPortletElement = document.createElement( "portlet" );

        appendChildElement( newPortletElement, "portlet-name", model.getStringProperty( PORTLET_NAME ) );

        appendChildElement( newPortletElement, "display-name", model.getStringProperty( DISPLAY_NAME ) );

        appendChildElement( newPortletElement, "portlet-class", getPortletClassText( model ) );

        // add <init-param> elements as needed
        List<ParamValue> initParams = (List<ParamValue>) model.getProperty( INIT_PARAMS );

        for( ParamValue initParam : initParams )
        {
            Element newInitParamElement = appendChildElement( newPortletElement, "init-param" );

            appendChildElement( newInitParamElement, "name", initParam.getName() );

            appendChildElement( newInitParamElement, "value", initParam.getValue() );
        }

        // expiration cache
        appendChildElement( newPortletElement, "expiration-cache", "0" );

        // supports node
        Element newSupportsElement = appendChildElement( newPortletElement, "supports" );

        appendChildElement( newSupportsElement, "mime-type", "text/html" );

        // for all support modes need to add into
        for( String portletMode : ALL_PORTLET_MODES )
        {
            if( model.getBooleanProperty( portletMode ) )
            {
                appendChildElement(
                    newSupportsElement, "portlet-mode",
                    model.getPropertyDescriptor( portletMode ).getPropertyDescription() );
            }
        }

        if( model.getBooleanProperty( CREATE_RESOURCE_BUNDLE_FILE ) )
        {
            // need to remove .properties off the end of the bundle_file_path
            String bundlePath = model.getStringProperty( CREATE_RESOURCE_BUNDLE_FILE_PATH );
            String bundleValue = bundlePath.replaceAll( "\\.properties$", "" );
            appendChildElement( newPortletElement, "resource-bundle", bundleValue );
        }

        // add portlet-info
        Element newPortletInfoElement = appendChildElement( newPortletElement, "portlet-info" );

        appendChildElement( newPortletInfoElement, "title", model.getStringProperty( TITLE ) );

        appendChildElement( newPortletInfoElement, "short-title", model.getStringProperty( SHORT_TITLE ) );

        appendChildElement( newPortletInfoElement, "keywords", model.getStringProperty( KEYWORDS ) );

        // security role refs
        for( String roleName : DEFAULT_SECURITY_ROLE_NAMES )
        {
            appendChildElement( appendChildElement( newPortletElement, "security-role-ref" ), "role-name", roleName );
        }

        // check for event-definition elements

        Node refNode = null;

        String[] refElementNames =
            new String[] { "custom-portlet-mode", "custom-window-state", "user-attribute", "security-constraint",
                "resource-bundle", "filter", "filter-mapping", "default-namespace", "event-definition",
                "public-render-parameter", "listener", "container-runtime-option" };

        for( int i = 0; i < refElementNames.length; i++ )
        {
            refNode = NodeUtil.findFirstChild( docRoot, refElementNames[i] );

            if( refNode != null )
            {
                break;
            }
        }

        docRoot.insertBefore( newPortletElement, refNode );

        // append a newline text node
        docRoot.appendChild( document.createTextNode( System.getProperty( "line.separator" ) ) );

        // format the new node added to the model;
        FormatProcessorXML processor = new FormatProcessorXML();

        processor.formatNode( newPortletElement );

        return Status.OK_STATUS;
    }

    protected String getPortletClassText( IDataModel model )
    {
        return model.getStringProperty( QUALIFIED_CLASS_NAME );
    }

    protected IStatus removeAllElements( IDOMDocument document, String tagName )
    {
        if( document == null )
        {
            return PortletCore.createErrorStatus( MessageFormat.format(
                "Could not remove {0} elements: null document", tagName ) );
        }

        NodeList elements = document.getElementsByTagName( tagName );

        try
        {
            if( elements != null && elements.getLength() > 0 )
            {
                for( int i = 0; i < elements.getLength(); i++ )
                {
                    Node element = elements.item( i );
                    element.getParentNode().removeChild( element );
                }
            }
        }
        catch( Exception ex )
        {
            return PortletCore.createErrorStatus( MessageFormat.format( "Could not remove {0} elements", tagName ), ex );
        }

        return Status.OK_STATUS;
    }

}
