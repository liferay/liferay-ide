
package com.liferay.ide.xml.search.ui.tests;

import static com.liferay.ide.ui.tests.UITestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.buildAndValidate;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkNoMarker;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.xml.search.ui.validators.LiferayBaseValidator;
import com.liferay.ide.xml.search.ui.validators.LiferayPortletDescriptorValidator;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.Test;

/**
 * @author Li Lu
 */
public class LiferayPortletXmlValidationTests extends XmlSearchTestsBase
{

    protected final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;
    private IFile descriptorFile;
    private IProject project;

    protected IFile getDescriptorFile() throws Exception
    {
        return descriptorFile != null ? descriptorFile : LiferayCore.create( getProject() ).getDescriptorFile(
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

    @Test
    public void testAssetRenderFactory() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "asset-renderer-factory";
        String elementValue = "asset-renderer-factory";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portlet.asset.model.AssetRendererFactory" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.AssetRendererFactoryImp";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );

    }

    public void testAtomCollectionAdapter() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "atom-collection-adapter";
        String elementValue = "atom-collection-adapter";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.atom.AtomCollectionAdapter" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.AtomCollectionAdapterImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testConfigurationActionClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "configuration-action-class";
        String elementValue = "configuration-action-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.portlet.ConfigurationAction" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.ConfigurationActionImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testControlPanelEntryClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "control-panel-entry-class";
        String elementValue = "control-panel-entry-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portlet.ControlPanelEntry" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.ControlPanelEntryImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );

    }

    @Test
    public void testControlPanelEntryWeight() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

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
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "custom-attributes-display";
        String elementValue = "custom-attributes-display";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portlet.expando.model.CustomAttributesDisplay" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.CustomAttributesDisplayImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testFriendlyURLMapperClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "friendly-url-mapper-class";
        String elementValue = "friendly-url-mapper-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.portlet.FriendlyURLMapper" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.FriendlyURLMapperImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );

    }

    @Test
    public void testDDMDisplay() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "ddm-display";
        String elementValue = "ddm-display";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portlet.dynamicdatamapping.util.DDMDisplay" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.DDMDisplayImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testIndexerClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "indexer-class";
        String elementValue = "indexer-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.search.Indexer" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.IndexerImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testPermissionPropagator() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "permission-propagator";
        String elementValue = "permission-propagator";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.security.permission.PermissionPropagator" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.PermissionPropagatorImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testPollerProcessorClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "poller-processor-class";
        String elementValue = "poller-processor-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.poller.PollerProcessor" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.PollerProcessorImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testPopMessageListenerClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "pop-message-listener-class";
        String elementValue = "pop-message-listener-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.pop.MessageListener" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.PopMessageListenerImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testPortletDataHandlerClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet-data-handler-class";
        String elementValue = "portlet-data-handler-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.lar.PortletDataHandler" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.PortletDataHandlerImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testPortletLayoutListenerClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet-layout-listener-class";
        String elementValue = "portlet-layout-listener-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.portlet.PortletLayoutListener" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.PortletLayoutListenerImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testSchedulerEventListenerClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "scheduler-event-listener-class";
        String elementValue = "scheduler-event-listener-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.messaging.MessageListener" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.MessageListenerImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testSocialActivityInterpreterClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "social-activity-interpreter-class";
        String elementValue = "portlet-layout-listener-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portlet.social.model.SocialActivityInterpreter" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.SocialActivityInterpreterImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testSocialRequestInterpreterClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "social-request-interpreter-class";
        String elementValue = "social-request-interpreter-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portlet.social.model.SocialRequestInterpreter" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.SocialRequestInterpreterImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testStagedModelDataHandlerClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "staged-model-data-handler-class";
        String elementValue = "staged-model-data-handler-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.lar.StagedModelDataHandler" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.StagedModelDataHandlerImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testTemplateHandler() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "template-handler";
        String elementValue = "template-handler";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.template.TemplateHandler" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.TemplateHandlerImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testURLEncoderClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "url-encoder-class";
        String elementValue = "url-encoder-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.servlet.URLEncoder" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.URLEncoderImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testUserNotificationHandlerClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "user-notification-handler-class";
        String elementValue = "user-notification-handler-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.notifications.UserNotificationHandler" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.UserNotificationHandlerImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testWebdavStorageClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "webdav-storage-class";
        String elementValue = "webdav-storage-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.webdav.WebDAVStorage" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.WebDAVStorageImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testXmlRpcMethodClass() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "xml-rpc-method-class";
        String elementValue = "xml-rpc-method-class";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                "com.liferay.portal.kernel.xmlrpc.Method" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.XmlrpcMethodImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testHeaderPortletCss() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "header-portlet-css";
        String elementValue = "header-portlet-css";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "/css/main.css";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testfooterPortletCss() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "footer-portlet-css";
        String elementValue = "footer-portlet-css";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                LiferayPortletDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "/css/main.css";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );

    }

    @Test
    public void testHeaderPortletJavascript() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "header-portlet-javascript";
        String elementValue = "header-portlet-javascript";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "/js/main.js";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );

    }

    @Test
    public void testFooterPortletJavaScript() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "footer-portlet-javascript";
        String elementValue = "footer-portlet-javascript";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "/js/main.js";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testIcon() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "icon";
        String elementValue = "icon";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "/icon.png";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

    }

    @Test
    public void testPortletName() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet-name";
        String elementValue = "portlet-name";

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_REFERENCE_NOT_FOUND, new Object[] { elementValue,
                "portlet.xml" } );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "Portlet-Xml-Test";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

}
