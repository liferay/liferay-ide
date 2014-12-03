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
package com.liferay.ide.portlet.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.portlet.core.model.Param;
import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.PortletApp;
import com.liferay.ide.portlet.core.model.PortletInfo;
import com.liferay.ide.portlet.core.model.SecurityRoleRef;
import com.liferay.ide.portlet.core.model.Supports;
import com.liferay.ide.project.core.tests.XmlTestsBase;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.junit.Test;


/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
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

}
