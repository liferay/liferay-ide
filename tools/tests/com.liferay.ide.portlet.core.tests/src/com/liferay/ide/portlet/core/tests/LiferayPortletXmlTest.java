
package com.liferay.ide.portlet.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.lfportlet.model.AssetRendererFactory;
import com.liferay.ide.portlet.core.lfportlet.model.CronTriggeValueTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.CronTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.CustomUserAttribute;
import com.liferay.ide.portlet.core.lfportlet.model.CutomUserAttributeName;
import com.liferay.ide.portlet.core.lfportlet.model.ICronTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.ISimpleTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.ITrigger;
import com.liferay.ide.portlet.core.lfportlet.model.IndexerClass;
import com.liferay.ide.portlet.core.lfportlet.model.LiferayPortlet;
import com.liferay.ide.portlet.core.lfportlet.model.LiferayPortletXml;
import com.liferay.ide.portlet.core.lfportlet.model.PortletStyleElement;
import com.liferay.ide.portlet.core.lfportlet.model.PropertyCronTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.PropertySimpleTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.SchedulerEntry;
import com.liferay.ide.portlet.core.lfportlet.model.SimpleTriggeValueTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.SimpleTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.SocialActivityInterpreterClass;
import com.liferay.ide.portlet.core.lfportlet.model.StagedModelDataHandlerClass;
import com.liferay.ide.portlet.core.lfportlet.model.TrashHandler;
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

    protected LiferayPortletXml newLiferayPortletAppOp( InputStream source ) throws Exception
    {
        final LiferayPortletXml op =
            LiferayPortletXml.TYPE.instantiate( new RootXmlResource( new XmlResourceStore( source ) ) );
        return op;
    }

    private LiferayPortletXml op( final IProject project ) throws Exception
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

        return LiferayPortletXml.TYPE.instantiate( new RootXmlResource( store ) );
    }

    private LiferayPortletXml op( String source ) throws ResourceStoreException
    {
        return LiferayPortletXml.TYPE.instantiate( new RootXmlResource( new XmlResourceStore(
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

        LiferayPortletXml liferayPortletApp = op( testProject );

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

        LiferayPortletXml liferayPortletApp = op( testProject );

        for( LiferayPortlet liferayPortlet : liferayPortletApp.getPortlets() )
        {
            ElementList<PortletStyleElement> portletCsses = liferayPortlet.getHeaderPortletCsses();
            {
                for( PortletStyleElement portletCss : portletCsses )
                {
                    final PossibleValuesService scriptService =
                        portletCss.getValue().service( PossibleValuesService.class );
                    assertEquals( true, scriptService.values().contains( "/css/main.css" ) );
                }
            }
        }
    }

    @Test
    public void testNumberValidationService() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        LiferayPortletXml liferayPortletApp = LiferayPortletXml.TYPE.instantiate();
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

        LiferayPortletXml liferayPortletApp = op( testProject );

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
        final LiferayPortletXml portletApp = op( PORTLET_XML );

        assertNotNull( portletApp );

        final ElementList<LiferayPortlet> portlets = portletApp.getPortlets();

        assertNotNull( portlets );

        assertEquals( 1, portlets.size() );

        final LiferayPortlet portlet = portlets.get( 0 );

        assertNotNull( portlet );

        assertEquals( "new", portlet.getPortletName().content() );

        assertEquals( "/icon.png", portlet.getIcon().content().toPortableString() );

        assertEquals( "/testStrutsPath", portlet.getStrutsPath().content() );

        assertEquals( "com.test.configuration.Test", portlet.getConfigurationActionClass().content().toString() );

        String[] indexerClassNames = { "com.test.index.Test1", "com.test.index.Test2", "com.test.index.Test3" };

        final ElementList<IndexerClass> indexerClasses = portlet.getIndexerClasses();

        assertNotNull( indexerClasses );

        for( IndexerClass indexer : indexerClasses )
        {
            assertEquals( true, Arrays.asList( indexerClassNames ).contains( indexer.getValue().toString() ) );
        }

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

        final ElementList<PortletStyleElement> headerPortletCsses = portlet.getHeaderPortletCsses();

        assertNotNull( headerPortletCsses );

        String[] headerPortletCssesValues = { "/css/portlet1.css", "/css/portlet2.css", "/css/portlet3.css" };

        for( PortletStyleElement headerPortalCss : headerPortletCsses )
        {
            assertEquals(
                true,
                Arrays.asList( headerPortletCssesValues ).contains(
                    headerPortalCss.getValue().content().toPortableString() ) );
        }


        final ElementList<PortletStyleElement> headerPortletJses = portlet.getHeaderPortletJavascripts();

        assertNotNull( headerPortletJses );

        String[] headerPortletJsesValues = { "/js/portlet1.js", "/js/portlet2.js", "/js/portlet3.js" };

        for( PortletStyleElement headerPortletJs : headerPortletJses )
        {
            assertEquals(
                true,
                Arrays.asList( headerPortletJsesValues ).contains(
                    headerPortletJs.getValue().content().toPortableString() ) );
        }

        final ElementList<PortletStyleElement> footerPortletJses = portlet.getFooterPortletJavascripts();

        assertNotNull( footerPortletJses );

        String[] footerPortletJsesValues = { "/js/portlet1.js", "/js/portlet2.js", "/js/portlet3.js" };

        for( PortletStyleElement footerPortletJs : footerPortletJses )
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
