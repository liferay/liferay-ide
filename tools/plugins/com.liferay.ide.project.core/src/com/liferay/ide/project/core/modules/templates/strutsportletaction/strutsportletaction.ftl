<#include "../basetemplate.ftl">

<#include "../component.ftl">

public class ${classname} extends ${supperclass} {

	@Override
	public void processAction(
			StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("${classname} - procesAction");
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User loggedinUser = themeDisplay.getUser();

		if ((loggedinUser != null) && _log.isInfoEnabled()) {
			_log.info(
				"Logging in with user:[" + loggedinUser.getFirstName() + " " +
					loggedinUser.getLastName() + "]");

			_log.info(
				"Logged in user: Current Greetings[" +
					loggedinUser.getGreeting() + "]");
		}

		originalStrutsPortletAction.processAction(
			originalStrutsPortletAction, portletConfig, actionRequest,
			actionResponse);
	}

	@Override
	public String render(
			StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("${classname} - render");
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User loggedinUser = themeDisplay.getUser();

		if (loggedinUser != null) {
			loggedinUser.setLastName("BLADE");

			loggedinUser.setGreeting(
				"Hello," + loggedinUser.getFirstName() + " from BLADE!");

			_userLocalService.updateUser(loggedinUser);
		}

		return originalStrutsPortletAction.render(
			originalStrutsPortletAction, portletConfig, renderRequest,
			renderResponse);
	}

	@Override
	public void serveResource(
			StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, ResourceRequest resourceRequest,
			ResourceResponse resourceResponse)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("${classname} - serveResource");
		}

		originalStrutsPortletAction.serveResource(
			originalStrutsPortletAction, portletConfig, resourceRequest,
			resourceResponse);
	}

	@Reference(unbind = "-")
	public void setUserService(UserLocalService userService) {
		_userLocalService = userService;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		${classname}.class);

	private UserLocalService _userLocalService;

}