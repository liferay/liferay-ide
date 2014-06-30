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

package com.liferay.ide.portlet.core.dd;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.project.core.descriptor.AddNewPortletOperation;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;
import com.liferay.ide.project.core.descriptor.RemoveAllPortletsOperation;
import com.liferay.ide.project.core.descriptor.RemoveSampleElementsOperation;

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
 * @author Kuo Zhang
 */
@SuppressWarnings( { "restriction", "unchecked" } )
public class PortletDescriptorHelper extends LiferayDescriptorHelper implements INewPortletClassDataModelProperties
{

    public static final String DESCRIPTOR_FILE = ILiferayConstants.PORTLET_XML_FILE;

    private static final String DESCRIPTOR_TEMPLATE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<portlet-app xmlns=\"http://java.sun.com/xml/ns/" //$NON-NLS-1$
        + "portlet/portlet-app_2_0.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:" //$NON-NLS-1$
        + "schemaLocation=\"http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd http://java.sun.com/" //$NON-NLS-1$
        + "xml/ns/portlet/portlet-app_2_0.xsd\" version=\"2.0\">\n</portlet-app>"; //$NON-NLS-1$

    public PortletDescriptorHelper()
    {
        super();
    }

    public PortletDescriptorHelper( IProject project )
    {
        super( project );
    }

    protected void addDescriptorOperations()
    {
        addDescriptorOperation
        ( 
            new RemoveSampleElementsOperation()
            {
                @Override
                public IStatus removeSampleElements()
                {
                    return doRemoveAllPortlets();
                };
            }
        );

        addDescriptorOperation
        (
            new AddNewPortletOperation()
            {
                @Override
                public IStatus addNewPortlet( final IDataModel model )
                {
                    IStatus status = Status.OK_STATUS;

                    if( canAddNewPortlet( model ) )
                    {
                        final IFile descriptorFile = getDescriptorFile();

                        if( descriptorFile != null )
                        {
                            DOMModelOperation domModelOperation = new DOMModelEditOperation( descriptorFile )
                            {
                                protected void createDefaultFile()
                                {
                                    createDefaultDescriptor( DESCRIPTOR_TEMPLATE, "" ); //$NON-NLS-1$
                                }

                                protected IStatus doExecute( IDOMDocument document )
                                {
                                    return doAddNewPortlet( document, model );
                                }
                            };

                            status = domModelOperation.execute();
                        }
                    }

                    return status;
                }
            }
        );

        addDescriptorOperation
        (
            new RemoveAllPortletsOperation()
            {
                @Override
                public IStatus removeAllPortlets()
                {
                    return doRemoveAllPortlets();
                }
            }
        );
    }

    public boolean canAddNewPortlet( IDataModel model )
    {
        return model.getID().contains( "NewPortlet" );
    }

    public IStatus configurePortletXml( final String newPortletName )
    {
        final IFile descriptorFile = getDescriptorFile();

        final IStatus status = new DOMModelEditOperation( descriptorFile )
        {
            protected IStatus doExecute( IDOMDocument document )
            {
                final Element rootElement = document.getDocumentElement();

                final NodeList portletNodes = rootElement.getElementsByTagName( "portlet" );

                if( portletNodes.getLength() > 0 )
                {
                    final Element lastPortletElement = (Element) portletNodes.item( portletNodes.getLength() - 1 );
                    final Element portletName = NodeUtil.findChildElement( lastPortletElement, "portlet-name" );
                    portletName.replaceChild( document.createTextNode( newPortletName ), portletName.getFirstChild() );
                }

                return Status.OK_STATUS;
            }
        }.execute();

        return status;
    }

    public IStatus doAddNewPortlet( IDOMDocument document, IDataModel model )
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
            String validBuildValue = bundleValue.replaceAll( "\\/", "." ); //$NON-NLS-1$

            NodeUtil.appendChildElement( newPortletElement, "resource-bundle", validBuildValue ); //$NON-NLS-1$
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

    public IStatus doRemoveAllPortlets()
    {
        final String portletTagName = "portlet";

        DOMModelEditOperation domModelOperation = new DOMModelEditOperation( getDescriptorFile() )
        {
            protected IStatus doExecute( IDOMDocument document )
            {
                return removeAllElements( document, portletTagName );
            }
        };

        IStatus status = domModelOperation.execute();

        return status;
    }

    public String[] getAllPortletNames()
    {
        final List<String> allPortletNames = new ArrayList<String>();

        final IFile descriptorFile = getDescriptorFile();

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

    public IFile getDescriptorFile()
    {
        return super.getDescriptorFile( DESCRIPTOR_FILE );
    }

    protected String getPortletClassText( IDataModel model )
    {
        return model.getStringProperty( QUALIFIED_CLASS_NAME );
    }

}
