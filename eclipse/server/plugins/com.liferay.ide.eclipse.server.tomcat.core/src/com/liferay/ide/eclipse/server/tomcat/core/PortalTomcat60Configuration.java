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

package com.liferay.ide.eclipse.server.tomcat.core;

import com.liferay.ide.eclipse.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.server.tomcat.core.internal.Tomcat60Configuration;
import org.eclipse.wst.server.core.IModule;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class PortalTomcat60Configuration extends Tomcat60Configuration implements IPortalTomcatConfiguration {

	public PortalTomcat60Configuration(IFolder path) {
		super(path);
	}

	@Override
	public IStatus publishContextConfig(IPath baseDir, IPath deployDir, IProgressMonitor monitor) {
		return super.publishContextConfig(baseDir, deployDir, monitor);
	}

	// TODO uncomment this once we have serve directly enabled in tomcat publishing
	// @Override
	// public IStatus updateContextsToServeDirectly(IPath baseDir, String loader, IProgressMonitor monitor) {
	// return super.updateContextsToServeDirectly(baseDir, loader, monitor);
	// }

	@Override
	protected String getWebModuleURL(IModule webModule) {
		if (webModule != null && ProjectUtil.isLiferayProject(webModule.getProject())) {
			return ""; // just go to portal root, no need to view the webapp
						// context url
		}

		return super.getWebModuleURL(webModule);
	}

}
