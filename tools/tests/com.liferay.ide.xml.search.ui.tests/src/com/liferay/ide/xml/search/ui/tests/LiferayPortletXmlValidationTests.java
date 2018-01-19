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
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkNoMarker;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.xml.search.ui.validators.LiferayBaseValidator;
import com.liferay.ide.xml.search.ui.validators.LiferayPortletDescriptorValidator;

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
public class LiferayPortletXmlValidationTests extends XmlSearchTestsBase
{

    protected final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;
    private IFile descriptorFile;
    private static IProject project;

    protected IFile getDescriptorFile() throws Exception
    {

        return descriptorFile != null ? descriptorFile : LiferayCore.create(IWebProject.class, getProject() ).getDescriptorFile(
            ILiferayConstants.LIFERAY_PORTLET_XML_FILE );
    }

    private IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "portlets", "Portlet-Xml-Test-portlet" );
            deleteOtherProjects( project );
        }

        return project;
    }

    public void validateElementResourceNotFound( String elementName, String elementValue ) throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );
    }

    public void validateElementTypeNotFound( String elementName, String elementValue ) throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );

        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );
    }

    public void validateElementTypeHierarchyInocorrect( String elementName, String extendType ) throws Exception
    {
        String elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                extendType } );

        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );
    }

    public void validateElementReferenceNotFound( String elementName, String elementValue ) throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_REFERENCE_NOT_FOUND, new Object[] { elementValue,
                "portlet.xml" } );

        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );
    }

    public void validateElementCorrectValue( String elementName, String correctValue ) throws Exception
    {
        // IFile descriptorFile = getDescriptorFile();
        setElementContent( descriptorFile, elementName, correctValue );

        buildAndValidate( descriptorFile );

        if( !checkNoMarker( descriptorFile, MARKER_TYPE ) )
        {
            buildAndValidate( descriptorFile );
        }

        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Before
    public void cleanupMarkers() throws Exception
    {
        descriptorFile = getDescriptorFile();
        ZipFile projectFile = new ZipFile( getProjectZip( getBundleId(), "Portlet-Xml-Test-portlet" ) );
        ZipEntry entry = projectFile.getEntry( "Portlet-Xml-Test-portlet/docroot/WEB-INF/liferay-portlet.xml" );

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

    @Test
    public void testAssetRenderFactory() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "asset-renderer-factory";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portlet.asset.model.AssetRendererFactory" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.AssetRendererFactoryImp" );
    }

    @Test
    public void testAtomCollectionAdapter() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "atom-collection-adapter";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portal.kernel.atom.AtomCollectionAdapter" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.AtomCollectionAdapterImpl" );
    }

    @Test
    public void testConfigurationActionClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "configuration-action-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portal.kernel.portlet.ConfigurationAction" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.ConfigurationActionImpl" );
    }

    @Test
    public void testControlPanelEntryClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "control-panel-entry-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portlet.ControlPanelEntry" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.ControlPanelEntryImpl" );
    }

    @Test
    public void testControlPanelEntryWeight() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "control-panel-entry-weight";
        String elementValue = "control-panel-entry-weight";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_ENTRY_WEIGHT_SYNTAX_INVALID, new Object[] { elementValue } );

        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "1.5";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testCustomAttributesDisplay() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "custom-attributes-display";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect(
            elementName, "com.liferay.portlet.expando.model.CustomAttributesDisplay" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.CustomAttributesDisplayImpl" );
    }

    @Test
    public void testDDMDisplay() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "ddm-display";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portlet.dynamicdatamapping.util.DDMDisplay" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.DDMDisplayImpl" );
    }

    @Test
    public void testfooterPortletCss() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "footer-portlet-css";

        validateElementResourceNotFound( elementName, "foo" );
        validateElementResourceNotFound( elementName, "" );
        validateElementCorrectValue( elementName, "/css/main.css" );
    }

    @Test
    public void testFooterPortletJavaScript() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "footer-portlet-javascript";

        validateElementResourceNotFound( elementName, "foo" );
        validateElementResourceNotFound( elementName, "" );
        validateElementCorrectValue( elementName, "/js/main.js" );
    }

    @Test
    public void testFriendlyURLMapperClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "friendly-url-mapper-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portal.kernel.portlet.FriendlyURLMapper" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.FriendlyURLMapperImpl" );
    }

    @Test
    public void testHeaderPortletCss() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "header-portlet-css";

        validateElementResourceNotFound( elementName, "foo" );
        validateElementResourceNotFound( elementName, "" );
        validateElementCorrectValue( elementName, "/css/main.css" );
    }

    @Test
    public void testHeaderPortletJavascript() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "header-portlet-javascript";

        validateElementResourceNotFound( elementName, "foo" );
        validateElementResourceNotFound( elementName, "" );
        validateElementCorrectValue( elementName, "/js/main.js" );
    }

    @Test
    public void testIcon() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "icon";

        validateElementResourceNotFound( elementName, "foo" );
        validateElementResourceNotFound( elementName, "" );
        validateElementCorrectValue( elementName, "/icon.png" );
    }

    @Test
    public void testIndexerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "indexer-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portal.kernel.search.Indexer" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.IndexerImpl" );
    }

    @Test
    public void testPermissionPropagator() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "permission-propagator";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect(
            elementName, "com.liferay.portal.security.permission.PermissionPropagator" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.PermissionPropagatorImpl" );
    }

    @Test
    public void testPollerProcessorClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "poller-processor-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portal.kernel.poller.PollerProcessor" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.PollerProcessorImpl" );
    }

    @Test
    public void testPopMessageListenerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "pop-message-listener-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portal.kernel.pop.MessageListener" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.PopMessageListenerImpl" );
    }

    @Test
    public void testPortletDataHandlerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "portlet-data-handler-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portal.kernel.lar.PortletDataHandler" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.PortletDataHandlerImpl" );
    }

    @Test
    public void testPortletLayoutListenerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "portlet-layout-listener-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portal.kernel.portlet.PortletLayoutListener" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.PortletLayoutListenerImpl" );
    }

    @Test
    public void testPortletName() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "portlet-name";

        validateElementReferenceNotFound( elementName, "foo" );
        validateElementReferenceNotFound( elementName, "" );
        validateElementCorrectValue( elementName, "Portlet-Xml-Test" );
    }

    @Test
    public void testSchedulerEventListenerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "scheduler-event-listener-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portal.kernel.messaging.MessageListener" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.MessageListenerImpl" );
    }

    @Test
    public void testSocialActivityInterpreterClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "social-activity-interpreter-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect(
            elementName, "com.liferay.portlet.social.model.SocialActivityInterpreter" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.SocialActivityInterpreterImpl" );
    }

    @Test
    public void testSocialRequestInterpreterClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "social-request-interpreter-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect(
            elementName, "com.liferay.portlet.social.model.SocialRequestInterpreter" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.SocialRequestInterpreterImpl" );
    }

    @Test
    public void testStagedModelDataHandlerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "staged-model-data-handler-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portal.kernel.lar.StagedModelDataHandler" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.StagedModelDataHandlerImpl" );
    }

    @Test
    public void testTemplateHandler() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "template-handler";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portal.kernel.template.TemplateHandler" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.TemplateHandlerImpl" );
    }

    @Test
    public void testURLEncoderClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "url-encoder-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portal.kernel.servlet.URLEncoder" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.URLEncoderImpl" );
    }

    @Test
    public void testUserNotificationHandlerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "user-notification-handler-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect(
            elementName, "com.liferay.portal.kernel.notifications.UserNotificationHandler" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.UserNotificationHandlerImpl" );
    }

    @Test
    public void testWebdavStorageClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "webdav-storage-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portal.kernel.webdav.WebDAVStorage" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.WebDAVStorageImpl" );
    }

    @Test
    public void testXmlRpcMethodClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "xml-rpc-method-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "com.liferay.portal.kernel.xmlrpc.Method" );
        validateElementCorrectValue( elementName, "com.liferay.ide.tests.XmlrpcMethodImpl" );
    }

}
