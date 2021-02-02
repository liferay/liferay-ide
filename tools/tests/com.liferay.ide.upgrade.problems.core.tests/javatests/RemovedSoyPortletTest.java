
package com.liferay.ide.upgrade.problems.test

import com.liferay.flags.taglib.internal.frontend.js.loader.modules.extender.npm.NPMResolverProvider;
import com.liferay.flags.taglib.servlet.taglib.soy.FlagsTag;
import com.liferay.portal.portlet.bridge.soy.SoyPortletRegister;
import com.liferay.portal.portlet.bridge.soy.internal.SoyPortlet;
import com.liferay.portal.portlet.bridge.soy.internal.SoyPortletHelper;
import com.liferay.portal.portlet.bridge.soy.internal.SoyPortletRegisterTracker;
import com.liferay.portal.portlet.bridge.soy.internal.SoyPortletRequestFactory;
import com.liferay.portal.portlet.bridge.soy.internal.util.SoyTemplateResourceFactoryUtil;
import com.liferay.portal.portlet.bridge.soy.internal.util.SoyTemplateResourcesProviderUtil;

public class RemovedSoyPortletTest  {

	class SoyPortletInstance implements SoyPortletRegister {}

	private PortletConfig _portletConfig = PortletConfigFactoryUtil.get(getPortletId());

	private org.osgi.framework.Bundle _bundle = FrameworkUtil.getBundle(SoyPortlet.class);

	private MVCCommandCache<?> _mvcCommandCache = new MVCCommandCache<>(MVCRenderCommand.EMPTY,
			getInitParameter("mvc-render-command-package-prefix"), getPortletName(),
			portletId, MVCRenderCommand.class,"RenderCommand");

	private FriendlyURLMapper _friendlyURLMapper = (LiferayPortletConfig) _portletConfig.getFriendlyURLMapperInstance();

	private com.liferay.portal.kernel.template.Template _template = TemplateManagerUtil.getTemplate(TemplateConstants.LANG_TYPE_SOY,
			SoyTemplateResourceFactoryUtil.createSoyTemplateResource(_getTemplateResources()), false);

	private javax.portlet.ActionRequest _actionRequest = new ActionRequestWrapper();

	private javax.portlet.ResourceRequest _resourceRequest = new ResourceRequestWrapper();

	private javax.portlet.ResourceResponse _resourceResponse = new ResourceResponseWrapper();

	private javax.portlet.RenderRequest _renderRequest = new RenderRequestWrapper();

	private javax.portlet.RenderResponse _renderResponse = new RenderResponseWrapper();

	private List<com.liferay.portal.kernel.template.TemplateResource> _templateResources;

	public test() {

		NPMResolver npmResolver = NPMResolverProvider.getNPMResolver();

		SoyPortlet soyPortlet = new SoyPortlet();

		soyPortlet.init(_portletConfig);

		soyPortlet.render(_renderRequest, _renderResponse);

		soyPortlet.serveResource(_resourceRequest, _resourceResponse);

	    SoyPortletHelper soyPortletHelper = new SoyPortletHelper(_bundle, _mvcCommandCache, _friendlyURLMapper);

	    soyPortletHelper.getJavaScriptLoaderModule("Path");

	    soyPortletHelper.getRouterJavaScript("", "", "", "",  _template);

	    soyPortletHelper.serializeTemplate(_template);

	    SoyPortletRequestFactory soyPortletRequestFactory = new SoyPortletRequestFactory(_portletConfig.getprotlet());

	    soyPortletRequestFactory.createActionRequest(_resourceRequest);

	    soyPortletRequestFactory.createActionResponse(_actionRequest, _resourceResponse);

	    soyPortletRequestFactory.createRenderRequest(_resourceRequest, _resourceResponse);

	    soyPortletRequestFactory.createRenderResponse(_renderRequest, _resourceResponse);

	    SoyTemplateResource soyTemplateResource = SoyTemplateResourceFactoryUtil.createSoyTemplateResource(_templateResources);

	    List<TemplateResource> mvcCommandTemplateResources = SoyTemplateResourcesProviderUtil.getBundleTemplateResources(_bundle, "");
	}
}
