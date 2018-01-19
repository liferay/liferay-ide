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

import static com.liferay.ide.ui.tests.UITestsUtils.containsProposal;
import static com.liferay.ide.ui.tests.UITestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.buildAndValidate;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.containHyperlink;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.findMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.findMarkerResolutionByClass;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getHyperLinksForElement;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getProposalsForElement;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.xml.search.ui.editor.LiferayCustomXmlViewerConfiguration;
import com.liferay.ide.xml.search.ui.markerResolutions.DecreaseInstanceScopeXmlValidationLevel;
import com.liferay.ide.xml.search.ui.markerResolutions.DecreaseProjectScopeXmlValidationLevel;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Kuo Zhang
 */
public class LiferayPortletXmlTests extends XmlSearchTestsBase
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

    public void validateHyperLinksForElement( String elementName, String expectedHyperlinkText ) throws Exception
    {
        descriptorFile = getDescriptorFile();
        IHyperlink[] hyperLinks = getHyperLinksForElement( descriptorFile, elementName );
        assertTrue( containHyperlink( hyperLinks, expectedHyperlinkText, true ) );
    }

    public void validateContentAssistForElement( String elementName, String expectedProposal ) throws Exception
    {
        descriptorFile = getDescriptorFile();

        final ICompletionProposal[] proposals = getProposalsForElement( descriptorFile, elementName );
        assertNotNull( proposals );

        assertTrue( containsProposal( proposals, expectedProposal, true ) );
    }

    public void validateQuickFixForElements( String elementName ) throws Exception
    {
        descriptorFile = getDescriptorFile();
        String elementContent = "quickFixTest";
        String markerMessageRegex = ".*" + elementContent + ".*";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );

        IMarker expectedMarker = findMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessageRegex, false );
        assertNotNull( findMarkerResolutionByClass( expectedMarker, DecreaseProjectScopeXmlValidationLevel.class ) );
        assertNotNull( findMarkerResolutionByClass( expectedMarker, DecreaseInstanceScopeXmlValidationLevel.class ) );
    }

    @Test
    public void testSourceViewerConfiguration() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IFile descriptorFile = getDescriptorFile();
        Object sourceViewerConfiguration =
            XmlSearchTestsUtils.getSourceViewerConfiguraionFromOpenedEditor( descriptorFile );

        assertEquals( true, sourceViewerConfiguration instanceof LiferayCustomXmlViewerConfiguration );
    }

    @Test
    public void testAssetRenderFactory() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "asset-renderer-factory";

        String expectedHyperlinkText = "Open 'AssetRendererFactoryImp - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "AssetRendererFactoryImp - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testAtomCollectionAdapter() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "atom-collection-adapter";

        String expectedHyperlinkText = "Open 'AtomCollectionAdapterImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "AtomCollectionAdapterImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testConfigurationActionClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "configuration-action-class";

        String expectedHyperlinkText = "Open 'ConfigurationActionImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "ConfigurationActionImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testControlPanelEntryClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "control-panel-entry-class";

        String expectedHyperlinkText = "Open 'ControlPanelEntryImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "ControlPanelEntryImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testCustomAttributesDisplay() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "custom-attributes-display";

        String expectedHyperlinkText = "Open 'CustomAttributesDisplayImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "CustomAttributesDisplayImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testDDMDisplay() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "ddm-display";

        String expectedHyperlinkText = "Open 'DDMDisplayImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "DDMDisplayImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testfooterPortletCss() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "footer-portlet-css";

        String expectedHyperlinkText = "Open '/Portlet-Xml-Test-portlet/docroot/css/main.css'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "/css/main.css";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testFooterPortletJavaScript() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "footer-portlet-javascript";

        String expectedHyperlinkText = "Open '/Portlet-Xml-Test-portlet/docroot/js/main.js'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "/js/main.js";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testFriendlyURLMapperClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "friendly-url-mapper-class";

        String expectedHyperlinkText = "Open 'FriendlyURLMapperImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "FriendlyURLMapperImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testHeaderPortletCss() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "header-portlet-css";

        String expectedHyperlinkText = "Open '/Portlet-Xml-Test-portlet/docroot/css/main.css'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "/css/main.css";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testHeaderPortletJavascript() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "header-portlet-javascript";

        String expectedHyperlinkText = "Open '/Portlet-Xml-Test-portlet/docroot/js/main.js'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "/js/main.js";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testIcon() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "icon";

        String expectedHyperlinkText = "Open '/Portlet-Xml-Test-portlet/docroot/icon.png'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "/icon.png";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testIndexerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "indexer-class";

        String expectedHyperlinkText = "Open 'IndexerImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "IndexerImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testPermissionPropagator() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "permission-propagator";

        String expectedHyperlinkText = "Open 'PermissionPropagatorImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "PermissionPropagatorImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testPollerProcessorClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "poller-processor-class";

        String expectedHyperlinkText = "Open 'PollerProcessorImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "PollerProcessorImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testPopMessageListenerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "pop-message-listener-class";

        String expectedHyperlinkText = "Open 'PopMessageListenerImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "PopMessageListenerImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testPortletDataHandlerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "portlet-data-handler-class";

        String expectedHyperlinkText = "Open 'PortletDataHandlerImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "PortletDataHandlerImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );
    }

    @Test
    public void testPortletLayoutListenerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "portlet-layout-listener-class";

        String expectedHyperlinkText = "Open 'PortletLayoutListenerImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "PortletLayoutListenerImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );
    }

    @Test
    public void testSchedulerEventListenerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "scheduler-event-listener-class";

        String expectedHyperlinkText = "Open 'MessageListenerImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "MessageListenerImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );
    }

    @Test
    public void testSocialActivityInterpreterClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "social-activity-interpreter-class";

        String expectedHyperlinkText = "Open 'SocialActivityInterpreterImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "SocialActivityInterpreterImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );
    }

    @Test
    public void testSocialRequestInterpreterClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "social-request-interpreter-class";

        String expectedHyperlinkText = "Open 'SocialRequestInterpreterImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "SocialRequestInterpreterImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testStagedModelDataHandlerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "staged-model-data-handler-class";

        String expectedHyperlinkText = "Open 'StagedModelDataHandlerImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "StagedModelDataHandlerImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testTemplateHandler() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "template-handler";

        String expectedHyperlinkText = "Open 'TemplateHandlerImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "TemplateHandlerImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testURLEncoderClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "url-encoder-class";

        String expectedHyperlinkText = "Open 'URLEncoderImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "URLEncoderImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testUserNotificationHandlerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "user-notification-handler-class";

        String expectedHyperlinkText = "Open 'UserNotificationHandlerImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "UserNotificationHandlerImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testWebdavStorageClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "webdav-storage-class";

        String expectedHyperlinkText = "Open 'WebDAVStorageImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "WebDAVStorageImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

    @Test
    public void testXmlRpcMethodClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "xml-rpc-method-class";

        String expectedHyperlinkText = "Open 'XmlrpcMethodImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String expectedProposal = "XmlrpcMethodImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, expectedProposal );

        validateQuickFixForElements( elementName );
    }

}
