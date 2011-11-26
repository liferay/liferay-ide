package com.liferay.ide.eclipse.hook.core.model.internal;

import com.liferay.ide.eclipse.hook.core.model.IHook;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.ListBindingImpl;


public abstract class HookListBindingImpl extends ListBindingImpl
{

	protected IHook hook()
	{
		return this.element().nearest( IHook.class );
	}

	protected IProject project()
	{
		return this.hook().adapt( IProject.class );
	}
}
