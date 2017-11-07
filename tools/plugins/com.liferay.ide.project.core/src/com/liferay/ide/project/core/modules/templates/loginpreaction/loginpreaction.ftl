<#include "../basetemplate.ftl">

<#include "../component.ftl">

public class ${classname} implements ${supperclass} {

	@Override
	public void processLifecycleEvent(LifecycleEvent lifecycleEvent)
		throws ActionException {

		System.out.println("login.event.pre=" + lifecycleEvent);
	}

}