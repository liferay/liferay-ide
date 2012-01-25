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

package com.liferay.ide.eclipse.server.jboss.ui.command;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.command.ServerCommand;

import com.liferay.ide.eclipse.server.jboss.core.LiferayJBoss7Server;

/**
 * @author kamesh
 */
@SuppressWarnings( "restriction" )
public abstract class AbstractCommand extends ServerCommand
{

	protected IServer server;
	protected LiferayJBoss7Server liferayJBoss7Server;

	public AbstractCommand( IServerWorkingCopy server, String name )
	{
		super( server, name );
		this.server = (IServer) server;
		liferayJBoss7Server = ( (LiferayJBoss7Server) server.getAdapter( LiferayJBoss7Server.class ) );

	}

}
