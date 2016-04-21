<#include "../basetemplate.ftl">

<#include "../component.ftl">

@Path("/${projectname}")
public class ${classname} extends ${supperclass} {

	@Override
	public Set<Object> getSingletons() {
		return Collections.<Object> singleton(this);
	}

	@GET
	@Path("/list")
	@Produces("text/plain")
	public String getUsers() {
		StringBuilder result = new StringBuilder();

		for (User user : _userLocalService.getUsers(-1, -1)) {
			result.append(user.getFullName()).append(",\n");
		}

		return result.toString();
	}

	@Reference
	public void setUserLocalService(UserLocalService userLocalService) {
		this._userLocalService = userLocalService;
	}

	private UserLocalService _userLocalService;
}