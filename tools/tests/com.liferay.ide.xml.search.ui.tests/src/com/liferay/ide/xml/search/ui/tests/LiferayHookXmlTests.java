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

import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.buildAndValidate;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkNoMarker;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;
import static org.junit.Assert.assertEquals;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.xml.search.ui.validators.LiferayHookDescriptorValidator;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.Test;

/**
 * @author Kuo Zhang
 */
public class LiferayHookXmlTests extends XmlSearchTestsBase
{
    private final static String MARKER_TYPE = LiferayHookDescriptorValidator.MARKER_TYPE;
    private IFile descriptor;
    private IProject project;

    private IFile getDescriptorFile() throws Exception
    {
        return descriptor != null ? descriptor : LiferayCore.create( getProject() ).getDescriptorFile( ILiferayConstants.LIFERAY_HOOK_XML_FILE );
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

    @Test
    public void testIndexerClassName() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        testIndexerClassNameValidation();
        testIndexerClassNameContentAssist();
        testIndexerClassNameHyperlink();
    }

    // TODO
    protected void testIndexerClassNameContentAssist()
    {
    }


    // TODO
    protected void testIndexerClassNameHyperlink()
    {
    }

    protected void testIndexerClassNameValidation() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "indexer-class-name";

        String elementValue = "Foo";
        setElementContent( descriptorFile, elementName, elementValue );

        String markerMessage = MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue } ) + ".*";
        buildAndValidate( descriptorFile );


        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, false ) );

        elementValue = "com.liferay.ide.tests.Indexer";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testIndexerPostProcesserImpl() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        testIndexerPostProcesserImplValidation();
        testIndexerPostProcesserImplContentAssist();
        testIndexerPostProcesserImplHyperlink();
    }

    // TODO
    private void testIndexerPostProcesserImplContentAssist()
    {
    }

    // TODO
    private void testIndexerPostProcesserImplHyperlink()
    {
    }

    public void testIndexerPostProcesserImplValidation() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "indexer-post-processor-impl";

        String elementValue = "Foo";
        setElementContent( descriptorFile, elementName, elementValue );
        String markerMessage = MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue } ) + ".*";
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, false ) );

        elementValue = "com.liferay.ide.tests.IndexerPostProcessorImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testLanguageProperties() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        testLanguagePropertiesValidation();
        testLanguagePropertiesContentAssist();
        testLanguagePropertiesHyperlink();
    }

    // TODO
    protected void testLanguagePropertiesContentAssist()
    {
    }

    // TODO
    protected void testLanguagePropertiesHyperlink()
    {
    }

    public void testLanguagePropertiesValidation() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "language-properties";
        String elementValue = null;
        String markerMessage = null;

        // language-properties value doesn't end with ".properties"
        elementValue = "LanguagePropertiesNotEndProperties";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_PROPERTIES_NOT_END_WITH_PROPERTIES, new Object[] { elementValue } );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // language-properties value ends with ".properties"
        elementValue = "LanguagePropertiesEndWithProperties.properties";
        setElementContent( descriptorFile, elementName, elementValue );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // language properties file doesn't exist
        elementValue = "LanguagePropertiesNotExist.properties";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // language properties file exists
        elementValue = "LanguagePropertiesExist.properties";
        setElementContent( descriptorFile, elementName, elementValue );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // language properties file doesn't exist
        elementValue = "content/LanguagePropertiesNotExist.properties";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage,true ) );

        // language properties file exists
        elementValue = "content/LanguagePropertiesExist.properties";
        setElementContent( descriptorFile, elementName, elementValue );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // language properties file with "*" doesn't exist
        elementValue = "LanguagePropertiesNotExist*.properties";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // language properties file with "*" exists
        elementValue = "LanguagePropertiesExist*.properties";
        setElementContent( descriptorFile, elementName, elementValue );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // set to a correct value
        elementValue = "content/Language.properties";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
    }

    @Test
    public void testPortalProperties() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        testPortalPropertiesValidation();
        testPortalPropertiesContentAssist();
        testPortalPropertiesQuickFix();
    }

    // TODO
    private void testPortalPropertiesContentAssist()
    {
    }

    // TODO
    private void testPortalPropertiesQuickFix()
    {
    }

    public void testPortalPropertiesValidation() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portal-properties";
        String elementValue = null;
        String markerMessage = null;

        // portal-properties value donesn't end with ".properties"
        elementValue = "PortalPropertiesNotEndProperties";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_PROPERTIES_NOT_END_WITH_PROPERTIES, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // portal-properties value ends with ".properties"
        elementValue = "PortalPropertiesEndWithProperties.properties";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // portal properties file doesn't exist
        elementValue = "PortalPropertiesNotExist.properties";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // portal properties file exists
        elementValue = "PortalPropertiesExist.properties";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // portal properties file doesn't exist
        elementValue = "content/PortalPropertiesNotExist.properties";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage , true ) );

        // portal properties file exists
        elementValue = "content/PortalPropertiesExist.properties";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // set to a correct value
        elementValue = "content/portal.properties";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
    }

    @Test
    public void testServletFilterImpl() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        testServletFilterImplValidation();
        testServletFilterImplContentAssist();
        testServletFilterImplHyperlink();
    }

    // TODO
    private void testServletFilterImplContentAssist()
    {
    }

    // TODO
    private void testServletFilterImplHyperlink()
    {
    }

    public void testServletFilterImplValidation() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "servlet-filter-impl";

        String elementValue = "Foo";
        setElementContent( descriptorFile, elementName, elementValue );
        String markerMessage = MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue } ) + ".*" ;
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, false ) );

        elementValue = "com.liferay.ide.tests.ServletFilterImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    // TODO
    public void testServiceTypeAndServiceImpl() throws Exception
    {
        // test service-type and service-impl together, because validation of service-impl relies on service-type

        // Service type
        // Foo "Type not found"
        // com.liferay.ide.tests.OrphanClass "Not a interface"
        // com.liferay.ide.tests.OrphanInterface "Should match *Service"
        // com.liferay.ide.tests.FooService, correct

        // Service Imple
        // Foo "Type not found"
        // OrphanClass "Type Hierarchy incorrect"
        // FooServiceWrapperImpl correct
    }

    // TODO
    protected void testServiceTypeAndServiceImplContentAssist()
    {
    }

    // TODO
    protected void testServiceTypeAndServiceImplHyperlink()
    {
    }

    // TODO
    protected void testServiceTypeAndServiceImplValidation()
    {
    }

    @Test
    public void testStrutsActionImpl() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        testStrutsActionImplValidation();
        testStrutsActionImplContentAssist();
        testStrutsActionImplHyperlink();
    }

    // TODO
    protected void testStrutsActionImplContentAssist()
    {
    }

    // TODO
    protected void testStrutsActionImplHyperlink()
    {
    }

    protected void testStrutsActionImplValidation() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "struts-action-impl";

        String elementValue = "Foo";
        setElementContent( descriptorFile, elementName, elementValue );
        String markerMessage = MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue } ) + ".*";
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, false ) );

        elementValue = "com.liferay.ide.tests.StrutsActionImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

}