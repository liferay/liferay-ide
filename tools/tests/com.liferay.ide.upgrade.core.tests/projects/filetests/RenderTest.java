import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portlet.asset.model.AssetRenderer;


public class RenderMethodTest{

	public void testMethod( long classPK, RenderRequest renderRequest,
		RenderResponse renderResponse, String template)){

		AssetRenderer assetRenderer = getAssetRenderer(classPK);

		if (assetRenderer != null) {
			 assetRenderer.render(renderRequest, renderResponse, template);
		}

		WorkflowHandler workFlowHandler = getAssetRenderer(classPK);

		if (assetRenderer != null) {
			workFlowHandler.render(classPK,renderRequest, renderResponse, template);
		}
	}

	@Override
	public String render(
		long classPK, RenderRequest renderRequest,
		RenderResponse renderResponse, String template) {

		try {
			AssetRenderer assetRenderer = getAssetRenderer(classPK);

			if (assetRenderer != null) {
				return assetRenderer.render(
					renderRequest, renderResponse, template);
			}
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn(e, e);
			}
		}

		return null;
	}

	@Override
	public String render(
		RenderRequest renderRequest,
		RenderResponse renderResponse, String template) {

		try {
			AssetRenderer assetRenderer = getAssetRenderer(classPK);

			if (assetRenderer != null) {
				return assetRenderer.render(
					renderRequest, renderResponse, template);
			}
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn(e, e);
			}
		}

		return null;
	}
}