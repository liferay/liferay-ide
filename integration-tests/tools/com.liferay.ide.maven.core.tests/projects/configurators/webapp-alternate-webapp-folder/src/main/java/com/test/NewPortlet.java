package com.test;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Portlet implementation class NewPortlet
 */
public class NewPortlet extends MVCPortlet {

	@Override
	public void doView(RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

		long groupId = themeDisplay.getLayout().getGroupId();

		try {
			Group group = GroupLocalServiceUtil.getGroup(groupId);
		} catch (PortalException exception) {

			// TODO Auto-generated catch block

			e.printStackTrace();
		} catch (SystemException exception) {

			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}

}