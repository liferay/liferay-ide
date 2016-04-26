<#include "../basetemplate.ftl">

<#include "../component.ftl">

public class ${classname} extends ${supperclass}<${simplemodelclass}> {

	@Override
	public void onBeforeCreate(${simplemodelclass} model) throws ModelListenerException {
		System.out.println(
			"About to create model: " + model.getPrimaryKey());
	}
}