<#include "../basetemplate.ftl">

<#include "../component.ftl">

public class ${classname} {

	public UserLocalService getUserLocalService() {
		return _userLocalService;
	}

	@Reference
	public void setUserLocalService(UserLocalService _userLocalService) {
		this._userLocalService = _userLocalService;
	}

	public void usercount() {
		System.out.println(
			"# of users: " + getUserLocalService().getUsersCount());
	}

	private UserLocalService _userLocalService;
}