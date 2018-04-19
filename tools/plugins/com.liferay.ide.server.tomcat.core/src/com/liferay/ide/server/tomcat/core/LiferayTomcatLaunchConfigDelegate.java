/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
 */

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.PortalLaunchParticipant;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jst.server.tomcat.core.internal.TomcatLaunchConfigurationDelegate;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Tao Tao
 */
@SuppressWarnings("restriction")
public class LiferayTomcatLaunchConfigDelegate extends TomcatLaunchConfigurationDelegate {

	@Override
	public String getVMArguments(ILaunchConfiguration configuration) throws CoreException {
		String retval = super.getVMArguments(configuration);

		for (PortalLaunchParticipant participant : LiferayServerCore.getPortalLaunchParticipants()) {
			String vmArgs = participant.provideVMArgs(configuration);

			if (!CoreUtil.isNullOrEmpty(vmArgs)) {
				retval += vmArgs;
			}
		}

		return retval;
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
		throws CoreException {

		for (PortalLaunchParticipant participant : LiferayServerCore.getPortalLaunchParticipants()) {
			participant.portalPreLaunch(configuration, mode, launch, monitor);
		}

		super.launch(configuration, mode, launch, monitor);

		for (PortalLaunchParticipant participant : LiferayServerCore.getPortalLaunchParticipants()) {
			participant.portalPostLaunch(configuration, mode, launch, monitor);
		}
	}

}