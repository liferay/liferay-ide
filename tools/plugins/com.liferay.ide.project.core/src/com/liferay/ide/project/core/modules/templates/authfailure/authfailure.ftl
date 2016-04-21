<#include "../basetemplate.ftl">

<#include "../component.ftl">

public class ${classname} implements ${supperclass} {

	@Override
	public void onFailureByEmailAddress(
			long companyId, String emailAddress,
			Map<String, String[]> headerMap,
			Map<String, String[]> parameterMap)
		throws AuthException {

		try {
			User user = UserLocalServiceUtil.getUserByEmailAddress(
				companyId, emailAddress);

			int failures = user.getFailedLoginAttempts();

			if (_log.isInfoEnabled()) {
				_log.info(
					"onFailureByEmailAddress: " + emailAddress +
						" has failed to login " + failures + " times");
			}
		}
		catch (PortalException pe) {
		}
	}

	@Override
	public void onFailureByScreenName(
			long companyId, String screenName, Map<String, String[]> headerMap,
			Map<String, String[]> parameterMap)
		throws AuthException {

		try {
			User user = UserLocalServiceUtil.getUserByScreenName(
				companyId, screenName);

			int failures = user.getFailedLoginAttempts();

			if (_log.isInfoEnabled()) {
				_log.info(
					"onFailureByScreenName: " + screenName +
						" has failed to login " + failures + " times");
			}
		}
		catch (PortalException pe) {
		}
	}

	@Override
	public void onFailureByUserId(
			long companyId, long userId, Map<String, String[]> headerMap,
			Map<String, String[]> parameterMap)
		throws AuthException {

		try {
			User user = UserLocalServiceUtil.getUserById(userId);

			int failures = user.getFailedLoginAttempts();

			if (_log.isInfoEnabled()) {
				_log.info(
					"onFailureByUserId: userId " + userId +
						" has failed to login " + failures + " times");
			}
		}
		catch (PortalException pe) {
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(${classname}.class);

}