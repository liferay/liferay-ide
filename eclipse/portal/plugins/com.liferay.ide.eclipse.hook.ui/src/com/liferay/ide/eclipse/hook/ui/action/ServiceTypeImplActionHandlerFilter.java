package com.liferay.ide.eclipse.hook.ui.action;

import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFilter;


public class ServiceTypeImplActionHandlerFilter extends SapphireActionHandlerFilter
{

	@Override
	public boolean check( SapphireActionHandler handler )
	{
		return !( "Sapphire.Create.Java.Interface".equals( handler.getId() ) || "Sapphire.Browse.Java.Type".equals( handler.getId() ) );
	}

}
