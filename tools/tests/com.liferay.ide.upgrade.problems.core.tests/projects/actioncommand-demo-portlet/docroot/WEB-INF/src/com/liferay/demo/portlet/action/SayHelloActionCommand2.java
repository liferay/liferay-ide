package com.liferay.demo.portlet.action;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.util.bridges.mvc.ActionCommand;
import com.liferay.util.bridges.mvc.BaseActionCommand;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

public class SayHelloActionCommand extends BaseActionCommand {

	@Override
	public boolean processCommand(PortletRequest portletRequest,
			PortletResponse portletResponse) throws PortletException {

		ActionRequest actionRequest = (ActionRequest) portletRequest;
		ActionResponse actionResponse = (ActionResponse) portletResponse;

		String name = ParamUtil.getString(actionRequest, "name");

		actionResponse.setRenderParameter("name", name);
		actionResponse.setRenderParameter(
			"mvcPath", "/html/demoactioncommand/view.jsp");


		return true;
	}

}
