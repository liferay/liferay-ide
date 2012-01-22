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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IModule;
import org.jboss.ide.eclipse.as.core.publishers.LocalPublishMethod;
import org.jboss.ide.eclipse.as.core.server.IJBossServerPublishMethod;
import org.jboss.ide.eclipse.as.core.server.internal.v7.DelegatingJBoss7ServerBehavior;

import com.liferay.ide.eclipse.server.core.ILiferayServerBehavior;

/**
 * @author kamesh
 */
public class LiferayJBossServerBehavior extends DelegatingJBoss7ServerBehavior implements ILiferayServerBehavior
{

	/**
	 * 
	 */
	public LiferayJBossServerBehavior()
	{
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.liferay.ide.eclipse.server.core.ILiferayServerBehavior#redeployModule(org.eclipse.wst.server.core.IModule[])
	 */
	public void redeployModule( IModule[] module )
	{
		// TODO Auto-generated method stub

	}
}
