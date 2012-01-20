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
 *******************************************************************************/

package com.liferay.ide.eclipse.server.jboss.core;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jst.common.project.facet.core.IClasspathProvider;
import org.eclipse.jst.server.core.internal.RuntimeClasspathProvider;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;

/**
 * @author kamesh
 */
@SuppressWarnings( { "rawtypes", "restriction" } )
public class LiferayJBossRuntimeClasspathFactory implements IAdapterFactory
{

	private static final Class[] ADAPTER_TYPES = { IClasspathProvider.class };

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	@Override
	public Object getAdapter( Object adaptableObject, Class adapterType )
	{
		IRuntimeComponent rc = (IRuntimeComponent) adaptableObject;

		return new RuntimeClasspathProvider( rc );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	@Override
	public Class[] getAdapterList()
	{
		return ADAPTER_TYPES;
	}

}
