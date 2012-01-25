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

import java.net.URL;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.ide.eclipse.as.core.extensions.descriptors.XPathModel;
import org.jboss.ide.eclipse.as.core.server.UnitedServerListenerManager;
import org.jboss.ide.eclipse.as.core.server.internal.v7.JBoss7Server;
import org.jboss.ide.eclipse.as.core.util.ServerUtil;

import com.liferay.ide.eclipse.server.core.ILiferayServer;

/**
 * @author kamesh
 */
public class LiferayJBoss7Server extends JBoss7Server implements ILiferayServer
{

	/**
	 * 
	 */
	public LiferayJBoss7Server()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.liferay.ide.eclipse.server.core.ILiferayServer#getPortalHomeUrl()
	 */
	public URL getPortalHomeUrl()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.liferay.ide.eclipse.server.core.ILiferayServer#getWebServicesListURL()
	 */
	public URL getWebServicesListURL()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
