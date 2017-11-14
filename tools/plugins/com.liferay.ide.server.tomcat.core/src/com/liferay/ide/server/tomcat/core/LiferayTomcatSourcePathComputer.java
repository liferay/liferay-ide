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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.jst.server.tomcat.core.internal.TomcatSourcePathComputerDelegate;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class LiferayTomcatSourcePathComputer extends TomcatSourcePathComputerDelegate {

	public static final String ID = "com.liferay.ide.server.tomcat.portalSourcePathComputer";

	@Override
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor)
		throws CoreException {

		ISourceContainer[] superContainers = super.computeSourceContainers(configuration, monitor);

		// add theme plugin _diffs folders

		List<ISourceContainer> containers = new ArrayList<>();

		Collections.addAll(containers, superContainers);

		return containers.toArray(new ISourceContainer[0]);
	}

}