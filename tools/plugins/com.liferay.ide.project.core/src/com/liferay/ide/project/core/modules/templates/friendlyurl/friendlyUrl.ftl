<#include "../basetemplate.ftl">

<#include "../component.ftl">

public class ${classname} extends ${supperclass} {

	@Override
	public String getMapping() {

		return _MAPPING;
	}

	private static final String _MAPPING = "${componentNameWithoutTemplateName}";

}