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
package com.liferay.ide.portlet.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.descriptor.PortletDescriptorValidator;
import com.liferay.ide.portlet.core.model.Param;
import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.PortletApp;
import com.liferay.ide.portlet.core.model.PortletInfo;
import com.liferay.ide.portlet.core.model.SecurityRoleRef;
import com.liferay.ide.portlet.core.model.Supports;
import com.liferay.ide.project.core.tests.XmlTestsBase;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class PortletXmlTests extends XmlTestsBase
{

    private static final String PORTLET_XML = "files/portlet.xml";

    @Test
    public void portletXmlRead() throws Exception
    {
        final PortletApp portletApp = portletApp( PORTLET_XML );

        assertNotNull( portletApp );

        final ElementList<Portlet> portlets = portletApp.getPortlets();

        assertNotNull( portlets );

        assertEquals( 1, portlets.size() );

        final Portlet portlet = portlets.get( 0 );

        assertNotNull( portlet );

        assertEquals( "1", portlet.getPortletName().content() );

        assertEquals( "Sample JSP", portlet.getDisplayName().content() );

        assertEquals( "com.liferay.samplejsp.portlet.JSPPortlet", portlet.getPortletClass().text() );

        final Param param = portlet.getInitParams().get( 0 );

        assertNotNull( param );

        assertEquals( "view-jsp", param.getName().content() );

        assertEquals( "/view.jsp", param.getValue().content() );

        assertEquals( new Integer( 0 ), portlet.getExpirationCache().content() );

        final Supports supports = portlet.getSupports();

        assertNotNull( supports );

        assertEquals( "text/html", supports.getMimeType().content() );

        final PortletInfo info = portlet.getPortletInfo();

        assertEquals( "Sample JSP", info.getTitle().content() );

        assertEquals( "Sample JSP", info.getShortTitle().content() );

        assertEquals( "Sample JSP", info.getKeywords().content() );

        final ElementList<SecurityRoleRef> roles = portlet.getSecurityRoleRefs();

        assertEquals( 4, roles.size() );

        final SecurityRoleRef role = roles.get( 1 );

        assertNotNull( role );

        assertEquals( "guest", role.getRoleName().content() );
    }

    private PortletApp portletApp( String portletXml ) throws ResourceStoreException
    {
        return PortletApp.TYPE.instantiate( new RootXmlResource( new XmlResourceStore(
            getClass().getResourceAsStream( portletXml )) ) );
    }

    private void setPropertiesValue( IFile descriptorFile, String elementName, String value ) throws Exception
    {
        final IDOMModel domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit( descriptorFile );
        final IDOMDocument document = domModel.getDocument();
        final NodeList elements = document.getElementsByTagName( elementName );

        assertEquals( true, elements.getLength() > 0 );

        final Element element = (Element) elements.item( 0 );

        final NodeList childNodes = element.getChildNodes();

        for( int i = 0; i < childNodes.getLength(); i++ )
        {
            element.removeChild( childNodes.item( i ) );
        }

        element.appendChild( document.createTextNode( value ) );

        domModel.save();
        domModel.releaseFromEdit();

        descriptorFile.refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );
    }

    @Test
    public void testResourceBundleElementValidation() throws Exception
    {
        final IProject project =
            importProject( "portlets", "com.liferay.ide.portlet.core.tests", "Portlet-Properties-Validation-Test-portlet" );
        final IFile descriptorFile = CoreUtil.getDescriptorFile( project, ILiferayConstants.PORTLET_XML_FILE );
        final String markerType = PortletDescriptorValidator.MARKER_TYPE;

        final String elementName = "resource-bundle";
        String elementValue = null;
        String markerMessage = null;

        // resource-bundle value ends with ".properties"
        elementValue = "ResourceBundleEndWithProperties.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            PortletDescriptorValidator.MESSAGE_RESOURCE_BUNDLE_NOT_END_PROPERTIES, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) ); 

        // resource-bundle doesn't end with ".properties"
        elementValue = "ResourceBundleNotEndWithProperties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );

        // resource-bundle values contains "/"
        elementValue = "ResourceBundle/WithSlash";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            PortletDescriptorValidator.MESSAGE_RESOURCE_BUNDLE_PATH_NOT_CONTAIN_SEPARATOR, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) ); 

        // resource-bundle values doesn't contain "/"
        elementValue = "ResourceBundleWithoutSlash";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );

        // resource bundle file doesn't exist
        elementValue = "ResourceBundleNotExist";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            PortletDescriptorValidator.MESSAGE_RESOURCE_BUNDLE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) ); 

        // resource bundle file exists
        elementValue = "ResourceBundleExist";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );

        // resource bundle file doesn't exist
        elementValue = "content.ResourceBundleNotExist";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            PortletDescriptorValidator.MESSAGE_RESOURCE_BUNDLE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) ); 

        // resource bundle file exists
        elementValue = "ResourceBundleExist";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );
    }

}
