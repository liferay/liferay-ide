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

package com.liferay.ide.portlet.jsf.core.operation;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.osgi.framework.Version;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */

@SuppressWarnings("restriction")
public class JSFPortletDescriptorHelper extends PortletDescriptorHelper
    implements INewJSFPortletClassDataModelProperties
{

    public JSFPortletDescriptorHelper( IProject project )
    {
        super( project );
    }

    @Override
    public IStatus addNewPortlet( final IDataModel model )
    {
        IStatus status = super.addNewPortlet( model );

        if( !status.isOK() )
        {
            return status;
        }

        ILiferayProject liferayProject = LiferayCore.create( this.project );

        if( liferayProject != null && liferayProject.getPortalVersion() != null )
        {
            final Version runtimeVersion = new Version( liferayProject.getPortalVersion() );

            // Runtime version should be equal or greater than 6.2.
            if( CoreUtil.compareVersions( runtimeVersion, ILiferayConstants.V620 ) >= 0 )
            {
                final IFile descriptorFile = getDescriptorFile( ILiferayConstants.LIFERAY_PORTLET_XML_FILE );

                if( descriptorFile != null )
                {
                    DOMModelOperation op = new DOMModelEditOperation( descriptorFile )
                    {

                        @Override
                        protected void createDefaultFile()
                        {
                            // Getting document from super( descriptorFile );
                        }

                        @Override
                        protected IStatus doExecute( IDOMDocument document )
                        {
                            return updateJSFLiferayPortletXML( document );
                        }
                    };

                    IStatus opStatus = op.execute();

                    if( !opStatus.isOK() )
                    {
                        return opStatus;
                    }
                }
            }
        }

        return status;
    }

    @Override
    protected String getPortletClassText( IDataModel model )
    {
        return model.getStringProperty( JSF_PORTLET_CLASS );
    }

    private IStatus updateJSFLiferayPortletXML( IDOMDocument document )
    {
        Element rootElement = document.getDocumentElement();

        NodeList portletNodes = rootElement.getElementsByTagName( "portlet" );

        if( portletNodes.getLength() > 1 )
        {
            FormatProcessorXML processor = new FormatProcessorXML();
            Element lastPortletElement = (Element) portletNodes.item( portletNodes.getLength() - 1 );
            Node headerPortletClassElement = lastPortletElement.getElementsByTagName( "header-portlet-css" ).item( 0 );
            NodeUtil.insertChildElement(
                lastPortletElement, headerPortletClassElement, "requires-namespaced-parameters", "false" );
            processor.formatNode( lastPortletElement );
        }

        return Status.OK_STATUS;
    }
}
