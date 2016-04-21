<#include "../basetemplate.ftl">

<#include "../component.ftl">

public class ${classname} implements ${supperclass} {

	@Activate
	public void activate() {
		Factory<SecurityManager> factory = new IniSecurityManagerFactory(
			"classpath:${classname}/userauth.ini");

		SecurityUtils.setSecurityManager(factory.getInstance());

		if (_log.isInfoEnabled()) {
			_log.info("activate");
		}
	}

	@Override
	public int authenticateByEmailAddress(
			long companyId, String emailAddress, String password,
			Map<String, String[]> headerMap,
			Map<String, String[]> parameterMap)
		throws AuthException {

		if (_log.isInfoEnabled()) {
			_log.info("authenticateByEmailAddress");
		}

		UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(
			emailAddress, password);

		Subject currentUser = SecurityUtils.getSubject();

		try {
			currentUser.login(usernamePasswordToken);

			boolean authenticated = currentUser.isAuthenticated();

			if (authenticated) {
				if (_log.isInfoEnabled()) {
					_log.info("authenticated");
				}

				return SKIP_LIFERAY_CHECK;
			}
			else {
				return FAILURE;
			}
		}
		catch (AuthenticationException ae) {
			_log.error(ae.getMessage(), ae);
			throw new AuthException(ae.getMessage(), ae);
		}
	}

	@Override
	public int authenticateByScreenName(
			long companyId, String screenName, String password,
			Map<String, String[]> headerMap,
			Map<String, String[]> parameterMap)
		throws AuthException {

		if (_log.isInfoEnabled()) {
			_log.info("authenticateByScreenName  - not implemented ");
		}

		return SUCCESS;
	}

	@Override
	public int authenticateByUserId(
			long companyId, long userId, String password,
			Map<String, String[]> headerMap,
			Map<String, String[]> parameterMap)
		throws AuthException {

		if (_log.isInfoEnabled()) {
			_log.info("authenticateByScreenName  - not implemented ");
		}

		return SUCCESS;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		${classname}.class);

}