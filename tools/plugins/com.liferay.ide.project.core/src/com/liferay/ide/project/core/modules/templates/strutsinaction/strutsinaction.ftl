<#include "../basetemplate.ftl">

<#include "../component.ftl">

public class ${classname} extends ${supperclass} {

	public String execute(
			HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Processing path /c/portal/${simplecomponent}");
		}

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/${componentfolder}/html/portal/${simplecomponent}.jsp");

		requestDispatcher.forward(request, response);

		return null;
	}

	@Reference(target = "(osgi.web.symbolicname=${bundlesymbolicname})")
	protected void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		${classname}.class);

	private ServletContext _servletContext;

}