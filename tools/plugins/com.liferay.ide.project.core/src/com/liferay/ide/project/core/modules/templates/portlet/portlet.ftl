<#include "../basetemplate.ftl">

<#include "../component.ftl">

public class ${classname} extends ${supperclass} {

	@Override
	protected void doView(RenderRequest request, RenderResponse response)
		throws IOException, PortletException {

		PrintWriter printWriter = response.getWriter();

		printWriter.print("DS Portlet - Hello World!");
	}
}