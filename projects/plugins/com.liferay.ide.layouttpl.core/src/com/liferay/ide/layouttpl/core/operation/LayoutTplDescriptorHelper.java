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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.layouttpl.core.operation;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.DescriptorHelper;
import com.liferay.ide.core.util.NodeUtil;

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
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class LayoutTplDescriptorHelper extends DescriptorHelper implements INewLayoutTplDataModelProperties
{

    public LayoutTplDescriptorHelper( IProject project )
    {
        super( project );
    }

    public IStatus addNewLayoutTemplate( final IDataModel dm )
    {
        final IFile descriptorFile = getDescriptorFile( ILiferayConstants.LIFERAY_LAYOUTTPL_XML_FILE );
        
        final DOMModelOperation operation = new DOMModelEditOperation( descriptorFile )
        {
            protected IStatus doExecute( IDOMDocument document )
            {
                return doAddLayoutTemplate( document, dm );
            }
        };

        IStatus status = operation.execute();

        if( !status.isOK() )
        {
            return status;
        }

        return status;
    }

    public IStatus doAddLayoutTemplate( IDOMDocument document, IDataModel model )
    {
        // <layout-templates> element
        Element docRoot = document.getDocumentElement();

        Element layoutTemplateElement = document.createElement( "layout-template" );
        layoutTemplateElement.setAttribute( "id", model.getStringProperty( LAYOUT_TEMPLATE_ID ) );
        layoutTemplateElement.setAttribute( "name", model.getStringProperty( LAYOUT_TEMPLATE_NAME ) );

        // find the <custom> element and if it doesn't exist create it
        Node customElement = NodeUtil.getFirstNamedChildNode( docRoot, "custom" );

        if( customElement == null )
        {
            // if we are going to create a new <custom> it must be after the <standard>
            Node standardElement = NodeUtil.getFirstNamedChildNode( docRoot, "standard" );

            customElement = document.createElement( "custom" );
            docRoot.insertBefore( customElement, standardElement );
        }

        customElement.appendChild( layoutTemplateElement );

        // now that we have the new <layout-template> element added to custom element, add the child nodes to layout
        String templatePath = model.getStringProperty( LAYOUT_TEMPLATE_FILE );
        String wapTemplatePath = model.getStringProperty( LAYOUT_WAP_TEMPLATE_FILE );
        String thumbnailPath = model.getStringProperty( LAYOUT_THUMBNAIL_FILE );

        appendChildElement( layoutTemplateElement, "template-path", templatePath );
        appendChildElement( layoutTemplateElement, "wap-template-path", wapTemplatePath );
        appendChildElement( layoutTemplateElement, "thumbnail-path", thumbnailPath );

        // format the new node added to the model;
        FormatProcessorXML processor = new FormatProcessorXML();

        processor.formatNode( customElement );

        return Status.OK_STATUS;
    }

    public boolean hasTemplateId( final String templateId )
    {
        if( CoreUtil.isNullOrEmpty( templateId ) )
        {
            return false;
        }

        final boolean[] retval = new boolean[1];

        DOMModelOperation operation =
            new DOMModelReadOperation( getDescriptorFile( ILiferayConstants.LIFERAY_LAYOUTTPL_XML_FILE ) )
            {
                @Override
                protected IStatus doExecute( IDOMDocument document )
                {
                    NodeList layoutTemplates = document.getElementsByTagName( "layout-template" );

                    if( layoutTemplates != null && layoutTemplates.getLength() > 0 )
                    {
                        for( int i = 0; i < layoutTemplates.getLength(); i++ )
                        {
                            Element layoutTemplate = (Element) layoutTemplates.item( i );

                            if( templateId.equals( layoutTemplate.getAttribute( "id" ) ) )
                            {
                                retval[0] = true;
                                break;
                            }
                        }
                    }

                    return Status.OK_STATUS;
                }
            };

        operation.execute();

        return retval[0];
    }

}
