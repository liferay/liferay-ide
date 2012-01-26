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
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.core.server.internal.v7.JBoss7LaunchConfigProperties;
import org.jboss.ide.eclipse.as.core.server.internal.v7.LocalJBoss7StartConfigurator;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class LiferayJBoss7StartConfigurator extends LocalJBoss7StartConfigurator {

	public LiferayJBoss7StartConfigurator( IServer server ) throws CoreException {
		super( server );
	}

	/* (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.internal.v7.LocalJBoss7StartConfigurator#createProperties()
	 */
	@Override
	protected JBoss7LaunchConfigProperties createProperties() {
		return new LiferayJBoss7LaunchConfigProperties();
	}
	
	

}
