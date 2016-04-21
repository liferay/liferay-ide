<#include "../basetemplate.ftl">

<#include "../component.ftl">

public class ${classname} extends ${supperclass} {
<#if ( supperclass == "BasePollerProcessor" ) >
	@Override
	protected PollerResponse doReceive(PollerRequest pollerRequest)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Recevied the poller request"+pollerRequest);
		}

		JSONObject responseJSON = JSONFactoryUtil.createJSONObject();
		PollerResponse pollerResponse = new DefaultPollerResponse();
		responseJSON.put(
			"message", "Hello from ${classname}, time now is:"+new Date());
		pollerResponse.setParameter("content", responseJSON);

		return pollerResponse;
	}

	@Override
	protected void doSend(PollerRequest pollerRequest) throws Exception {
		String status = getString(pollerRequest, "status");

		if (_log.isInfoEnabled()) {
			_log.info("Poller status:"+status);
		}
	}

	private final Log _log = LogFactoryUtil.getLog(${classname}.class);
</#if>
}