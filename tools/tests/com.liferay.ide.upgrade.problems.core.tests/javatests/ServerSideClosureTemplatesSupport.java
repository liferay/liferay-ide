import com.liferay.flags.taglib.servlet.taglib.soy.FlagsTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.AlertTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.BadgeTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.ButtonTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.CheckboxTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.DropdownActionsTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.DropdownMenuTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.FileCardTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.HorizontalCardTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.IconTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.ImageCardTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.LabelTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.LinkTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.ManagementToolbarTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.MultiSelectTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.NavigationBarTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.ProgressBarTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.RadioTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.SelectTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.StickerTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.StripeTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.TableTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.UserCardTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.VerticalCardTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.BaseClayTag;
import com.liferay.frontend.taglib.soy.servlet.taglib.ComponentRendererTag;
import com.liferay.frontend.taglib.soy.servlet.taglib.TemplateRendererTag;
import com.liferay.portal.template.soy.TemplateResource;
import com.liferay.portal.template.soy.SoyTemplateResourceFactory;
import com.liferay.portal.template.soy.constants.SoyTemplateConstants;
import com.liferay.portal.template.soy.data.SoyDataFactory;
import com.liferay.portal.template.soy.data.SoyRawData;
import com.liferay.portal.template.soy.util.SoyContext;
import com.liferay.portal.template.soy.util.SoyContextFactory;
import com.liferay.portal.template.soy.util.SoyContextFactoryUtil;
import com.liferay.portal.template.soy.util.SoyRawData;
import com.liferay.portal.template.soy.util.SoyTemplateResourcesProvider;
import com.liferay.portal.template.soy.context.contributor.internal.template.ThemeDisplaySoyTemplateContextContributor;
import com.liferay.portal.template.soy.internal.SoyContextImpl;
import com.liferay.portal.template.soy.internal.BaseTemplateManager;
import com.liferay.portal.template.soy.internal.SoyManagerCleaner;
import com.liferay.portal.template.soy.internal.SoyMsgBundleBridge;
import com.liferay.portal.template.soy.internal.SoyProviderCapabilityBundleRegister;
import com.liferay.portal.template.soy.internal.SoyTemplate;
import com.liferay.portal.template.soy.internal.SoyTemplateBundleResourceParser;
import com.liferay.portal.template.soy.internal.SoyTemplateContextHelper;
import com.liferay.portal.template.soy.internal.SoyTemplateRecord;
import com.liferay.portal.template.soy.internal.SoyTemplateResourceBundleTrackerCustomizer;
import com.liferay.portal.template.soy.internal.SoyTemplateResourceCache;
import com.liferay.portal.template.soy.internal.SoyTemplateResourceFactoryImpl;
import com.liferay.portal.template.soy.internal.SoyTemplateResourceLoader;
import com.liferay.portal.template.soy.internal.SoyTofuCacheBag;
import com.liferay.portal.template.soy.internal.SoyTofuCacheHandler;
import com.liferay.portal.template.soy.internal.activator.PortalTemplateSoyImplBundleActivator;
import com.liferay.portal.template.soy.internal.configuration.SoyTemplateEngineConfiguration;
import com.liferay.portal.template.soy.internal.data.SoyDataFactoryImpl;
import com.liferay.portal.template.soy.internal.data.SoyDataFactoryProvider;
import com.liferay.portal.template.soy.internal.data.SoyHTMLDataImpl;
import com.liferay.portal.template.soy.internal.data.SoyRawDataImpl;
import com.liferay.portal.template.soy.internal.upgrade.PortalTemplateSoyImplUpgrade;
import com.liferay.portal.template.soy.internal.util.SoyContextFactoryImpl;
import com.liferay.portal.template.soy.internal.util.SoyTemplateResourcesCollectorUtil;
import com.liferay.portal.template.soy.internal.util.SoyTemplateResourcesProviderImpl;
import com.liferay.portal.template.soy.internal.util.SoyTemplateUtil;
import com.liferay.frontend.taglib.clay.internal.SoyDataFactoryProvider;

/**
 * @author Seiphon Wang
 */
public class ServerSideClosureTemplatesSupport extends ComponentRendererTag implements TemplateResource, SoyTemplateResourceFactory {

	public void MainTest() {
		SoyDataFactoryImpl soyDataFactoryImpl = new SoyDataFactoryImpl();

		soyDataFactoryImpl.createSoyRawData("test");

		SoyTemplateContextHelper soyTemplateContextHelper = new SoyTemplateContextHelper();

		soyTemplateContextHelper.getRestrictedVariables();
	}
}
