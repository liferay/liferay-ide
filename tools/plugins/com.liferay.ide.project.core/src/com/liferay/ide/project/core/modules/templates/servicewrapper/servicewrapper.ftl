<#include "../basetemplate.ftl">

@Component(service = ${extensionclass})

public class ${classname} extends ${supperclass} {

	public ${classname}() {
		super(null);
	}

	@Override
	public int authenticateByEmailAddress(
			long companyId, String emailAddress, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap,
			Map<String, Object> resultsMap)
		throws PortalException {

		System.out.println(
			"Authenticating user by email address " + emailAddress);

		return super.authenticateByEmailAddress(
			companyId, emailAddress, password, headerMap, parameterMap,
			resultsMap);
	}

	@Override
	public User getUser(long userId) throws PortalException {
		System.out.println("Getting user by id " + userId);

		return super.getUser(userId);
	}
}