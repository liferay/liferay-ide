<#include "../basetemplate.ftl">

<#include "../component.ftl">

<#if ( supperclass == "MVCActionCommand" ) >
public class ${classname} implements ${supperclass} {
	@Override
	public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		_handleActionCommand(actionRequest);

		return true;
	}

	private void _handleActionCommand(ActionRequest actionRequest) {
		String name = ParamUtil.get(actionRequest, "name", StringPool.BLANK);

		if (_log.isInfoEnabled()) {
			_log.info("Hello " + name);
		}

		String greetingMessage = "Hello " + name + "! Welcome to OSGi";

		actionRequest.setAttribute("GREETER_MESSAGE", greetingMessage);

		SessionMessages.add(actionRequest, "greetingMessage", greetingMessage);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		${classname}.class);
<#elseif ( supperclass == "FreeMarkerPortlet" ) >
public class ${classname} extends ${supperclass} {
</#if>
}