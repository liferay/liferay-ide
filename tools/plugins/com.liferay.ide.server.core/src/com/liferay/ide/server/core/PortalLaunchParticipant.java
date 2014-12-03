/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay.ide.server.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;


/**
 * @author Gregory Amerson
 */
public interface PortalLaunchParticipant
{

    ISourceLookupParticipant[] getPortalSourceLookupParticipants();

    void portalPostLaunch( ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor )
        throws CoreException;

    void portalPreLaunch( ILaunchConfiguration configuration, String mode , ILaunch launch, IProgressMonitor monitor )
        throws CoreException;

    String provideVMArgs( ILaunchConfiguration configuration );

}
