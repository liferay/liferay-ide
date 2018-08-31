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

package com.liferay.ide.project.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 */
public class PortletClasspathContainer extends PluginClasspathContainer {

	public static final String SEGMENT_PATH = "portlet";

	public PortletClasspathContainer(
		IPath containerPath, IJavaProject project, IPath portalDir, String javadocURL, IPath sourcePath) {

		super(containerPath, project, portalDir, javadocURL, sourcePath);
	}

	public String getDescription() {
		return Msgs.liferayPortletPluginAPI;
	}

	@Override
	protected String[] getPortalJars() {
		return new String[] {
			"commons-logging.jar", "log4j.jar", "util-bridges.jar", "util-java.jar", "util-taglib.jar"
		};
	}

	private static class Msgs extends NLS {

		public static String liferayPortletPluginAPI;

		static {
			initializeMessages(PortletClasspathContainer.class.getName(), Msgs.class);
		}

	}

}