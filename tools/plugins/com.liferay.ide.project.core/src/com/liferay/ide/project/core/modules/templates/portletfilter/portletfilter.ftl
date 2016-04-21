<#include "../basetemplate.ftl">

<#include "../component.ftl">

<#if ( supperclass == "GenericPortlet" ) >
public class ${classname} extends ${supperclass} {

	@Override
	protected void doView(RenderRequest request, RenderResponse response)
		throws IOException, PortletException {

		PrintWriter printWriter = response.getWriter();

		Object customAttr = request.getAttribute("CUSTOM_ATTRIBUTE");

		printWriter.print("Custom Attribute = " + customAttr);
	}
<#elseif ( supperclass == "RenderFilter" ) >
public class ${classname} implements ${supperclass} {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(
			RenderRequest request, RenderResponse response, FilterChain chain)
		throws IOException, PortletException {

		System.out.println("Before filter");
		request.setAttribute("CUSTOM_ATTRIBUTE", "My Custom Attribute Value");
		chain.doFilter(request, response);
		System.out.println("After filter");
	}

	@Override
	public void init(FilterConfig filterConfig) throws PortletException {
	}
</#if>
}