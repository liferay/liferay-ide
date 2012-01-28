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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.core.server.internal.v7.LocalJBoss7StartLaunchDelegate;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class LiferayJBoss7StartLaunchDelegate extends LocalJBoss7StartLaunchDelegate {
	
	

	/*
	 * (non-Javadoc)
	 * @see
	 * org.jboss.ide.eclipse.as.core.server.internal.v7.LocalJBoss7StartLaunchDelegate#setupLaunchConfiguration(org.
	 * eclipse.debug.core.ILaunchConfigurationWorkingCopy, org.eclipse.wst.server.core.IServer)
	 */
	@Override
	public void setupLaunchConfiguration( ILaunchConfigurationWorkingCopy workingCopy, IServer server )
		throws CoreException {

		new LiferayJBoss7StartConfigurator( server ).configure( workingCopy );
	}
	
	

}
