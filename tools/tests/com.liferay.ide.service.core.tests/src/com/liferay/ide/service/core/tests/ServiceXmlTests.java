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
package com.liferay.ide.service.core.tests;

import static org.junit.Assert.assertEquals;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.tests.XmlTestsBase;
import com.liferay.ide.service.core.model.Entity;
import com.liferay.ide.service.core.model.Relationship;
import com.liferay.ide.service.core.model.ServiceBuilder;
import com.liferay.ide.service.core.model.ServiceBuilder6xx;
import com.liferay.ide.service.core.operation.ServiceBuilderDescriptorHelper;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.junit.Test;


/**
 * @author Kuo Zhang
 * @author Gregory Amerson
 */
public class ServiceXmlTests extends XmlTestsBase
{
    // This test needs to set the "liferay.bundles.dir" in the configuration.

    @Test
    public void testAddDefaultColumns() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IProject project =
            importProject( "portlets", "com.liferay.ide.service.core.tests", "Add-Default-Columns-Test-portlet" );

        final ServiceBuilderDescriptorHelper descriptorHelper = new ServiceBuilderDescriptorHelper( project );

        assertEquals( Status.OK_STATUS, descriptorHelper.addEntity( "AddDefaultColumns" ) );
        assertEquals( Status.OK_STATUS, descriptorHelper.addDefaultColumns( "AddDefaultColumns" ) );

        final IFile serviceXmlFile = descriptorHelper.getDescriptorFile();

        final String serviceXmlContent = CoreUtil.readStreamToString( serviceXmlFile.getContents() );

        final String expectedServiceXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream(
                "files/service-sample-6.2.0-add-default-columns.xml" ) );

        assertEquals(
            expectedServiceXmlContent.replaceAll( "\\s+", StringPool.SPACE ),
            serviceXmlContent.replaceAll( "\\s+", StringPool.SPACE) );
    }

    @Test
    public void testAddSampleEntity() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IProject project =
            importProject( "portlets","com.liferay.ide.service.core.tests", "Add-Sample-Entity-Test-portlet" );

        final ServiceBuilderDescriptorHelper descriptorHelper = new ServiceBuilderDescriptorHelper( project );

        assertEquals( Status.OK_STATUS, descriptorHelper.addDefaultEntity() );

        final IFile serviceXmlFile = descriptorHelper.getDescriptorFile();

        final String serviceXmlContent = CoreUtil.readStreamToString( serviceXmlFile.getContents() );

        final String expectedServiceXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream(
                "files/service-sample-6.2.0-add-sample-entity.xml" ) );

        assertEquals(
            expectedServiceXmlContent.replaceAll( "\\s+", StringPool.SPACE ),
            serviceXmlContent.replaceAll( "\\s+", StringPool.SPACE ) );
    }

    @Test
    public void testEntityReferenceService() throws Exception
    {
        ServiceBuilder sb =
            ServiceBuilder6xx.TYPE.instantiate( new RootXmlResource( new XmlResourceStore(
                this.getClass().getResourceAsStream( "files/entity-reference-test.xml" ) ) ) );

        Entity foo = sb.getEntities().get( 0 );
        Entity bar = sb.getEntities().get( 1 );

        ElementList<Relationship> relationships = sb.getRelationships();

        assertEquals( 1, relationships.size() );

        Entity to = sb.getRelationships().get( 0 ).getToEntity().target();
        Entity from = sb.getRelationships().get( 0 ).getFromEntity().target();

        assertEquals( to, foo );
        assertEquals( from, bar );
    }

}
