/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
import com.liferay.ide.core.util.StringPool;
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
 * @author Simon Jiang
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
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<portlet-app xmlns=\"http://java.sun.com/xml/ns/" //$NON-NLS-1$
                        + "portlet/portlet-app_2_0.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:" //$NON-NLS-1$
                        + "schemaLocation=\"http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd http://java.sun.com/" //$NON-NLS-1$
                        + "xml/ns/portlet/portlet-app_2_0.xsd\" version=\"2.0\">\n</portlet-app>"; //$NON-NLS-1$

                createDefaultDescriptor( templateString, "" ); //$NON-NLS-1$
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
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE liferay-portlet-app PUBLIC \"-//Liferay//" //$NON-NLS-1$
                        + "DTD Portlet Application {0}//EN\" \"http://www.liferay.com/dtd/liferay-portlet-app_{1}.dtd" //$NON-NLS-1$
                        + "\">\n\n<liferay-portlet-app>\n\u0009<role-mapper>\n\u0009\u0009<role-name>administrator</role-" //$NON-NLS-1$
                        + "name>\n\u0009\u0009<role-link>Administrator</role-link>\n\u0009</role-mapper>\n\u0009<role-" //$NON-NLS-1$
                        + "mapper>\n\u0009\u0009<role-name>guest</role-name>\n\u0009\u0009<role-link>Guest</role-link>\n" //$NON-NLS-1$
                        + "\u0009</role-mapper>\n\u0009<role-mapper>\n\u0009\u0009<role-name>power-user</role-name>\n" //$NON-NLS-1$
                        + "\u0009\u0009<role-link>Power User</role-link>\n\u0009</role-mapper>\n\u0009<role-mapper>\n" //$NON-NLS-1$
                        + "\u0009\u0009<role-name>user</role-name>\n\u0009\u0009<role-link>User</role-link>\n\u0009</" //$NON-NLS-1$
                        + "role-mapper>\n</liferay-portlet-app>"; //$NON-NLS-1$

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
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE display PUBLIC \"-//Liferay//DTD Display " //$NON-NLS-1$
                        + "{0}//EN\" \"http://www.liferay.com/dtd/liferay-display_{1}.dtd\">\n\n<display>\n</display>"; //$NON-NLS-1$

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

    public IStatus configurePortletXml( final String newPortletName )
    {
        final IFile descriptorFile = getDescriptorFile( ILiferayConstants.PORTLET_XML_FILE );

        DOMModelOperation domModelOperation = new DOMModelEditOperation( descriptorFile )
        {
            protected void createDefaultFile()
            {
            }

            protected IStatus doExecute( IDOMDocument document )
            {
                return updatePortletName( document, newPortletName );
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
            }

            protected IStatus doExecute( IDOMDocument document )
            {
                return updatePortletName( document, newPortletName );
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
            }

            protected IStatus doExecute( IDOMDocument document )
            {
                return updatePortletId( document, newPortletName );
            }
        };

        status = domModelOperation.execute();

        return status;
    }
    
    
    public String[] getAllPortletCategories()
    {
        final List<String> allPortletCategories = new ArrayList<String>();

        final IFile descriptorFile = getDescriptorFile( ILiferayConstants.LIFERAY_DISPLAY_XML_FILE );

        if( descriptorFile != null )
        {
            DOMModelOperation op = new DOMModelReadOperation( descriptorFile )
            {
                protected IStatus doExecute( IDOMDocument document )
                {
                    NodeList nodeList = document.getElementsByTagName( "category" ); //$NON-NLS-1$

                    if( nodeList != null && nodeList.getLength() > 0 )
                    {
                        for( int i = 0; i < nodeList.getLength(); i++ )
                        {
                            Element categoryElemnt = (Element) nodeList.item( i );
                            String categoryName = categoryElemnt.getAttribute( "name" );

                            if( categoryName != null && !categoryName.matches( "\\s*" ) )
                            {
                                allPortletCategories.add( categoryName );
                            }
                        }
                    }

                    return Status.OK_STATUS;
                }
            };

            op.execute();
        }

        return allPortletCategories.toArray( new String[0] );
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
                    NodeList nodeList = document.getElementsByTagName( "portlet-name" ); //$NON-NLS-1$

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

    protected String getPortletClassText( IDataModel model )
    {
        return model.getStringProperty( QUALIFIED_CLASS_NAME );
    }

    protected IStatus removeAllElements( IDOMDocument document, String tagName )
    {
        if( document == null )
        {
            return PortletCore.createErrorStatus( MessageFormat.format(
                "Could not remove {0} elements: null document", tagName ) ); //$NON-NLS-1$
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
            return PortletCore.createErrorStatus( MessageFormat.format( "Could not remove {0} elements", tagName ), ex ); //$NON-NLS-1$
        }

        return Status.OK_STATUS;
    }

    public IStatus removeAllPortlets()
    {
        final String portletTagName = "portlet"; //$NON-NLS-1$
        final String categoryTagName = "category"; //$NON-NLS-1$

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
        Element rootElement = document.getDocumentElement();

        // for the category assignment check to see if there is already a
        // category element with that id
        Element category = null;

        String modelCategory = model.getStringProperty( CATEGORY );

        for( Element child : getChildElements( rootElement ) )
        {
            if( child.getNodeName().equals( "category" ) && modelCategory.equals( child.getAttribute( "name" ) ) ) //$NON-NLS-1$ //$NON-NLS-2$
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
                if( child.getNodeName().equals( "portlet" ) && modelId.equals( child.getAttribute( "id" ) ) ) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    id = child;

                    break;
                }
            }
        }
        else
        {
            category = document.createElement( "category" ); //$NON-NLS-1$
            category.setAttribute( "name", modelCategory ); //$NON-NLS-1$

            rootElement.appendChild( category );

            Node newline = document.createTextNode( System.getProperty( "line.separator" ) ); //$NON-NLS-1$

            rootElement.appendChild( newline );
        }

        if( id == null )
        {
            NodeUtil.appendChildElement( category, "portlet" ).setAttribute( "id", modelId ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // format the new node added to the model;
        FormatProcessorXML processor = new FormatProcessorXML();

        processor.formatNode( category );

        return Status.OK_STATUS;
    }

    public IStatus updateLiferayPortletXML( IDOMDocument document, final IDataModel model )
    {
        // <liferay-portlet-app> element
        Element rootElement = document.getDocumentElement();

        // new <portlet> element
        Element newPortletElement = document.createElement( "portlet" ); //$NON-NLS-1$

        NodeUtil.appendChildElement( newPortletElement, "portlet-name", model.getStringProperty( LIFERAY_PORTLET_NAME ) ); //$NON-NLS-1$

        NodeUtil.appendChildElement( newPortletElement, "icon", model.getStringProperty( ICON_FILE ) ); //$NON-NLS-1$

        if( model.getBooleanProperty( ADD_TO_CONTROL_PANEL ) )
        {
            String entryCategory = model.getStringProperty( ENTRY_CATEGORY ).replaceAll( "^category\\.", StringPool.EMPTY ); //$NON-NLS-1$
            NodeUtil.appendChildElement( newPortletElement, "control-panel-entry-category", entryCategory ); //$NON-NLS-1$
            NodeUtil.appendChildElement( newPortletElement, "control-panel-entry-weight", model.getStringProperty( ENTRY_WEIGHT ) ); //$NON-NLS-1$

            if( model.getBooleanProperty( CREATE_ENTRY_CLASS ) )
            {
                NodeUtil.appendChildElement(
                    newPortletElement, "control-panel-entry-class", model.getStringProperty( JAVA_PACKAGE ) + "." + //$NON-NLS-1$ //$NON-NLS-2$
                        model.getStringProperty( ENTRY_CLASS_NAME ) );
            }
        }

        if( model.getBooleanProperty( ALLOW_MULTIPLE ) )
        {
            NodeUtil.appendChildElement(
                newPortletElement, "instanceable", Boolean.toString( model.getBooleanProperty( ALLOW_MULTIPLE ) ) ); //$NON-NLS-1$
        }

        NodeUtil.appendChildElement( newPortletElement, "header-portlet-css", model.getStringProperty( CSS_FILE ) ); //$NON-NLS-1$

        NodeUtil.appendChildElement( newPortletElement, "footer-portlet-javascript", model.getStringProperty( JAVASCRIPT_FILE ) ); //$NON-NLS-1$

        NodeUtil.appendChildElement( newPortletElement, "css-class-wrapper", model.getStringProperty( CSS_CLASS_WRAPPER ) ); //$NON-NLS-1$

        // must append this before any role-mapper elements
        Element firstRoleMapper = null;

        for( Element child : getChildElements( rootElement ) )
        {
            if( child.getNodeName().equals( "role-mapper" ) ) //$NON-NLS-1$
            {
                firstRoleMapper = child;

                break;
            }
        }
        Node newline = document.createTextNode( System.getProperty( "line.separator" ) ); //$NON-NLS-1$

        if( firstRoleMapper != null )
        {
            rootElement.insertBefore( newPortletElement, firstRoleMapper );

            rootElement.insertBefore( newline, firstRoleMapper );
        }
        else
        {
            rootElement.appendChild( newPortletElement );

            rootElement.appendChild( newline );
        }

        // format the new node added to the model;
        FormatProcessorXML processor = new FormatProcessorXML();

        processor.formatNode( newPortletElement );

        return Status.OK_STATUS;
    }

    public IStatus updatePortletXML( IDOMDocument document, IDataModel model )
    {
        // <portlet-app> element
        Element rootElement = document.getDocumentElement();

        // new <portlet> element
        Element newPortletElement = document.createElement( "portlet" ); //$NON-NLS-1$

        NodeUtil.appendChildElement( newPortletElement, "portlet-name", model.getStringProperty( PORTLET_NAME ) ); //$NON-NLS-1$

        NodeUtil.appendChildElement( newPortletElement, "display-name", model.getStringProperty( DISPLAY_NAME ) ); //$NON-NLS-1$

        NodeUtil.appendChildElement( newPortletElement, "portlet-class", getPortletClassText( model ) ); //$NON-NLS-1$

        // add <init-param> elements as needed
        List<ParamValue> initParams = (List<ParamValue>) model.getProperty( INIT_PARAMS );

        for( ParamValue initParam : initParams )
        {
            Element newInitParamElement = NodeUtil.appendChildElement( newPortletElement, "init-param" ); //$NON-NLS-1$

            NodeUtil.appendChildElement( newInitParamElement, "name", initParam.getName() ); //$NON-NLS-1$

            NodeUtil.appendChildElement( newInitParamElement, "value", initParam.getValue() ); //$NON-NLS-1$
        }

        // expiration cache
        NodeUtil.appendChildElement( newPortletElement, "expiration-cache", "0" ); //$NON-NLS-1$ //$NON-NLS-2$

        // supports node
        Element newSupportsElement = NodeUtil.appendChildElement( newPortletElement, "supports" ); //$NON-NLS-1$

        NodeUtil.appendChildElement( newSupportsElement, "mime-type", "text/html" ); //$NON-NLS-1$ //$NON-NLS-2$

        // for all support modes need to add into
        for( String portletMode : ALL_PORTLET_MODES )
        {
            if( model.getBooleanProperty( portletMode ) )
            {
                NodeUtil.appendChildElement(
                    newSupportsElement, "portlet-mode", //$NON-NLS-1$
                    model.getPropertyDescriptor( portletMode ).getPropertyDescription() );
            }
        }

        if( model.getBooleanProperty( CREATE_RESOURCE_BUNDLE_FILE ) )
        {
            // need to remove .properties off the end of the bundle_file_path
            String bundlePath = model.getStringProperty( CREATE_RESOURCE_BUNDLE_FILE_PATH );
            String bundleValue = bundlePath.replaceAll( "\\.properties$", StringPool.EMPTY ); //$NON-NLS-1$
            NodeUtil.appendChildElement( newPortletElement, "resource-bundle", bundleValue ); //$NON-NLS-1$
        }

        // add portlet-info
        Element newPortletInfoElement = NodeUtil.appendChildElement( newPortletElement, "portlet-info" ); //$NON-NLS-1$

        NodeUtil.appendChildElement( newPortletInfoElement, "title", model.getStringProperty( TITLE ) ); //$NON-NLS-1$

        NodeUtil.appendChildElement( newPortletInfoElement, "short-title", model.getStringProperty( SHORT_TITLE ) ); //$NON-NLS-1$

        NodeUtil.appendChildElement( newPortletInfoElement, "keywords", model.getStringProperty( KEYWORDS ) ); //$NON-NLS-1$

        // security role refs
        for( String roleName : DEFAULT_SECURITY_ROLE_NAMES )
        {
            NodeUtil.appendChildElement( NodeUtil.appendChildElement( newPortletElement, "security-role-ref" ), "role-name", roleName ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // check for event-definition elements

        Node refNode = null;

        String[] refElementNames =
            new String[] { "custom-portlet-mode", "custom-window-state", "user-attribute", "security-constraint", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                "resource-bundle", "filter", "filter-mapping", "default-namespace", "event-definition", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                "public-render-parameter", "listener", "container-runtime-option" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        for( int i = 0; i < refElementNames.length; i++ )
        {
            refNode = NodeUtil.findFirstChild( rootElement, refElementNames[i] );

            if( refNode != null )
            {
                break;
            }
        }

        rootElement.insertBefore( newPortletElement, refNode );

        // append a newline text node
        rootElement.appendChild( document.createTextNode( System.getProperty( "line.separator" ) ) ); //$NON-NLS-1$

        // format the new node added to the model;
        FormatProcessorXML processor = new FormatProcessorXML();

        processor.formatNode( newPortletElement );

        return Status.OK_STATUS;
    }

    
    public IStatus updatePortletName( IDOMDocument document, final String newPortletName )
    {

        final Element rootElement = document.getDocumentElement();

        final NodeList portletNodes = rootElement.getElementsByTagName( "portlet" );

        if( portletNodes.getLength() > 0 )
        {
            final Element lastPortletElement = (Element) portletNodes.item( portletNodes.getLength() - 1 );
            Element portletName = NodeUtil.findChildElement( lastPortletElement, "portlet-name" );
            portletName.replaceChild( document.createTextNode( newPortletName ), portletName.getFirstChild() );
        }
        return Status.OK_STATUS;
    }

    public IStatus updatePortletId( IDOMDocument document, final String newPortletName )
    {

        final Element rootElement = document.getDocumentElement();

        final NodeList portletNodes = rootElement.getElementsByTagName( "category" );

        if( portletNodes.getLength() > 0 )
        {
            final Element lastPortletElement = (Element) portletNodes.item( portletNodes.getLength() - 1 );
            Element portletName = NodeUtil.findChildElement( lastPortletElement, "portlet" );
            portletName.setAttribute( "id", newPortletName );
        }
        return Status.OK_STATUS;
    }
}
