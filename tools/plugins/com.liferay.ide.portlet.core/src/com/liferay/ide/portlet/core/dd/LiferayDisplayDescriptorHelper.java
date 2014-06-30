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
@SuppressWarnings( "restriction" )
public class LiferayDisplayDescriptorHelper extends LiferayDescriptorHelper
                                            implements INewPortletClassDataModelProperties
{
    public static final String DESCRIPTOR_FILE = ILiferayConstants.LIFERAY_DISPLAY_XML_FILE;

    private static final String DESCRIPTOR_TEMPLATE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE display PUBLIC \"-//Liferay//DTD Display " //$NON-NLS-1$
        + "{0}//EN\" \"http://www.liferay.com/dtd/liferay-display_{1}.dtd\">\n\n<display>\n</display>"; //$NON-NLS-1$

    public LiferayDisplayDescriptorHelper()
    {
        super();
    }

    public LiferayDisplayDescriptorHelper( IProject project )
    {
        super( project );
    }

    @Override
    protected void addDescriptorOperations()
    {
        addDescriptorOperation
        (
            new AddNewPortletOperation()
            {
                @Override
                public IStatus addNewPortlet( final IDataModel model )
                {
                    IStatus status = Status.OK_STATUS;

                    final IFile descriptorFile = getDescriptorFile();

                    if( descriptorFile != null )
                    {
                        DOMModelEditOperation domModelOperation = new DOMModelEditOperation( descriptorFile )
                        {
                            protected void createDefaultFile()
                            {
                                createDefaultDescriptor( DESCRIPTOR_TEMPLATE, getDescriptorVersion() );
                            }

                            protected IStatus doExecute( IDOMDocument document )
                            {
                                return doAddNewPortlet( document, model );
                            }
                        };

                        status = domModelOperation.execute();
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
                    return removeAllPortlets();
                }
            }
        );

        addDescriptorOperation
        (
            new RemoveSampleElementsOperation()
            {
                @Override
                public IStatus removeSampleElements()
                {
                    return removeAllPortlets();
                }
            }
        );
    }

    public IStatus configureLiferayDisplayXml( final String newPortletName )
    {
        final IStatus status = new DOMModelEditOperation( getDescriptorFile() )
        {
            protected IStatus doExecute( IDOMDocument document )
            {
                final Element rootElement = document.getDocumentElement();

                final NodeList portletNodes = rootElement.getElementsByTagName( "category" );

                if( portletNodes.getLength() > 0 )
                {
                    final Element lastPortletElement = (Element) portletNodes.item( portletNodes.getLength() - 1 );
                    final Element portletName = NodeUtil.findChildElement( lastPortletElement, "portlet" );
                    portletName.setAttribute( "id", newPortletName );
                }

                return Status.OK_STATUS;
            }
        }.execute();

        return status;
    }

    protected IStatus doAddNewPortlet( IDOMDocument document, final IDataModel model )
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

    public String[] getAllPortletCategories()
    {
        final List<String> allPortletCategories = new ArrayList<String>();

        final IFile descriptorFile = getDescriptorFile();

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

    public IFile getDescriptorFile()
    {
        return super.getDescriptorFile( DESCRIPTOR_FILE );
    }

    protected IStatus removeAllPortlets()
    {
        final String categoryTagName = "category";

        DOMModelEditOperation domModelOperation = new DOMModelEditOperation( getDescriptorFile() )
        {
            protected IStatus doExecute( IDOMDocument document )
            {
                return removeAllElements( document, categoryTagName );
            }
        };

        IStatus status = domModelOperation.execute();

        return status;
    }

}
