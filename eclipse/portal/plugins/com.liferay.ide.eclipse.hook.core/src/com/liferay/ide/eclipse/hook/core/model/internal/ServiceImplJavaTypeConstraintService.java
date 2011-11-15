/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.eclipse.hook.core.model.internal;

import com.liferay.ide.eclipse.hook.core.model.IService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.java.internal.DefaultJavaTypeConstraintService;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;


/**
 * @author Gregory Amerson
 */
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

		return new ArrayList<String>();
	}

}
