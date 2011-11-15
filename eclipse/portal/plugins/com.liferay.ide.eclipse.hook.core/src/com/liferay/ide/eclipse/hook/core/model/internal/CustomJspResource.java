package com.liferay.ide.eclipse.hook.core.model.internal;

import com.liferay.ide.eclipse.hook.core.model.ICustomJsp;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.modeling.BindingImpl;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.ValueBindingImpl;


public class CustomJspResource extends Resource
{
	private IPath path;

	public CustomJspResource( Resource parent, IPath jspPath )
	{
		super( parent );
		this.path = jspPath;
	}

	@Override
	protected BindingImpl createBinding( ModelProperty property )
	{
		if ( property == ICustomJsp.PROP_VALUE )
		{
			ValueBindingImpl binding = new ValueBindingImpl()
			{

				@Override
				public String read()
				{
					return getPath().toPortableString();
				}

				@Override
				public void write( final String value )
				{
					setPath( new Path( value ) );
				}
			};

			binding.init( element(), ICustomJsp.PROP_VALUE, null );

			return binding;
		}

		return null;
	}

	private IPath getPath()
	{
		return this.path;
	}

	private void setPath( IPath value )
	{
		this.path = value;
	}

}
