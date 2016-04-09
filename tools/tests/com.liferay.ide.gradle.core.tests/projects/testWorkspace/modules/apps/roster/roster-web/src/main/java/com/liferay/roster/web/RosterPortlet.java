package com.liferay.roster.web;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.roster.model.Roster;
import com.liferay.roster.service.RosterLocalServiceUtil;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;

import org.osgi.service.component.annotations.Component;

/**
 * @author David Truong
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.roster",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=My Roster",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class RosterPortlet extends MVCPortlet {

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		try {
			String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				updateRoster(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				deleteRoster(actionRequest);
			}

			if (Validator.isNotNull(cmd)) {
				if (SessionErrors.isEmpty(actionRequest)) {
					SessionMessages.add(actionRequest, "requestProcessed");
				}

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				actionResponse.sendRedirect(redirect);
			}
		}
		catch (Exception e) {
			throw new PortletException(e);
		}
	}

	protected void deleteRoster(ActionRequest actionRequest) throws Exception {
		long rosterId = ParamUtil.getLong(actionRequest, "rosterId");

		RosterLocalServiceUtil.deleteRoster(rosterId);
	}

	protected void updateRoster(ActionRequest actionRequest) throws Exception {
		long rosterId = ParamUtil.getLong(actionRequest, "rosterId");

		long clubId = ParamUtil.getLong(actionRequest, "clubId");
		String name = ParamUtil.getString(actionRequest, "name");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Roster.class.getName(), actionRequest);

		if (rosterId <= 0) {
			RosterLocalServiceUtil.addRoster(clubId, name, serviceContext);
		}
		else {
			RosterLocalServiceUtil.updateRoster(
				rosterId, clubId, name, serviceContext);
		}
	}

}