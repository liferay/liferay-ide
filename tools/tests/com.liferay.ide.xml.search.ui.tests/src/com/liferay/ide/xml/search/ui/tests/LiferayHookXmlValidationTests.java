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
package com.liferay.ide.xml.search.ui.tests;

import static com.liferay.ide.ui.tests.UITestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.buildAndValidate;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.xml.search.ui.validators.LiferayBaseValidator;
import com.liferay.ide.xml.search.ui.validators.LiferayHookDescriptorValidator;

import java.text.MessageFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Li Lu
 */
public class LiferayHookXmlValidationTests extends XmlSearchTestsBase
{

    protected final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;
    private IFile descriptorFile;
    private static IProject project;

    protected IFile getDescriptorFile() throws Exception
    {
        return descriptorFile != null ? descriptorFile : LiferayCore.create(IWebProject.class, getProject() ).getDescriptorFile(
            ILiferayConstants.LIFERAY_HOOK_XML_FILE );
    }

    private IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "hooks", "Liferay-Hook-Xml-Test-hook" );
            deleteOtherProjects( project );
        }

        return project;
    }

    @Before
    public void cleanupMarkers() throws Exception
    {
        descriptorFile = getDescriptorFile();
        ZipFile projectFile = new ZipFile( getProjectZip( getBundleId(), "Liferay-Hook-Xml-Test-hook" ) );
        ZipEntry entry = projectFile.getEntry( "Liferay-Hook-Xml-Test-hook/docroot/WEB-INF/liferay-hook.xml" );

        descriptorFile.setContents( projectFile.getInputStream( entry ), IResource.FORCE, new NullProgressMonitor() );
        projectFile.close();
    }

    @AfterClass
    public static void deleteProject() throws Exception
    {
        try
        {
            project.close( null );
            project.delete( true, null );
        }
        catch( Exception e )
        {
        }
    }

    public void validateElementsIncorrectValue( String elementName, String elementValue, String markerMessage )
        throws Exception
    {
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );
    }

    public void validateElementcorrectValue( String elementName, String elementValue )
        throws Exception
    {
        String markerMessageRegex = ".*" + elementValue +".*";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        assertFalse( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessageRegex, false ) );
    }

    @Test
    public void testPortalProperties() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "portal-properties";
        String elementValue = null;
        String markerMessage = null;

        // portal-properties value doesn't end with ".properties"
        elementValue = "PortalPropertiesNotEndProperties";
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_PROPERTIES_NOT_END_WITH_PROPERTIES,
                new Object[] { elementValue } );

        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        // portal properties file doesn't exist
        elementValue = "PortalPropertiesNotExist.properties";
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        // portal properties file exists
        elementValue = "PortalPropertiesExist.properties";
        validateElementcorrectValue( elementName, elementValue );

        // portal properties file is null
        markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { "" } );
        validateElementsIncorrectValue( elementName, "", markerMessage );
    }

    @Test
    public void testLanguageProperties() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "language-properties";
        String elementValue = null;
        String markerMessage = null;

        // language-properties value doesn't end with ".properties"
        elementValue = "LanguagePropertiesNotEndProperties";
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_PROPERTIES_NOT_END_WITH_PROPERTIES,
                new Object[] { elementValue } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        // language properties file doesn't exist
        elementValue = "LanguagePropertiesNotExist.properties";
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        elementValue = "content/LanguagePropertiesNotExist.properties";
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        // language properties file exists
        elementValue = "LanguagePropertiesExist.properties";
        validateElementcorrectValue( elementName, elementValue );

        elementValue = "content/Language.properties";
        validateElementcorrectValue( elementName, elementValue );

        // language properties file with "*" doesn't exist
        elementValue = "LanguagePropertiesNotExist*.properties";
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        // language properties file with "*" exists
        elementValue = "LanguagePropertiesExist*.properties";
        validateElementcorrectValue( elementName, elementValue );

        // set to a "" value
        markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { "" } );
        validateElementsIncorrectValue( elementName, "", markerMessage );
    }

    @Test
    public void testCustomJspDir() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "custom-jsp-dir";

        String elementValue = "/custom_jspsNotExist";
        String markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        // path incorrect test
        elementValue = "/custom_jsps";
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        // set to correct jsp dir
        elementValue = "/WEB-INF/src";
        validateElementcorrectValue( elementName, elementValue );

        // set to null
        markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { "" } );
        validateElementsIncorrectValue( elementName, "", markerMessage );
    }

    @Test
    public void testServiceTypeAndServiceImpl() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        String elementName = "service-type";

        // type not exist
        String elementValue = "Foo";
        String markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        // type is not an interface
        elementValue = "com.liferay.ide.tests.Orphan";
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_SERVICE_TYPE_NOT_INTERFACE, new Object[] { elementValue } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        // validate type hierarchy
        elementValue = "com.liferay.ide.tests.InterfaceTest";
        markerMessage = LiferayHookDescriptorValidator.MESSAGE_SERVICE_TYPE_INVALID;
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        // set to ""
        markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { "" } );
        validateElementsIncorrectValue( elementName, "", markerMessage );

        // correct
        elementValue = "com.liferay.portal.service.AccountService";
        validateElementcorrectValue( elementName, elementValue );

        // service-impl test
        elementName = "service-impl";
        elementValue = "Foo";

        markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        elementValue = "com.liferay.ide.tests.Orphan";
        markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_SERVICE_IMPL_TYPE_INCORRECT, new Object[] {
                elementValue, "com.liferay.portal.service.AccountServiceWrapper" } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        elementValue = "com.liferay.ide.tests.AccountServiceWrapperImpl";
        validateElementcorrectValue( elementName, elementValue );

        markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { "" } );
        validateElementsIncorrectValue( elementName, "", markerMessage );
    }

    @Test
    public void testIndexerClassName() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "indexer-class-name";

        String elementValue = "Foo";
        String markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        elementValue = "com.liferay.ide.tests.Orphan";
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.model.BaseModel" } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        elementValue = "com.liferay.ide.tests.Indexer";
        validateElementcorrectValue( elementName, elementValue );

        markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { "" } );
        validateElementsIncorrectValue( elementName, "", markerMessage );
    }

    @Test
    public void testIndexerPostProcesserImpl() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "indexer-post-processor-impl";

        String elementValue = "Foo";
        String markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        elementValue = "com.liferay.ide.tests.Orphan";
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.search.IndexerPostProcessor" } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        elementValue = "com.liferay.ide.tests.IndexerPostProcessorImpl";
        validateElementcorrectValue( elementName, elementValue );

        markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { "" } );
        validateElementsIncorrectValue( elementName, "", markerMessage );
    }

    @Test
    public void testServletFilterImpl() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "servlet-filter-impl";

        String elementValue = "Foo";
        String markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        elementValue = "com.liferay.ide.tests.Orphan";
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "javax.servlet.Filter" } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        elementValue = "com.liferay.ide.tests.ServletFilterImpl";
        validateElementcorrectValue( elementName, elementValue );

        markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { "" } );
        validateElementsIncorrectValue( elementName, "", markerMessage );
    }

    @Test
    public void testStrutsActionImpl() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "struts-action-impl";

        String elementValue = "Foo";
        String markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        elementValue = "com.liferay.ide.tests.Orphan";
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.struts.StrutsAction, com.liferay.portal.kernel.struts.StrutsPortletAction" } );
        validateElementsIncorrectValue( elementName, elementValue, markerMessage );

        elementValue = "com.liferay.ide.tests.StrutsActionImpl";
        validateElementcorrectValue( elementName, elementValue );

        markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { "" } );
        validateElementsIncorrectValue( elementName, "", markerMessage );
    }

}
