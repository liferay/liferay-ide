package com.liferay.ide.eclipse.hook.core.model.internal;

import com.liferay.ide.eclipse.hook.core.model.IService;

import java.util.Arrays;
import java.util.List;

import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.java.internal.DefaultJavaTypeConstraintService;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;


@SuppressWarnings( "restriction" )
public class ServiceImplJavaTypeConstraintService extends DefaultJavaTypeConstraintService
{

	private IService service;

	@Override
	protected void init()
	{
		super.init();

		this.service = context( IService.class );

		this.service.addListener( new ModelPropertyListener()
		{

			@Override
			public void handlePropertyChangedEvent( ModelPropertyChangeEvent event )
			{
				ServiceImplJavaTypeConstraintService.this.broadcast();
			}
		}, "ServiceType" );
	}

	@Override
	public List<String> type()
	{
		JavaTypeName type = service.getServiceType().getContent( false );

		if ( type != null )
		{
			return Arrays.asList( new String[] { type.qualified() + "Wrapper" } );
		}

		return null;
	}

}
