package com.liferay.ide.eclipse.hook.core.model.internal;

import com.liferay.ide.eclipse.hook.core.model.IPortalProperty;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.modeling.ListBindingImpl;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Resource;


public class PortalPropertiesBindingImpl extends ListBindingImpl
{

	private List<Resource> portalPropertyResources;

	@Override
	public List<Resource> read()
	{
		this.portalPropertyResources = new ArrayList<Resource>();
		return this.portalPropertyResources;
	}

	@Override
	public ModelElementType type( Resource resource )
	{
		return IPortalProperty.TYPE;
	}

}
