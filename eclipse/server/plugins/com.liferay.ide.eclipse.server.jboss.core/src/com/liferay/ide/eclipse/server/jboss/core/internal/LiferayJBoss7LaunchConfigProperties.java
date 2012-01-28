/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.

 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.

 * Contributors:
 * Kamesh Sampath - initial implementation
 * Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.eclipse.server.jboss.core.internal;

import com.liferay.ide.eclipse.server.jboss.core.ILiferayJBossServer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.core.server.internal.v7.JBoss7LaunchConfigProperties;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class LiferayJBoss7LaunchConfigProperties extends JBoss7LaunchConfigProperties {

	protected IServer server;

	public LiferayJBoss7LaunchConfigProperties() {

	}

	public LiferayJBoss7LaunchConfigProperties( IServer server ) {
		this.server = server;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.jboss.ide.eclipse.as.core.server.internal.launch.configuration.JBossLaunchConfigProperties#getVMArguments
	 * (org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public String getVMArguments( ILaunchConfiguration launchConfig ) throws CoreException {
		//TODO how to get server from here ?? and get its meory arguments
		String vmArgs = server.getAttribute( ILiferayJBossServer.PROPERTY_MEMORY_ARGS, super.getVMArguments( launchConfig ) );
		return vmArgs;
	}
}
