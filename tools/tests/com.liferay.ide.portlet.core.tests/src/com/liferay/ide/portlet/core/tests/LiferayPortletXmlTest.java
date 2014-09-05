
package com.liferay.ide.portlet.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.lfportlet.model.AssetRendererFactory;
import com.liferay.ide.portlet.core.lfportlet.model.AtomCollectionAdapter;
import com.liferay.ide.portlet.core.lfportlet.model.CronTriggeValueTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.CronTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.CustomAttributesDisplay;
import com.liferay.ide.portlet.core.lfportlet.model.CustomUserAttribute;
import com.liferay.ide.portlet.core.lfportlet.model.CutomUserAttributeName;
import com.liferay.ide.portlet.core.lfportlet.model.FooterPortalCss;
import com.liferay.ide.portlet.core.lfportlet.model.FooterPortalJavascript;
import com.liferay.ide.portlet.core.lfportlet.model.FooterPortletCss;
import com.liferay.ide.portlet.core.lfportlet.model.FooterPortletJavascript;
import com.liferay.ide.portlet.core.lfportlet.model.HeaderPortalCss;
import com.liferay.ide.portlet.core.lfportlet.model.HeaderPortalJavascript;
import com.liferay.ide.portlet.core.lfportlet.model.HeaderPortletCss;
import com.liferay.ide.portlet.core.lfportlet.model.HeaderPortletJavascript;
import com.liferay.ide.portlet.core.lfportlet.model.ICronTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.ISimpleTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.ITrigger;
import com.liferay.ide.portlet.core.lfportlet.model.IndexerClass;
import com.liferay.ide.portlet.core.lfportlet.model.LiferayPortlet;
import com.liferay.ide.portlet.core.lfportlet.model.LiferayPortletApp;
import com.liferay.ide.portlet.core.lfportlet.model.PropertyCronTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.PropertySimpleTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.SchedulerEntry;
import com.liferay.ide.portlet.core.lfportlet.model.SimpleTriggeValueTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.SimpleTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.SocialActivityInterpreterClass;
import com.liferay.ide.portlet.core.lfportlet.model.StagedModelDataHandlerClass;
import com.liferay.ide.portlet.core.lfportlet.model.TrashHandler;
import com.liferay.ide.portlet.core.lfportlet.model.UserNotificationHandlerClass;
import com.liferay.ide.portlet.core.lfportlet.model.internal.NumberValueValidationService;
import com.liferay.ide.portlet.core.model.SecurityRoleRef;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.tests.XmlTestsBase;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.services.RelativePathService;
import org.eclipse.sapphire.services.ValidationService;
import org.junit.Test;

/**
 * @author Simon Jiang
 */

public class LiferayPortletXmlTest extends XmlTestsBase
{

    private static final String PORTLET_XML = "files/liferay-portlet.xml";

    private IFile getLiferayPortletXml( IProject project )
    {
        IFile portletXmlFile =
            CoreUtil.getDefaultDocrootFolder( project ).getFile(
                "WEB-INF/" + ILiferayConstants.LIFERAY_PORTLET_XML_FILE );

        return portletXmlFile;

    }

    protected LiferayPortletApp newLiferayPortletAppOp( InputStream source ) throws Exception
    {
        final LiferayPortletApp op =
            LiferayPortletApp.TYPE.instantiate( new RootXmlResource( new XmlResourceStore( source ) ) );
        return op;
    }

    private LiferayPortletApp op( final IProject project ) throws Exception
    {
        final XmlResourceStore store = new XmlResourceStore( getLiferayPortletXml( project ).getContents( true ) )
        {

            public <A> A adapt( Class<A> adapterType )
            {
                if( IProject.class.equals( adapterType ) )
                {
                    return adapterType.cast( project );
                }

                return super.adapt( adapterType );
            }
        };

        return LiferayPortletApp.TYPE.instantiate( new RootXmlResource( store ) );
    }

    private LiferayPortletApp op( String source ) throws ResourceStoreException
    {
        return LiferayPortletApp.TYPE.instantiate( new RootXmlResource( new XmlResourceStore(
            getClass().getResourceAsStream( source ) ) ) );
    }

    @Test
    public void testIconRelativePathService() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        NewLiferayPluginProjectOp newProjectOp = NewLiferayPluginProjectOp.TYPE.instantiate();

        newProjectOp.setProjectName( "test-path" );
        newProjectOp.setPluginType( PluginType.portlet );
        newProjectOp.setIncludeSampleCode( true );
        newProjectOp.setPortletFramework( "mvc" );
        newProjectOp.setPortletName( "testPortlet" );
        final IProject testProject = createAntProject( newProjectOp );

        LiferayPortletApp liferayPortletApp = op( testProject );

        for( LiferayPortlet liferayPortlet : liferayPortletApp.getPortlets() )
        {
            final RelativePathService pathService = liferayPortlet.getIcon().service( RelativePathService.class );
            List<Path> iconPaths = pathService.roots();
            assertEquals( true, iconPaths.size() > 0 );
        }

    }

    @Test
    public void testLiferayScriptPossibleValuesService() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        NewLiferayPluginProjectOp newProjectOp = NewLiferayPluginProjectOp.TYPE.instantiate();
        newProjectOp.setProjectName( "test-script" );
        newProjectOp.setPluginType( PluginType.portlet );
        newProjectOp.setIncludeSampleCode( true );
        newProjectOp.setPortletFramework( "mvc" );
        newProjectOp.setPortletName( "testPortlet" );
        final IProject testProject = createAntProject( newProjectOp );

        LiferayPortletApp liferayPortletApp = op( testProject );

        for( LiferayPortlet liferayPortlet : liferayPortletApp.getPortlets() )
        {
            ElementList<HeaderPortletCss> portletCsses = liferayPortlet.getHeaderPortletCsses();
            {
                for( HeaderPortletCss portletCss : portletCsses )
                {
                    final PossibleValuesService scriptService =
                        portletCss.getValue().service( PossibleValuesService.class );
                    assertEquals( true, scriptService.values().contains( "/css/main.css" ) );
                }
            }

            ElementList<HeaderPortalJavascript> portletJses = liferayPortlet.getHeaderPortalJavascripts();
            {
                for( HeaderPortalJavascript portletJs : portletJses )
                {
                    final PossibleValuesService scriptService =
                        portletJs.getValue().service( PossibleValuesService.class );
                    assertEquals( true, scriptService.values().contains( "/js/main.js" ) );
                }
            }
        }
    }

    @Test
    public void testNumberValidationService() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        LiferayPortletApp liferayPortletApp = LiferayPortletApp.TYPE.instantiate();
        LiferayPortlet liferayPortlet = liferayPortletApp.getPortlets().insert();
        SchedulerEntry schedulerEntry = liferayPortlet.getSchedulerEntries().insert();

        ElementHandle<ITrigger> cronTrigger = ( (ElementHandle<ITrigger>) ( schedulerEntry.getPortletTrigger() ) );

        ElementHandle<ICronTrigger> cronTriggerValueTrigger =
            cronTrigger.content( true, CronTrigger.class ).getCronTrigger();

        CronTriggeValueTrigger cronTriggerValue = cronTriggerValueTrigger.content( true, CronTriggeValueTrigger.class );

        NumberValueValidationService vs =
            cronTriggerValue.getCronTriggerValue().service( NumberValueValidationService.class );

        cronTriggerValue.setCronTriggerValue( "-1" );
        assertEquals( false, "ok".equals( vs.validation().message() ) );

        cronTriggerValue.setCronTriggerValue( "150" );
        assertEquals( true, "ok".equals( vs.validation().message() ) );

    }

    @Test
    public void testPortletNameValidationService() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        NewLiferayPluginProjectOp newProjectOp = NewLiferayPluginProjectOp.TYPE.instantiate();
        newProjectOp.setProjectName( "test-validation" );
        newProjectOp.setPluginType( PluginType.portlet );
        newProjectOp.setIncludeSampleCode( true );
        newProjectOp.setPortletFramework( "mvc" );
        newProjectOp.setPortletName( "testPortlet" );
        final IProject testProject = createAntProject( newProjectOp );

        LiferayPortletApp liferayPortletApp = op( testProject );

        for( LiferayPortlet liferayPortlet : liferayPortletApp.getPortlets() )
        {
            final ValidationService vs = liferayPortlet.getPortletName().service( ValidationService.class );
            assertEquals( "ok", vs.validation().message() );
            assertEquals( "ok", liferayPortlet.getPortletName().validation().message() );
        }

        for( LiferayPortlet liferayPortlet : liferayPortletApp.getPortlets() )
        {
            liferayPortlet.setPortletName( "test1" );
            final ValidationService vs = liferayPortlet.getPortletName().service( ValidationService.class );
            assertEquals( false, "ok".equals( vs.validation().message() ) );
            assertEquals( false, "ok".equals( liferayPortlet.getPortletName().validation().message() ) );
        }

    }

    @Test
    public void testPortletXmlRead() throws Exception
    {
        final LiferayPortletApp portletApp = op( PORTLET_XML );

        assertNotNull( portletApp );

        final ElementList<LiferayPortlet> portlets = portletApp.getPortlets();

        assertNotNull( portlets );

        assertEquals( 1, portlets.size() );

        final LiferayPortlet portlet = portlets.get( 0 );

        assertNotNull( portlet );

        assertEquals( "new", portlet.getPortletName().content() );

        assertEquals( "/icon.png", portlet.getIcon().content().toPortableString() );

        assertEquals( "/testVirtualPath", portlet.getVirtualPath().content() );

        assertEquals( "/testStrutsPath", portlet.getStrutsPath().content() );

        assertEquals( "/test", portlet.getParentStrutsPath().content() );

        assertEquals( "com.test.configuration.Test", portlet.getConfigurationActionClass().content().toString() );

        String[] indexerClassNames = { "com.test.index.Test1", "com.test.index.Test2", "com.test.index.Test3" };

        final ElementList<IndexerClass> indexerClasses = portlet.getIndexerClasses();

        assertNotNull( indexerClasses );

        for( IndexerClass indexer : indexerClasses )
        {
            assertEquals( true, Arrays.asList( indexerClassNames ).contains( indexer.getValue().toString() ) );
        }

        assertEquals( "com.test.Opensearch", portlet.getOpenSearchClass().content().toString() );

        String[] schedulerEntryDescriptions = { "scheduler cron entry test", "scheduler simple entry test" };

        String[] schedulerEntryClasses =
            { "com.test.schedulerEntry.TestScheduler1", "com.test.schedulerEntry.TestScheduler2" };

        final ElementList<SchedulerEntry> schedulerEntris = portlet.getSchedulerEntries();

        assertNotNull( schedulerEntris );

        for( SchedulerEntry schedulerEntry : schedulerEntris )
        {
            assertEquals(
                true,
                Arrays.asList( schedulerEntryDescriptions ).contains(
                    schedulerEntry.getSchedulerDescription().content() ) );

            assertEquals(
                true,
                Arrays.asList( schedulerEntryClasses ).contains(
                    schedulerEntry.getSchedulerEventListenerClass().toString() ) );

            ElementHandle<ITrigger> trigger = schedulerEntry.getPortletTrigger();

            if( trigger.content() instanceof CronTrigger )
            {
                CronTrigger cronTrigger = (CronTrigger) ( trigger.content() );

                ElementHandle<ICronTrigger> cronTriggerDetail = cronTrigger.getCronTrigger();

                if( cronTriggerDetail.content() instanceof PropertyCronTrigger )
                {
                    PropertyCronTrigger propertyTrigger = (PropertyCronTrigger) cronTriggerDetail.content();
                    assertEquals( "cron", propertyTrigger.getPropertyKey().content() );
                }
                else if( cronTriggerDetail.content() instanceof CronTriggeValueTrigger )
                {
                    CronTriggeValueTrigger valueTrigger = (CronTriggeValueTrigger) cronTriggerDetail.content();
                    assertEquals( "15", valueTrigger.getCronTriggerValue().content() );
                }

            }
            else
            {
                SimpleTrigger simpleTrigger = (SimpleTrigger) ( trigger.content() );

                ElementHandle<ISimpleTrigger> simpleTriggerDetail = simpleTrigger.getSimpleTrigger();

                if( simpleTriggerDetail.content() instanceof PropertySimpleTrigger )
                {
                    PropertySimpleTrigger propertyTrigger = (PropertySimpleTrigger) simpleTriggerDetail.content();
                    assertEquals( "simple", propertyTrigger.getPropertyKey().content() );
                }
                else if( simpleTriggerDetail.content() instanceof SimpleTriggeValueTrigger )
                {
                    SimpleTriggeValueTrigger valueTrigger = (SimpleTriggeValueTrigger) simpleTriggerDetail.content();
                    assertEquals( "15", valueTrigger.getSimpleTriggerValue().content() );
                }

                assertEquals( "minute", simpleTrigger.getTimeUnit().content() );
            }
        }

        assertEquals( "com.test.portletUrlClass.Test", portlet.getPortletUrlClass().content().toString() );

        assertEquals( "com.test.friendUrlMapper.Test", portlet.getFriendlyURLMapperClass().content().toString() );

        assertEquals( "test", portlet.getFriendlyURLMapping().toString() );

        assertEquals( "test", portlet.getFriendlyURLRoutes().toString() );

        assertEquals( "com.test.urlEncoder.Test", portlet.getURLEncoderClass().content().toString() );

        assertEquals( "com.test.portletDataHandler.Test", portlet.getPortletDataHandlerClass().content().toString() );

        final ElementList<StagedModelDataHandlerClass> stageHandlers = portlet.getStagedModelDataHandlerClasses();

        assertNotNull( stageHandlers );

        String[] stageHandlersValue =
            { "com.test.stagedModelDataHandler.Test1", "com.test.stagedModelDataHandler.Test2",
                "com.test.stagedModelDataHandler.Test3" };

        for( StagedModelDataHandlerClass stageHandler : stageHandlers )
        {
            assertEquals(
                true, Arrays.asList( stageHandlersValue ).contains( stageHandler.getValue().content().toString() ) );
        }

        assertEquals( "com.test.templateHandler.Test1", portlet.getTemplateHandler().content().toString() );

        assertEquals(
            "com.test.portletLayoutListener.Test1", portlet.getPortletLayoutListenerClass().content().toString() );

        assertEquals( "com.test.pollerProcesser.Test1", portlet.getPollerProcessorClass().content().toString() );

        assertEquals( "com.test.popMessageListern.Test1", portlet.getPopMessageListenerClass().content().toString() );

        final ElementList<SocialActivityInterpreterClass> socialActivities =
            portlet.getSocialActivityInterpreterClasses();

        assertNotNull( socialActivities );

        String[] socialActivityValues =
            { "com.test.socialActivityListener.Test1", "com.test.socialActivityListener.Test2",
                "com.test.socialActivityListener.Test3" };

        for( SocialActivityInterpreterClass socialActivity : socialActivities )
        {
            assertEquals(
                true, Arrays.asList( socialActivityValues ).contains( socialActivity.getValue().content().toString() ) );
        }

        assertEquals(
            "com.test.socialRequestInterperter.Test1", portlet.getSocialRequestInterpreterClass().content().toString() );

        assertEquals( "/userNotification.xml", portlet.getUserNotificationDefinitions().toString() );

        final ElementList<UserNotificationHandlerClass> notficationHandlers =
            portlet.getUserNotificationHandlerClasses();

        assertNotNull( notficationHandlers );

        String[] notficationHandlerValues =
            { "com.test.userNotificationHandler.Test1", "com.test.userNotificationHandler.Test2",
                "com.test.userNotificationHandler.Test3" };

        for( UserNotificationHandlerClass notficationHandler : notficationHandlers )
        {
            assertEquals(
                true,
                Arrays.asList( notficationHandlerValues ).contains( notficationHandler.getValue().content().toString() ) );
        }

        assertEquals( "webdav", portlet.getWebDAVStorageToken().content().toPortableString() );

        assertEquals( "com.test.webdavStorage.Test1", portlet.getWebDAVStorageClass().content().toString() );

        assertEquals( "com.test.xmlRpcMethod.Test1", portlet.getXmlRPCMethodClass().content().toString() );

        assertEquals( "my", portlet.getControlPanelEntryCategory().content() );

        assertEquals( new Double( 1.5 ), portlet.getControlPanelEntryWeight().content() );

        assertEquals( "com.test.NewPortletControlPanelEntry", portlet.getControlPanelEntryClass().content().toString() );

        final ElementList<AssetRendererFactory> assetHandlers = portlet.getAssetRendererFactories();

        assertNotNull( assetHandlers );

        String[] assetHandlersValues =
            { "com.test.assetRenderFactory.Test1", "com.test.assetRenderFactory.Test2",
                "com.test.assetRenderFactory.Test3" };

        for( AssetRendererFactory assetHandler : assetHandlers )
        {
            assertEquals(
                true, Arrays.asList( assetHandlersValues ).contains( assetHandler.getValue().content().toString() ) );
        }

        final ElementList<AtomCollectionAdapter> atomAdapters = portlet.getAtomCollectionAdapters();

        assertNotNull( atomAdapters );

        String[] atomAdaptersValues =
            { "com.test.atomCollectionAdapter.Test1", "com.test.atomCollectionAdapter.Test2",
                "com.test.atomCollectionAdapter.Test3" };

        for( AtomCollectionAdapter atomAdapter : atomAdapters )
        {
            assertEquals(
                true, Arrays.asList( atomAdaptersValues ).contains( atomAdapter.getValue().content().toString() ) );
        }

        final ElementList<CustomAttributesDisplay> customDisplays = portlet.getCustomAttributesDisplays();

        assertNotNull( customDisplays );

        String[] customDisplaysValues =
            { "com.test.customAttribute.Test1", "com.test.customAttribute.Test2", "com.test.customAttribute.Test3" };

        for( CustomAttributesDisplay customDisplay : customDisplays )
        {
            assertEquals(
                true, Arrays.asList( customDisplaysValues ).contains( customDisplay.getValue().content().toString() ) );
        }

        assertEquals( "com.test.ddmDisplay.Test1", portlet.getDDMDisplay().content().toString() );

        assertEquals( "com.test.permissionPropagator.Test1", portlet.getPermissionPropagator().content().toString() );

        final ElementList<TrashHandler> trashHanlders = portlet.getTrashHandlers();

        assertNotNull( trashHanlders );

        String[] trashHanldersValues =
            { "com.test.trashHandler.Test1", "com.test.trashHandler.Test2", "com.test.trashHandler.Test3" };

        for( TrashHandler trashHanlder : trashHanlders )
        {
            assertEquals(
                true, Arrays.asList( trashHanldersValues ).contains( trashHanlder.getValue().content().toString() ) );
        }

        // workflow test

        assertEquals( "userId", portlet.getUserPrincipalStrategy().toString() );

        assertEquals( new Double( 15 ), portlet.getActionTimeout().content() );

        assertEquals( new Double( 15 ), portlet.getRenderTimeout().content() );

        final ElementList<HeaderPortalCss> headerPortalCsses = portlet.getHeaderPortalCsses();

        String[] headerPortalCssesValues = { "/css/portal1.css", "/css/portal2.css", "/css/portal3.css" };

        assertNotNull( headerPortalCsses );

        for( HeaderPortalCss headerPortalCss : headerPortalCsses )
        {
            assertEquals(
                true,
                Arrays.asList( headerPortalCssesValues ).contains(
                    headerPortalCss.getValue().content().toPortableString() ) );
        }

        final ElementList<HeaderPortletCss> headerPortletCsses = portlet.getHeaderPortletCsses();

        assertNotNull( headerPortletCsses );

        String[] headerPortletCssesValues = { "/css/portlet1.css", "/css/portlet2.css", "/css/portlet3.css" };

        for( HeaderPortletCss headerPortalCss : headerPortletCsses )
        {
            assertEquals(
                true,
                Arrays.asList( headerPortletCssesValues ).contains(
                    headerPortalCss.getValue().content().toPortableString() ) );
        }

        final ElementList<HeaderPortalJavascript> headerPortalJses = portlet.getHeaderPortalJavascripts();

        assertNotNull( headerPortalJses );

        String[] headerPortalJsesValues = { "/js/portal.js", "/js/porta2.js", "/js/porta3.js" };

        for( HeaderPortalJavascript headerPortalJs : headerPortalJses )
        {
            assertEquals(
                true,
                Arrays.asList( headerPortalJsesValues ).contains(
                    headerPortalJs.getValue().content().toPortableString() ) );
        }

        final ElementList<HeaderPortletJavascript> headerPortletJses = portlet.getHeaderPortletJavascripts();

        assertNotNull( headerPortletJses );

        String[] headerPortletJsesValues = { "/js/portlet1.js", "/js/portlet2.js", "/js/portlet3.js" };

        for( HeaderPortletJavascript headerPortletJs : headerPortletJses )
        {
            assertEquals(
                true,
                Arrays.asList( headerPortletJsesValues ).contains(
                    headerPortletJs.getValue().content().toPortableString() ) );
        }

        final ElementList<FooterPortalCss> footerPortalCsses = portlet.getFooterPortalCsses();

        assertNotNull( footerPortalCsses );

        String[] footerPortalCssesValues = { "/css/portal1.css", "/css/portal2.css", "/css/portal3.css" };

        for( FooterPortalCss footerPortalCss : footerPortalCsses )
        {
            assertEquals(
                true,
                Arrays.asList( footerPortalCssesValues ).contains(
                    footerPortalCss.getValue().content().toPortableString() ) );
        }

        final ElementList<FooterPortletCss> footerPortletCsses = portlet.getFooterPortletCsses();

        assertNotNull( footerPortletCsses );

        String[] footerPortletCssesValues = { "/css/portlet1.css", "/css/portlet2.css", "/css/portlet3.css" };

        for( FooterPortletCss footerPortalCss : footerPortletCsses )
        {
            assertEquals(
                true,
                Arrays.asList( footerPortletCssesValues ).contains(
                    footerPortalCss.getValue().content().toPortableString() ) );
        }

        final ElementList<FooterPortalJavascript> footerPortalJses = portlet.getFooterPortalJavascripts();

        assertNotNull( footerPortalJses );

        String[] footerPortalJsesValues = { "/js/portal1.js", "/js/portal2.js", "/js/portal3.js" };

        for( FooterPortalJavascript footerPortaljs : footerPortalJses )
        {
            assertEquals(
                true,
                Arrays.asList( footerPortalJsesValues ).contains(
                    footerPortaljs.getValue().content().toPortableString() ) );
        }

        final ElementList<FooterPortletJavascript> footerPortletJses = portlet.getFooterPortletJavascripts();

        assertNotNull( footerPortletJses );

        String[] footerPortletJsesValues = { "/js/portlet1.js", "/js/portlet2.js", "/js/portlet3.js" };

        for( FooterPortletJavascript footerPortletJs : footerPortletJses )
        {
            assertEquals(
                true,
                Arrays.asList( footerPortletJsesValues ).contains(
                    footerPortletJs.getValue().content().toPortableString() ) );
        }

        assertEquals( "test", portlet.getCssClassWrapper().toString() );

        assertEquals( "test", portlet.getCssClassWrapper().content() );

        final ElementList<SecurityRoleRef> roleMappers = portletApp.getRoleMappers();

        assertNotNull( roleMappers );

        String[] roleMapperNameValues = { "administrator", "guest", "power-user", "user" };
        String[] roleMapperLinkValues = { "Administrator", "Guest", "Power User", "User" };

        for( SecurityRoleRef roleMapper : roleMappers )
        {
            assertEquals( true, Arrays.asList( roleMapperNameValues ).contains( roleMapper.getRoleName().content() ) );

            assertEquals( true, Arrays.asList( roleMapperLinkValues ).contains( roleMapper.getRoleLink().content() ) );

        }

        final ElementList<CustomUserAttribute> userAttributes = portletApp.getCustomUserAttributes();

        assertNotNull( userAttributes );

        String[] attributeNameValues = { "tag1", "tag2", "tag3" };
        String[] attributeClassValues = { "com.test.customUserAttribute.Test1" };

        for( CustomUserAttribute attribute : userAttributes )
        {
            ElementList<CutomUserAttributeName> attributeNames = attribute.getCustomUserAttributeNames();

            assertNotNull( attributeNames );

            for( CutomUserAttributeName attributeName : attributeNames )
            {
                assertEquals( true, Arrays.asList( attributeNameValues ).contains( attributeName.getValue().content() ) );
            }

            assertEquals( true, Arrays.asList( attributeClassValues ).contains( attribute.getCustomClass().toString() ) );

        }

    }

}
